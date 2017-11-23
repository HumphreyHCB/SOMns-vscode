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
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

/**
 * Representation of a range and highlighting style identifiers that should be
 * highlighted based on the underlying model.
 */
@SuppressWarnings("all")
public class ColoringInformation {
  /**
   * The range that should be highlighted on the client-side.
   */
  @NonNull
  private Range range;
  
  /**
   * A list of highlighting styles, that should be applied on
   * the range. Several styles could be merged on the client-side by
   * applying all styles on the range.
   */
  @NonNull
  private List<Integer> styles;
  
  public ColoringInformation() {
    ArrayList<Integer> _arrayList = new ArrayList<Integer>();
    this.styles = _arrayList;
  }
  
  public ColoringInformation(final Range range, final List<Integer> styles) {
    this.range = range;
    this.styles = styles;
  }
  
  /**
   * The range that should be highlighted on the client-side.
   */
  @Pure
  @NonNull
  public Range getRange() {
    return this.range;
  }
  
  /**
   * The range that should be highlighted on the client-side.
   */
  public void setRange(@NonNull final Range range) {
    this.range = range;
  }
  
  /**
   * A list of highlighting styles, that should be applied on
   * the range. Several styles could be merged on the client-side by
   * applying all styles on the range.
   */
  @Pure
  @NonNull
  public List<Integer> getStyles() {
    return this.styles;
  }
  
  /**
   * A list of highlighting styles, that should be applied on
   * the range. Several styles could be merged on the client-side by
   * applying all styles on the range.
   */
  public void setStyles(@NonNull final List<Integer> styles) {
    this.styles = styles;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("range", this.range);
    b.add("styles", this.styles);
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
    ColoringInformation other = (ColoringInformation) obj;
    if (this.range == null) {
      if (other.range != null)
        return false;
    } else if (!this.range.equals(other.range))
      return false;
    if (this.styles == null) {
      if (other.styles != null)
        return false;
    } else if (!this.styles.equals(other.styles))
      return false;
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.range== null) ? 0 : this.range.hashCode());
    result = prime * result + ((this.styles== null) ? 0 : this.styles.hashCode());
    return result;
  }
}
