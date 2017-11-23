/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.lsp4j;

import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.SymbolKind;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

/**
 * Represents information about programming constructs like variables, classes, classs etc.
 */
@SuppressWarnings("all")
public class SymbolInformation {
  /**
   * The name of this symbol.
   */
  @NonNull
  private String name;
  
  /**
   * The kind of this symbol.
   */
  @NonNull
  private SymbolKind kind;
  
  /**
   * The location of this symbol.
   */
  @NonNull
  private Location location;
  
  /**
   * The name of the symbol containing this symbol.
   */
  private String containerName;
  
  public SymbolInformation() {
  }
  
  public SymbolInformation(@NonNull final String name, @NonNull final SymbolKind kind, @NonNull final Location location) {
    this.name = name;
    this.kind = kind;
    this.location = location;
  }
  
  public SymbolInformation(@NonNull final String name, @NonNull final SymbolKind kind, @NonNull final Location location, final String containerName) {
    this.name = name;
    this.kind = kind;
    this.location = location;
    this.containerName = containerName;
  }
  
  /**
   * The name of this symbol.
   */
  @Pure
  @NonNull
  public String getName() {
    return this.name;
  }
  
  /**
   * The name of this symbol.
   */
  public void setName(@NonNull final String name) {
    this.name = name;
  }
  
  /**
   * The kind of this symbol.
   */
  @Pure
  @NonNull
  public SymbolKind getKind() {
    return this.kind;
  }
  
  /**
   * The kind of this symbol.
   */
  public void setKind(@NonNull final SymbolKind kind) {
    this.kind = kind;
  }
  
  /**
   * The location of this symbol.
   */
  @Pure
  @NonNull
  public Location getLocation() {
    return this.location;
  }
  
  /**
   * The location of this symbol.
   */
  public void setLocation(@NonNull final Location location) {
    this.location = location;
  }
  
  /**
   * The name of the symbol containing this symbol.
   */
  @Pure
  public String getContainerName() {
    return this.containerName;
  }
  
  /**
   * The name of the symbol containing this symbol.
   */
  public void setContainerName(final String containerName) {
    this.containerName = containerName;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("name", this.name);
    b.add("kind", this.kind);
    b.add("location", this.location);
    b.add("containerName", this.containerName);
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
    SymbolInformation other = (SymbolInformation) obj;
    if (this.name == null) {
      if (other.name != null)
        return false;
    } else if (!this.name.equals(other.name))
      return false;
    if (this.kind == null) {
      if (other.kind != null)
        return false;
    } else if (!this.kind.equals(other.kind))
      return false;
    if (this.location == null) {
      if (other.location != null)
        return false;
    } else if (!this.location.equals(other.location))
      return false;
    if (this.containerName == null) {
      if (other.containerName != null)
        return false;
    } else if (!this.containerName.equals(other.containerName))
      return false;
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.name== null) ? 0 : this.name.hashCode());
    result = prime * result + ((this.kind== null) ? 0 : this.kind.hashCode());
    result = prime * result + ((this.location== null) ? 0 : this.location.hashCode());
    result = prime * result + ((this.containerName== null) ? 0 : this.containerName.hashCode());
    return result;
  }
}
