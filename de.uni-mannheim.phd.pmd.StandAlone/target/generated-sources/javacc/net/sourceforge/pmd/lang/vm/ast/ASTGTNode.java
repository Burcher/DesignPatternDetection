/* Generated By:JJTree: Do not edit this line. ASTGTNode.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=true,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package net.sourceforge.pmd.lang.vm.ast;

public
class ASTGTNode extends net.sourceforge.pmd.lang.vm.ast.AbstractVmNode {
  public ASTGTNode(int id) {
    super(id);
  }

  public ASTGTNode(VmParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(VmParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=9ed3f4602f18fd0a1a2543ca34ba3280 (do not edit this line) */
