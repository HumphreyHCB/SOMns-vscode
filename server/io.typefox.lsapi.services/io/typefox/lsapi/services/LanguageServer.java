/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi.services;

import io.typefox.lsapi.InitializeParams;
import io.typefox.lsapi.InitializeResult;
import io.typefox.lsapi.services.TextDocumentService;
import io.typefox.lsapi.services.WindowService;
import io.typefox.lsapi.services.WorkspaceService;
import java.util.concurrent.CompletableFuture;

/**
 * Interface for implementations of
 * https://github.com/Microsoft/vscode-languageserver-protocol
 */
@SuppressWarnings("all")
public interface LanguageServer {
  /**
   * The initialize request is sent as the first request from the client to the server.
   */
  public abstract CompletableFuture<InitializeResult> initialize(final InitializeParams params);
  
  /**
   * The shutdown request is sent from the client to the server. It asks the server to shutdown, but to not exit
   * (otherwise the response might not be delivered correctly to the client). There is a separate exit notification
   * that asks the server to exit.
   */
  public abstract void shutdown();
  
  /**
   * A notification to ask the server to exit its process.
   */
  public abstract void exit();
  
  /**
   * Provides access to the textDocument services.
   */
  public abstract TextDocumentService getTextDocumentService();
  
  /**
   * Provides access to the workspace services.
   */
  public abstract WorkspaceService getWorkspaceService();
  
  /**
   * Provides access to the window services.
   */
  public abstract WindowService getWindowService();
}
