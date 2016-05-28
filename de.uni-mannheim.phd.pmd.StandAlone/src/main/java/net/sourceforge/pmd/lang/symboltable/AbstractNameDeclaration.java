/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.symboltable;


/**
 * Base class for all name declarations.
 */
public abstract class AbstractNameDeclaration implements NameDeclaration {

    protected ScopedNode node;

    public AbstractNameDeclaration(ScopedNode node) {
        this.node = node;
    }


    public ScopedNode getNode() {
        return node;
    }


    public String getImage() {
        return node.getImage();
    }


    public Scope getScope() {
        return node.getScope();
    }


    public String getName() {
        return getImage();
    }
}
