/* Generated By:JJTree: Do not edit this line. ASTLTNode.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=true,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package net.sourceforge.pmd.lang.vm.ast;

public
class ASTLTNode extends net.sourceforge.pmd.lang.vm.ast.AbstractVmNode {
  public ASTLTNode(int id) {
    super(id);
  }

  public ASTLTNode(VmParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(VmParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=407945c5c2fa36a1eb74e4fc3fbd1a78 (do not edit this line) */