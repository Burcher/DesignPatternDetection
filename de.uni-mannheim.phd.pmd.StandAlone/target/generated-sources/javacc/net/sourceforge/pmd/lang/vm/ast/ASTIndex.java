/* Generated By:JJTree: Do not edit this line. ASTIndex.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=true,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package net.sourceforge.pmd.lang.vm.ast;

public
class ASTIndex extends net.sourceforge.pmd.lang.vm.ast.AbstractVmNode {
  public ASTIndex(int id) {
    super(id);
  }

  public ASTIndex(VmParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(VmParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=ab135f0e6b55c94492ede6d37e8f1b47 (do not edit this line) */
