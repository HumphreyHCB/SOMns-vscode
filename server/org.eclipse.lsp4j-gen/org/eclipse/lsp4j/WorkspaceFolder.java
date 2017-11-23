/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.lsp4j;

import com.google.common.annotations.Beta;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@Beta
@SuppressWarnings("all")
public class WorkspaceFolder {
  /**
   * The associated URI for this workspace folder.
   */
  @NonNull
  private String uri;
  
  /**
   * The name of the workspace folder. Defaults to the uri's basename.
   */
  private String name;
  
  /**
   * The associated URI for this workspace folder.
   */
  @Pure
  @NonNull
  public String getUri() {
    return this.uri;
  }
  
  /**
   * The associated URI for this workspace folder.
   */
  public void setUri(@NonNull final String uri) {
    this.uri = uri;
  }
  
  /**
   * The name of the workspace folder. Defaults to the uri's basename.
   */
  @Pure
  public String getName() {
    return this.name;
  }
  
  /**
   * The name of the workspace folder. Defaults to the uri's basename.
   */
  public void setName(final String name) {
    this.name = name;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("uri", this.uri);
    b.add("name", this.name);
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
    WorkspaceFolder other = (WorkspaceFolder) obj;
    if (this.uri == null) {
      if (other.uri != null)
        return false;
    } else if (!this.uri.equals(other.uri))
      return false;
    if (this.name == null) {
      if (other.name != null)
        return false;
    } else if (!this.name.equals(other.name))
      return false;
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.uri== null) ? 0 : this.uri.hashCode());
    result = prime * result + ((this.name== null) ? 0 : this.name.hashCode());
    return result;
  }
}
