/* Generated By:JJTree: Do not edit this line. ASTSetDirective.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=true,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package net.sourceforge.pmd.lang.vm.ast;

public
class ASTSetDirective extends net.sourceforge.pmd.lang.vm.ast.AbstractVmNode {
  public ASTSetDirective(int id) {
    super(id);
  }

  public ASTSetDirective(VmParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(VmParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=49e6426a2cfba8c99c27e5e852874894 (do not edit this line) */
