/* Generated By:JJTree: Do not edit this line. ASTDatatypeDeclaration.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=true,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package net.sourceforge.pmd.lang.plsql.ast;

public
class ASTDatatypeDeclaration extends net.sourceforge.pmd.lang.plsql.ast.AbstractPLSQLNode {
  public ASTDatatypeDeclaration(int id) {
    super(id);
  }

  public ASTDatatypeDeclaration(PLSQLParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(PLSQLParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=7ca2d04bb725d43c80833e0fe5654400 (do not edit this line) */
