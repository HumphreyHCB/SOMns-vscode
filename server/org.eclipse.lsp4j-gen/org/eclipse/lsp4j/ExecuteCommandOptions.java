/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.lsp4j;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

/**
 * Execute command options.
 */
@SuppressWarnings("all")
public class ExecuteCommandOptions {
  /**
   * The commands to be executed on the server
   */
  @NonNull
  private List<String> commands;
  
  public ExecuteCommandOptions() {
    this(new ArrayList<String>());
  }
  
  public ExecuteCommandOptions(@NonNull final List<String> commands) {
    this.commands = commands;
  }
  
  /**
   * The commands to be executed on the server
   */
  @Pure
  @NonNull
  public List<String> getCommands() {
    return this.commands;
  }
  
  /**
   * The commands to be executed on the server
   */
  public void setCommands(@NonNull final List<String> commands) {
    this.commands = commands;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("commands", this.commands);
    return b.toString();
  }
  
  @Override
  @Pure
  public boolean equals(final Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ExecuteCommandOptions other = (ExecuteCommandOptions) obj;
    if (this.commands == null) {
      if (other.commands != null)
        return false;
    } else if (!this.commands.equals(other.commands))
      return false;
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.commands== null) ? 0 : this.commands.hashCode());
    return result;
  }
}
