/*
 * All files in the distribution of BLOAT (Bytecode Level Optimization and
 * Analysis tool for Java(tm)) are Copyright 1997-2001 by the Purdue
 * Research Foundation of Purdue University.  All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms are permitted
 * provided that this entire copyright notice is duplicated in all
 * such copies, and that any documentation, announcements, and other
 * materials related to such distribution and use acknowledge that the
 * software was developed at Purdue University, West Lafayette, IN by
 * Antony Hosking, David Whitlock, and Nathaniel Nystrom.  No charge
 * may be made for copies, derivations, or distributions of this
 * material without the express written consent of the copyright
 * holder.  Neither the name of the University nor the name of the
 * author may be used to endorse or promote products derived from this
 * material without specific prior written permission.  THIS SOFTWARE
 * IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR ANY PARTICULAR PURPOSE.
 *
 * <p>
 * Java is a trademark of Sun Microsystems, Inc.
 */

package EDU.purdue.cs.bloat.tree;

import java.util.*;

import EDU.purdue.cs.bloat.editor.*;

/**
 * <tt>OperandStack</tt> is used to simulate the JVM stack. A stack of
 * expressions is maintained. <tt>OperandStack</tt> has methods to push and
 * pop (both wide and non-wide) expressions, replace an expression at a given
 * depth in the stack, peek into the stack, etc.
 * 
 * @see Expr
 * @see Tree
 */
public class OperandStack {
	ArrayList stack; // The contents of the stack

	int height; // The height of the stack (i.e. the number

	// of elements in the stack)

	/**
	 * Constructor.
	 */
	public OperandStack() {
		stack = new ArrayList();
		height = 0;
	}

	/**
	 * @return True, if the stack is empty.
	 */
	public boolean isEmpty() {
		return stack.isEmpty();
	}

	/**
	 * Pops an operand off the stack. Checks to make sure the top of the stack
	 * is of the expected Type.
	 * 
	 * @param type
	 *            The expected Type of the top of the stack
	 * @return Expression on the top of the stack
	 */
	public Expr pop(final Type type) {
		final Expr top = (Expr) stack.remove(stack.size() - 1);

		final Type topType = top.type();

		height -= topType.stackHeight();

		if (type.isAddress()) {
			if (!topType.isAddress()) {
				throw new IllegalArgumentException("Expected " + type
						+ ", stack = " + toString());
			}
		} else if (type.isReference()) {
			if (!topType.isReference()) {
				throw new IllegalArgumentException("Expected " + type
						+ ", stack = " + toString());
			}
		} else if (type.isIntegral()) {
			if (!topType.isIntegral()) {
				throw new IllegalArgumentException("Expected " + type
						+ ", stack = " + toString());
			}
		} else if (!type.equals(topType)) {
			throw new IllegalArgumentException("Expected " + type
					+ ", stack = " + toString());
		}

		return top;
	}

	/**
	 * Returns the expression at the top of the stack, but does not modify the
	 * stack.
	 */
	public Expr peek() {
		return (Expr) stack.get(stack.size() - 1);
	}

	/**
	 * Sets the entry at a specified index from the bottom of the stack
	 * 
	 * @param index
	 *            The position in the stack.
	 * @param expr
	 *            The new value of the expression.
	 */
	public void set(final int index, final Expr expr) {
		stack.set(index, expr);
	}

	/**
	 * @return The number of elements in the stack.
	 */
	public int height() {
		return height;
	}

	/**
	 * Replaces the expression that is depth expressions from the top of the
	 * stack.
	 * 
	 * @param depth
	 *            The number of expressions from the top of the stack.
	 * 
	 * @param expr
	 *            The new expression
	 */
	public void replace(int depth, final Expr expr) {
		for (int i = stack.size() - 1; i >= 0; i--) {
			final Expr top = (Expr) stack.get(i);

			if (depth == 0) {
				stack.set(i, expr);
				return;
			}

			depth -= top.type().stackHeight();
		}

		throw new IllegalArgumentException("Can't replace below stack bottom.");
	}

	/**
	 * Get the expression that is depth expressions from the top of the stack,
	 * but do not modify the stack.
	 * 
	 * @param depth
	 *            Number of expressions deep to get.
	 * 
	 * @return The expression that is depth expression from the top of the
	 *         stack.
	 */
	public Expr peek(int depth) {
		for (int i = stack.size() - 1; i >= 0; i--) {
			final Expr top = (Expr) stack.get(i);
			if (depth == 0) {
				return top;
			}

			depth -= top.type().stackHeight();
		}

		throw new IllegalArgumentException("Can't peek below stack bottom.");
	}

	/**
	 * Pops a non-wide expression off the stack.
	 */
	public Expr pop1() {
		final Expr top = (Expr) stack.remove(stack.size() - 1);

		final Type type = top.type();

		if (type.isWide()) {
			throw new IllegalArgumentException("Expected a word "
					+ ", got a long");
		}

		height--;

		return top;
	}

	/**
	 * Pops a (possibly) wide expression off of the stack and returns the result
	 * as an array of <tt>Expr</tt>. If the expression at the top of the
	 * stack is indeed wide, it is returned in element [0] of the array. If the
	 * expression at the top of the stack is <b>not</b> wide, the top two
	 * expressions are returned in the array as elements 0 and 1.
	 */
	public Expr[] pop2() {
		final Expr top = (Expr) stack.remove(stack.size() - 1);
		Expr[] a;

		final Type type = top.type();

		if (type.isWide()) {
			a = new Expr[1];
			a[0] = top;
		} else {
			a = new Expr[2];
			a[0] = (Expr) stack.remove(stack.size() - 1);
			a[1] = top;
		}

		height -= 2;

		return a;
	}

	/**
	 * Push an expression onto the stack.
	 * 
	 * @see EDU.purdue.cs.bloat.editor.Type#stackHeight
	 */
	public void push(final Expr expr) {
		height += expr.type().stackHeight();
		stack.add(expr);
	}

	/**
	 * Returns the number of expressions on the stack.
	 */
	public int size() {
		return stack.size();
	}

	/**
	 * Returns the expression at index from the bottom of the stack.
	 */
	public Expr get(final int index) {
		final Expr expr = (Expr) stack.get(index);
		return expr;
	}

	/**
	 * Returns a String represntation of this stack.
	 */
	public String toString() {
		return stack.toString();
	}
}
