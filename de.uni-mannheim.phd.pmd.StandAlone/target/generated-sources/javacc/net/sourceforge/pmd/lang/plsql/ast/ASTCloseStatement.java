/* Generated By:JJTree: Do not edit this line. ASTCloseStatement.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=true,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package net.sourceforge.pmd.lang.plsql.ast;

public
class ASTCloseStatement extends net.sourceforge.pmd.lang.plsql.ast.AbstractPLSQLNode {
  public ASTCloseStatement(int id) {
    super(id);
  }

  public ASTCloseStatement(PLSQLParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(PLSQLParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=6a5da24c8fe57ff8e7c5b3f47a7c8a20 (do not edit this line) */
