"use strict";

import { spawn, ChildProcess } from 'child_process';
import { BreakpointEvent, DebugSession, Handles, InitializedEvent, Scope,
  Source as VSSource, StackFrame, StoppedEvent, Variable, OutputEvent,
  TerminatedEvent } from 'vscode-debugadapter';
import { DebugProtocol } from 'vscode-debugprotocol';
import * as WebSocket from 'ws';

import { BreakpointData, Source as WDSource, Respond, SuspendEventMessage,
  InitialBreakpointsResponds, SourceMessage, IdMap, StoppedMessage,
  StackTraceResponse, StackTraceRequest, ScopesRequest, ScopesResponse,
  StepMessage, StepType, VariablesRequest, VariablesResponse, ThreadsResponse,
  createLineBreakpointData } from './messages';

export interface LaunchRequestArguments extends DebugProtocol.LaunchRequestArguments {
  /** Path to the main program */
  program: string;

  /** Arguments to the main program */
  args?: string[];

  /** Workding directory */
  cwd: string;

  /** Automatically stop after launch */
  stopOnEntry?: boolean;

  /** Path to the SOMns interpreter */
  runtime?: string;

  /** Optional interpreter arguments */
  runtimeArgs?: string[];
}

export interface AttachRequestArguments extends DebugProtocol.AttachRequestArguments {
  /** Debugger port of the interpreter */
  port: number
}

interface BreakpointPair {
  vs:  DebugProtocol.Breakpoint;
  som: BreakpointData;
}

class SomDebugSession extends DebugSession {
  private socket: WebSocket;
  private somProc: ChildProcess;

  private breakpoints: IdMap<IdMap<BreakpointPair>>;
  private nextBreakpointId: number;
  private bufferedBreakpoints: BreakpointPair[];
  private sources: IdMap<WDSource>;

  private nextRequestId: number;
  private requests: IdMap<any>;

  private varHandles : Handles<string>;

  public constructor() {
    super();

    this.socket = null;

    this.breakpoints = {};
    this.nextBreakpointId = 0;
    this.bufferedBreakpoints = [];

    this.sources = {};

    this.nextRequestId = 0;
    this.requests = {};

    this.varHandles = new Handles<string>();

    // Truffle source sections use 1-based indexes
    this.setDebuggerColumnsStartAt1(true);
    this.setDebuggerLinesStartAt1(true);
  }

  protected initializeRequest(response: DebugProtocol.InitializeResponse,
      args: DebugProtocol.InitializeRequestArguments): void {
    this.sendEvent(new InitializedEvent());

    response.body.supportsConfigurationDoneRequest = true;
    response.body.supportsEvaluateForHovers = true;
    response.body.supportsRestartFrame = true;
    response.body.supportsStepBack = false; // TODO: figure out how to support

    // TODO: https://github.com/Microsoft/vscode-debugadapter-node/pull/44
    // response.body.supportsStepInTargetsRequest

    this.sendResponse(response);
  }

  protected launchRequest(response: DebugProtocol.LaunchResponse,
      args: LaunchRequestArguments): void {
    const options = {cwd: args.cwd};
    let somArgs = ['-G', '-wd', args.program];
    if (args.runtimeArgs) {
      somArgs = args.runtimeArgs.concat(somArgs);
    }
    if (args.args) {
      somArgs = somArgs.concat(args.args);
    }
    const cmdStr = args.runtime + ' ' + somArgs.join(' ');
    this.sendEvent(new OutputEvent(`cmd: ${cmdStr}`, 'console'));

    this.somProc = spawn(args.runtime, somArgs, options);
    let connecting = false;

    this.somProc.stdout.on('data', (data) => {
      const str = data.toString();
      this.sendEvent(new OutputEvent(str, 'stdout'));
      if (str.includes("Started HTTP Server") && !connecting) {
        this.connectDebugger(response, 7977);
      }
    });
    this.somProc.stderr.on('data', (data) => {
      const str = data.toString();
      this.sendEvent(new OutputEvent(str, 'stderr'));
    });
    this.somProc.on('close', code => {
      this.sendEvent(new TerminatedEvent());
    })
  }

  protected attachRequest(response: DebugProtocol.AttachResponse,
      args: AttachRequestArguments): void {
    this.connectDebugger(response, args.port);
  }

  protected disconnectRequest(response: DebugProtocol.DisconnectResponse,
    args: DebugProtocol.DisconnectArguments): void {
    this.somProc.kill();
    this.sendResponse(response);
  }

  private connectDebugger(response: DebugProtocol.Response, port: number): void {
    this.socket = new WebSocket('ws://localhost:' + port);

    this.socket.on('open', () => {
      this.sendInitialBreakpoints();
    });

    this.socket.onmessage = this.onWDMessage.bind(this);

    this.sendResponse(response);
  }

  private onWDMessage(event): void {
    const data = JSON.parse(event.data);

    switch (data.type) {
      case "source":
        this.onSource(data);
        break;
      case "StackTraceResponse": {
        console.assert(this.requests[data.requestId]);
        const response = this.requests[data.requestId];
        delete this.requests[data.requestId];
        this.onProgramStackTraceResponse(data, response);
        break;
      }
      case "ScopesResponse": {
        console.assert(this.requests[data.requestId]);
        const response = this.requests[data.requestId];
        delete this.requests[data.requestId];
        this.onProgramScopesResponse(data, response);
        break;
      }
      case "VariablesResponse": {
        console.assert(this.requests[data.requestId]);
        const response = this.requests[data.requestId];
        delete this.requests[data.requestId];
        this.onProgramVariablesResponse(data, response);
      }
      case "StoppedEvent":
        this.onStoppedEvent(data);
        break;
      case "ThreadsResponse":
        const response = this.requests[data.requestId];
        delete this.requests[data.requestId];
        this.onThreadsResponse(data, response);
        break;
    }
  }

  private onSource(data: SourceMessage): void {
    for (let source of data.sources) {
      this.sources[source.uri] = source;
    }
  }

  private onStoppedEvent(data: StoppedMessage): void {
    this.sendEvent(new StoppedEvent(data.reason, data.activityId, data.text));
  }

  private sendInitialBreakpoints() : void {
    const updatedBreakpoints = [];
    const initialBreakpoints = [];

    for (const bpP of this.bufferedBreakpoints) {
      bpP.vs.verified = true;
      updatedBreakpoints.push(bpP.vs);
      initialBreakpoints.push(bpP.som);
    }
    this.bufferedBreakpoints.length = 0;
    const msg: InitialBreakpointsResponds = {
        action: "initialBreakpoints", breakpoints: initialBreakpoints,
        debuggerProtocol: true};
    this.send(msg);

    for (const vsBp of updatedBreakpoints) {
      this.sendEvent(new BreakpointEvent('Connection Established', vsBp));
    }
  }

  protected setBreakPointsRequest(response: DebugProtocol.SetBreakpointsResponse,
      args: DebugProtocol.SetBreakpointsArguments): void {
    const connected = this.socket != null;
    const uri = 'file:' + args.source.path;
    const breakpoints = [];
    response.body = {breakpoints: breakpoints};

    // the `args` contain the latest list of line breakpoints for a uri
    // this means, we need to figure out which breakpoints were added, and which
    // were removed

    if (!this.breakpoints[uri]) { this.breakpoints[uri] = {}; }
    const currentBPs = this.breakpoints[uri];
    const removedBPs = Object.assign({}, currentBPs);

    for (let bp of args.breakpoints) {
      if (currentBPs[bp.line]) {
        // already exists, so, it is unchanged and not the removed list
        delete removedBPs[bp.line];
      } else {
        const lineBp = createLineBreakpointData(uri, bp.line, true);
        const vsBp = {
          id:       this.getNextBpId(),
          verified: connected,
          source:   args.source,
          line:     bp.line
        }
        const bpP = {vs: vsBp, som: lineBp};
        currentBPs[bp.line] = bpP;
        this.sendBreakpoint(currentBPs[bp.line], connected);
      }
    }

    // have not seen these breakpoint sin args.breakpoints, so they got disabled
    for (let line in removedBPs) {
      const bpP = removedBPs[line];
      bpP.som.enabled = false;
      this.sendBreakpoint(bpP, connected);
    }

    // that's the current list of bps, which we return to the IDE
    for (let line in currentBPs) {
      const bpP = currentBPs[line];
      breakpoints.push(bpP.vs);
    }
    this.sendResponse(response);
  }

  private getNextBpId(): number {
    const id = this.nextBreakpointId;
    this.nextBreakpointId += 1;
    return id;
  }

  private send(respond: Respond) {
    if (this.socket) {
      this.socket.send(JSON.stringify(respond));
    }
  }

  private sendBreakpoint(bpP: BreakpointPair, connected: boolean): void {
    if (connected) {
      this.send({
        action: "updateBreakpoint",
        breakpoint: bpP.som
      });
    } else {
      this.bufferedBreakpoints.push(bpP);
    }
  }

  protected threadsRequest(response: DebugProtocol.ThreadsResponse): void {
    this.requests[this.nextRequestId] = response;
    this.send({action: "ThreadsRequest", requestId: this.nextRequestId});
    this.nextRequestId += 1;
  }

  private onThreadsResponse(data: ThreadsResponse,
      ideResponse: DebugProtocol.ThreadsResponse) {
    ideResponse.body = { threads: data.threads };
    console.log(data.threads);
    this.sendResponse(ideResponse);
  }

  private vsSourceFromUri(uri: string): VSSource {
    const s = this.sources[uri];
    let source;
    if (s) {
      source = new VSSource(s.name, s.uri.substring("file:".length));
    } else {
      source = new VSSource('vmMirror', null, undefined, 'internal module');
    }
    return source;
  }

  private onProgramStackTraceResponse(data: StackTraceResponse,
      ideResponse: DebugProtocol.StackTraceResponse) {
    const frames = [];
    for (let frame of data.stackFrames) {
      if (frame.sourceUri) {
        frames.push(new StackFrame(frame.id, frame.name,
          this.vsSourceFromUri(frame.sourceUri), frame.line, frame.column));
      } else {
        frames.push(new StackFrame(frame.id, frame.name))
      }
    }

    ideResponse.body = {
      stackFrames: frames,
      totalFrames: data.totalFrames
    };
    this.sendResponse(ideResponse);
  }

  protected stackTraceRequest(response: DebugProtocol.StackTraceResponse,
      args: DebugProtocol.StackTraceArguments): void {
    const request: StackTraceRequest = {
      action: "StackTraceRequest",
      activityId: args.threadId,
      startFrame: args.startFrame,
      levels:     args.levels,
      requestId:  this.nextRequestId};

    this.requests[this.nextRequestId] = response;
    this.nextRequestId += 1;

    this.send(request);
  }

  protected scopesRequest(response: DebugProtocol.ScopesResponse,
      args: DebugProtocol.ScopesArguments): void {
    const request: ScopesRequest = {
      action: "ScopesRequest",
      frameId: args.frameId,
      requestId:  this.nextRequestId};

    this.requests[this.nextRequestId] = response;
    this.nextRequestId += 1;

    this.send(request);
  }

  protected onProgramScopesResponse(data: ScopesResponse,
    response: DebugProtocol.ScopesResponse): void {

    const scopes = [];
    for (const scope of data.scopes) {
      scopes.push(new Scope(scope.name, scope.variablesReference, scope.expensive));
    }
    response.body = { scopes: scopes };
    this.sendResponse(response);
  }

  protected variablesRequest(response: DebugProtocol.VariablesResponse,
      args: DebugProtocol.VariablesArguments): void {
    const request: VariablesRequest = {
      action: "VariablesRequest",
      requestId: this.nextRequestId,
      variablesReference: args.variablesReference
    }

    this.requests[this.nextRequestId] = response;
    this.nextRequestId += 1;

    this.send(request);
  }

  protected onProgramVariablesResponse(data: VariablesResponse,
    response: DebugProtocol.VariablesResponse): void {
    response.body = { variables: data.variables };
    this.sendResponse(response);
  }

  private sendStep(stepType: StepType, response, args) {
    const step: StepMessage = {action: stepType, suspendEvent: "se-" + args.threadId};
    this.send(step);
    response.body = {allThreadsContinued: false};
    this.sendResponse(response);
  }

  protected nextRequest(response: DebugProtocol.NextResponse,
      args: DebugProtocol.NextArguments): void {
    this.sendStep("stepOver", response, args);
  }

  protected continueRequest(response: DebugProtocol.ContinueResponse,
      args: DebugProtocol.ContinueArguments): void {
    this.sendStep("resume", response, args);
  }

  protected stepInRequest(response: DebugProtocol.StepInResponse,
      args: DebugProtocol.StepInArguments): void {
    this.sendStep("stepInto", response, args);
  }

  protected stepOutRequest(response: DebugProtocol.StepOutResponse,
      args: DebugProtocol.StepOutArguments): void {
    this.sendStep("return", response, args);
  }

  protected pauseRequest(response: DebugProtocol.PauseResponse,
      args: DebugProtocol.PauseArguments): void {
    this.sendStep("stop", response, args);
  }
}

DebugSession.run(SomDebugSession);
