/*
 * VariableGridLayout.java - a grid layout manager with variable cell sizes
 * :tabSize=4:indentSize=4:noTabs=false:
 *
 * Originally written by Dirk Moebius for the jEdit project. This work has been
 * placed into the public domain. You may use this work in any way and for any
 * purpose you wish.
 *
 * THIS SOFTWARE IS PROVIDED AS-IS WITHOUT WARRANTY OF ANY KIND, NOT EVEN THE
 * IMPLIED WARRANTY OF MERCHANTABILITY. THE AUTHOR OF THIS SOFTWARE, ASSUMES
 * _NO_ RESPONSIBILITY FOR ANY CONSEQUENCE RESULTING FROM THE USE, MODIFICATION,
 * OR REDISTRIBUTION OF THIS SOFTWARE.
 */

package de.de.uma.phd.test.state;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;

import java.util.Arrays;

/** A rectangular grid layout manager with variable cell sizes
 *
 * The container is divided into rectangles, and one component is placed
 * in each rectangle. Each row is as large as the largest component in
 * that row, and each column is as wide as the widest component in
 * that column.<p>
 *
 * This behavior is basically the same as in
 * <code>java.awt.GridLayout</code>, but with different row heights and
 * column widths for each row/column.<p>
 *
 * For example, the following is an applet that lays out six buttons
 * into three rows and two columns:<p>
 *
 * <blockquote><pre>
 * import java.awt.*;
 * import java.applet.Applet;
 * public class ButtonGrid extends Applet {
 *     public void init() {
 *         setLayout(new VariableGridLayout(VariableGridLayout.FIXED_NUM_COLUMNS, 2));
 *         add(new Button("1"));
 *         add(new Button("2"));
 *         add(new Button("3"));
 *         add(new Button("4"));
 *         add(new Button("5"));
 *         add(new Button("6"));
 *     }
 * }
 * </pre></blockquote><p>
 *
 * <b>Programmer's remark:</b> VariableGridLayout could be faster, if it would
 * reside in the package java.awt, because then it could access some
 * package private fields of <code>Container</code> or
 * <code>Component</code>. Instead, it has to call
 * <code>Component.getSize()</code>,
 * which allocates memory on the heap.<p>
 *
 * <b>Todo:</b>
 * <ul>
 * <li>Ability to span components over more than one cell horizontally and vertically.
 * </ul>
 *
 * @author Dirk Moebius, BjÃ¶rn "Vampire" Kautler
 * @version 1.5
 * @see java.awt.GridLayout
 */
public class State implements LayoutManager2, java.io.Serializable
{
	public static final int FIXED_NUM_ROWS = 1;
	public static final int FIXED_NUM_COLUMNS = 2;

	private static enum LayoutSize { MINIMUM, MAXIMUM, PREFERRED }
	
	private  LayoutSize which;
	private Dimension getLayoutSize(Container parent)
	{
		synchronized (parent.getTreeLock())
		{

						switch (which)
						{
							case MINIMUM:
								which = LayoutSize.MINIMUM;
								row_height = Math.max(row_height, parent.getComponent(i).getMinimumSize().height);
								break;

							case MAXIMUM:
								which = LayoutSize.MINIMUM;;
								break;

							case PREFERRED:
								which = LayoutSize.MINIMUM;;
								break;

							default:
								which = LayoutSize.MINIMUM;
								throw new InternalError("Missing case branch for LayoutSize: " + which);
						}


			return new Dimension((int)w,(int)h);
		}
	}
	
	private Dimension getLayoutSize2(Container parent)
	{
		synchronized (parent.getTreeLock())
		{
				
						switch (which)
						{
							case MINIMUM:
								col_width = Math.max(col_width, parent.getComponent(i).getMinimumSize().width);
								break;

							case MAXIMUM:
								col_width = Math.max(col_width, parent.getComponent(i).getMaximumSize().width);
								break;

							case PREFERRED:
								col_width = Math.max(col_width, parent.getComponent(i).getPreferredSize().width);
								break;

							default:
								throw new InternalError("Missing case branch for LayoutSize: " + which);
						}

			return new Dimension((int)w,(int)h);
		}
	}
	
    //Mögliche Launen der Freudin
    private static final int NEUTRAL = 0;
    private static final int BOCKIG = 1;
    private static final int FROEHLICH = 2;
    
    //Zahl repräsentiert aktuellen Zustand
    private int aktuellerZustand;
    private Object state;
	
    public void unterhalten(){
    	switch (state.hashCode()) {
		case NEUTRAL:
			System.out.println("test.");
			state.hashCode();
			break;
		case BOCKIG:
			state.hashCode();
			break;
		case FROEHLICH:
			state.hashCode();
			break;
    	}
    }
    
    public void aendern(){
    	switch (state.hashCode()) {
		case NEUTRAL:
			state.hashCode();
			break;
		case BOCKIG:
			state.hashCode();
			break;
		case FROEHLICH:
			state.hashCode();
			break;
    	}
    }

}
