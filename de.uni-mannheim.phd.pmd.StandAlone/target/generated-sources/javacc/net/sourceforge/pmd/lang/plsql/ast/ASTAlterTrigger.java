/* Generated By:JJTree: Do not edit this line. ASTAlterTrigger.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=true,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package net.sourceforge.pmd.lang.plsql.ast;

public
class ASTAlterTrigger extends net.sourceforge.pmd.lang.plsql.ast.AbstractPLSQLNode {
  public ASTAlterTrigger(int id) {
    super(id);
  }

  public ASTAlterTrigger(PLSQLParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(PLSQLParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=1febd54d7bcb3d3d7e946f7cfad7e5d4 (do not edit this line) */
