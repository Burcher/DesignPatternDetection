/* Generated By:JJTree: Do not edit this line. ASTConditionalAndExpression.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=true,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package net.sourceforge.pmd.lang.plsql.ast;

public
class ASTConditionalAndExpression extends net.sourceforge.pmd.lang.plsql.ast.AbstractPLSQLNode {
  public ASTConditionalAndExpression(int id) {
    super(id);
  }

  public ASTConditionalAndExpression(PLSQLParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(PLSQLParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=c8c5e3c5c145fe7605c0b88742cac1cc (do not edit this line) */
