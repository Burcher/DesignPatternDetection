/* Generated By:JJTree: Do not edit this line. ASTExceptionDeclaration.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=true,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package net.sourceforge.pmd.lang.plsql.ast;

public
class ASTExceptionDeclaration extends net.sourceforge.pmd.lang.plsql.ast.AbstractPLSQLNode {
  public ASTExceptionDeclaration(int id) {
    super(id);
  }

  public ASTExceptionDeclaration(PLSQLParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(PLSQLParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=f020a5762847b5b18c03e223fc4bf032 (do not edit this line) */
