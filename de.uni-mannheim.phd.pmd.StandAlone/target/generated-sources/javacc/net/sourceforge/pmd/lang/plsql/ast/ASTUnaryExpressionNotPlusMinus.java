/* Generated By:JJTree: Do not edit this line. ASTUnaryExpressionNotPlusMinus.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=true,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package net.sourceforge.pmd.lang.plsql.ast;

public
class ASTUnaryExpressionNotPlusMinus extends net.sourceforge.pmd.lang.plsql.ast.AbstractPLSQLNode {
  public ASTUnaryExpressionNotPlusMinus(int id) {
    super(id);
  }

  public ASTUnaryExpressionNotPlusMinus(PLSQLParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(PLSQLParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=afca1012d633823bb1bc7f46dbaea3c6 (do not edit this line) */
