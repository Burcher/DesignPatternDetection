/* Generated By:JJTree: Do not edit this line. ASTMap.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=true,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package net.sourceforge.pmd.lang.vm.ast;

public
class ASTMap extends net.sourceforge.pmd.lang.vm.ast.AbstractVmNode {
  public ASTMap(int id) {
    super(id);
  }

  public ASTMap(VmParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(VmParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=e4a1b6c625256790874f6ed11517eeff (do not edit this line) */
