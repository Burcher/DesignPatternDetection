/* Generated By:JJTree: Do not edit this line. ASTCursorSpecification.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=true,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package net.sourceforge.pmd.lang.plsql.ast;

public
class ASTCursorSpecification extends net.sourceforge.pmd.lang.plsql.ast.AbstractPLSQLNode {
  public ASTCursorSpecification(int id) {
    super(id);
  }

  public ASTCursorSpecification(PLSQLParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(PLSQLParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=cbcce6c65bb690b60e5ed1f84619c44e (do not edit this line) */