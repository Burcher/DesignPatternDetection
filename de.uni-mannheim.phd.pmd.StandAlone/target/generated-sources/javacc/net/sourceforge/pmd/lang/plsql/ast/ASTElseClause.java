/* Generated By:JJTree: Do not edit this line. ASTElseClause.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=true,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package net.sourceforge.pmd.lang.plsql.ast;

public
class ASTElseClause extends net.sourceforge.pmd.lang.plsql.ast.AbstractPLSQLNode {
  public ASTElseClause(int id) {
    super(id);
  }

  public ASTElseClause(PLSQLParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(PLSQLParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=5c489cc3732fa061837e1378d9751827 (do not edit this line) */
