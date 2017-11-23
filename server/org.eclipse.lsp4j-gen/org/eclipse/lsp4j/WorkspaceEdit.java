/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.lsp4j;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.lsp4j.TextDocumentEdit;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

/**
 * A workspace edit represents changes to many resources managed in the workspace.
 * The edit should either provide `changes` or `documentChanges`.
 * If documentChanges are present they are preferred over `changes`
 * if the client can handle versioned document edits.
 */
@SuppressWarnings("all")
public class WorkspaceEdit {
  /**
   * Holds changes to existing resources.
   */
  private Map<String, List<TextEdit>> changes;
  
  /**
   * An array of `TextDocumentEdit`s to express changes to specific a specific
   * version of a text document. Whether a client supports versioned document
   * edits is expressed via `WorkspaceClientCapabilities.versionedWorkspaceEdit`.
   */
  private List<TextDocumentEdit> documentChanges;
  
  public WorkspaceEdit() {
    LinkedHashMap<String, List<TextEdit>> _linkedHashMap = new LinkedHashMap<String, List<TextEdit>>();
    this.changes = _linkedHashMap;
  }
  
  public WorkspaceEdit(final Map<String, List<TextEdit>> changes) {
    this.changes = changes;
  }
  
  public WorkspaceEdit(final List<TextDocumentEdit> documentChanges) {
    this.documentChanges = documentChanges;
  }
  
  /**
   * @deprecated According to the protocol documentation, it doesn't make sense to send both
   * 		changes and documentChanges
   */
  @Deprecated
  public WorkspaceEdit(final Map<String, List<TextEdit>> changes, final List<TextDocumentEdit> documentChanges) {
    this.changes = changes;
    this.documentChanges = documentChanges;
  }
  
  /**
   * Holds changes to existing resources.
   */
  @Pure
  public Map<String, List<TextEdit>> getChanges() {
    return this.changes;
  }
  
  /**
   * Holds changes to existing resources.
   */
  public void setChanges(final Map<String, List<TextEdit>> changes) {
    this.changes = changes;
  }
  
  /**
   * An array of `TextDocumentEdit`s to express changes to specific a specific
   * version of a text document. Whether a client supports versioned document
   * edits is expressed via `WorkspaceClientCapabilities.versionedWorkspaceEdit`.
   */
  @Pure
  public List<TextDocumentEdit> getDocumentChanges() {
    return this.documentChanges;
  }
  
  /**
   * An array of `TextDocumentEdit`s to express changes to specific a specific
   * version of a text document. Whether a client supports versioned document
   * edits is expressed via `WorkspaceClientCapabilities.versionedWorkspaceEdit`.
   */
  public void setDocumentChanges(final List<TextDocumentEdit> documentChanges) {
    this.documentChanges = documentChanges;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("changes", this.changes);
    b.add("documentChanges", this.documentChanges);
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
    WorkspaceEdit other = (WorkspaceEdit) obj;
    if (this.changes == null) {
      if (other.changes != null)
        return false;
    } else if (!this.changes.equals(other.changes))
      return false;
    if (this.documentChanges == null) {
      if (other.documentChanges != null)
        return false;
    } else if (!this.documentChanges.equals(other.documentChanges))
      return false;
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.changes== null) ? 0 : this.changes.hashCode());
    result = prime * result + ((this.documentChanges== null) ? 0 : this.documentChanges.hashCode());
    return result;
  }
}
