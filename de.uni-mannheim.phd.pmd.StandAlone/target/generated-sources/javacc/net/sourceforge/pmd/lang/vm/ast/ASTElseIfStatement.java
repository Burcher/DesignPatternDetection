/* Generated By:JJTree: Do not edit this line. ASTElseIfStatement.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=true,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package net.sourceforge.pmd.lang.vm.ast;

public
class ASTElseIfStatement extends net.sourceforge.pmd.lang.vm.ast.AbstractVmNode {
  public ASTElseIfStatement(int id) {
    super(id);
  }

  public ASTElseIfStatement(VmParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(VmParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=bbe2ea8dcd89efa9eb7f82231c3abd02 (do not edit this line) */
