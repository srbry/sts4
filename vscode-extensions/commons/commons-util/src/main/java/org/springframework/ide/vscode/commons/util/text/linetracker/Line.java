/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.springframework.ide.vscode.commons.util.text.linetracker;

import org.springframework.ide.vscode.commons.util.text.IRegion;

/**
 * Describes a line as a particular number of characters beginning at
 * a particular offset, consisting of a particular number of characters,
 * and being closed with a particular line delimiter.
 */
final class Line implements IRegion {

	/** The offset of the line */
	public int offset;
	/** The length of the line */
	public int length;
	/** The delimiter of this line */
	public final String delimiter;

	/**
	 * Creates a new Line.
	 *
	 * @param offset the offset of the line
	 * @param end the last including character offset of the line
	 * @param delimiter the line's delimiter
	 */
	public Line(int offset, int end, String delimiter) {
		this.offset= offset;
		this.length= (end - offset) +1;
		this.delimiter= delimiter;
	}

	/**
	 * Creates a new Line.
	 *
	 * @param offset the offset of the line
	 * @param length the length of the line
	 */
	public Line(int offset, int length) {
		this.offset= offset;
		this.length= length;
		this.delimiter= null;
	}

	@Override
	public int getOffset() {
		return offset;
	}

	@Override
	public int getLength() {
		return length;
	}
}


