/* Generated By:JJTree: Do not edit this line. ASTRelationalExpression.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=true,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package net.sourceforge.pmd.lang.plsql.ast;

public
class ASTRelationalExpression extends net.sourceforge.pmd.lang.plsql.ast.AbstractPLSQLNode {
  public ASTRelationalExpression(int id) {
    super(id);
  }

  public ASTRelationalExpression(PLSQLParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(PLSQLParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=e3e42d5d5f041d509e357b0b90b49736 (do not edit this line) */
