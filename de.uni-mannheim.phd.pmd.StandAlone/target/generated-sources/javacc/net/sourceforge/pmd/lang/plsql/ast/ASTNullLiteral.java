/* Generated By:JJTree: Do not edit this line. ASTNullLiteral.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=true,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package net.sourceforge.pmd.lang.plsql.ast;

public
class ASTNullLiteral extends net.sourceforge.pmd.lang.plsql.ast.AbstractPLSQLNode {
  public ASTNullLiteral(int id) {
    super(id);
  }

  public ASTNullLiteral(PLSQLParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(PLSQLParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=a068a7057e25b128af65049285065f1c (do not edit this line) */
