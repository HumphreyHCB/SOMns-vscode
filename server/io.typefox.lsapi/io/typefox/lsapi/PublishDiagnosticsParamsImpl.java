/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi;

import io.typefox.lsapi.DiagnosticImpl;
import io.typefox.lsapi.PublishDiagnosticsParams;
import java.util.List;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

/**
 * Diagnostics notification are sent from the server to the client to signal results of validation runs.
 */
@SuppressWarnings("all")
public class PublishDiagnosticsParamsImpl implements PublishDiagnosticsParams {
  /**
   * The URI for which diagnostic information is reported.
   */
  private String uri;
  
  @Pure
  @Override
  public String getUri() {
    return this.uri;
  }
  
  public void setUri(final String uri) {
    this.uri = uri;
  }
  
  /**
   * An array of diagnostic information items.
   */
  private List<DiagnosticImpl> diagnostics;
  
  @Pure
  @Override
  public List<DiagnosticImpl> getDiagnostics() {
    return this.diagnostics;
  }
  
  public void setDiagnostics(final List<DiagnosticImpl> diagnostics) {
    this.diagnostics = diagnostics;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("uri", this.uri);
    b.add("diagnostics", this.diagnostics);
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
    PublishDiagnosticsParamsImpl other = (PublishDiagnosticsParamsImpl) obj;
    if (this.uri == null) {
      if (other.uri != null)
        return false;
    } else if (!this.uri.equals(other.uri))
      return false;
    if (this.diagnostics == null) {
      if (other.diagnostics != null)
        return false;
    } else if (!this.diagnostics.equals(other.diagnostics))
      return false;
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.uri== null) ? 0 : this.uri.hashCode());
    result = prime * result + ((this.diagnostics== null) ? 0 : this.diagnostics.hashCode());
    return result;
  }
}