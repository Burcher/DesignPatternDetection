/* Generated By:JJTree: Do not edit this line. ASTAccessibleByClause.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=true,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package net.sourceforge.pmd.lang.plsql.ast;

public
class ASTAccessibleByClause extends net.sourceforge.pmd.lang.plsql.ast.AbstractPLSQLNode {
  public ASTAccessibleByClause(int id) {
    super(id);
  }

  public ASTAccessibleByClause(PLSQLParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(PLSQLParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=745f6f9563339b1567a0c72cdf8c521a (do not edit this line) */
