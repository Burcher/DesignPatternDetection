/* Generated By:JJTree: Do not edit this line. ASTExpression.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=true,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package net.sourceforge.pmd.lang.vm.ast;

public
class ASTExpression extends net.sourceforge.pmd.lang.vm.ast.AbstractVmNode {
  public ASTExpression(int id) {
    super(id);
  }

  public ASTExpression(VmParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(VmParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=d98cb80bbb581b9e53e8b800831f0d6f (do not edit this line) */
