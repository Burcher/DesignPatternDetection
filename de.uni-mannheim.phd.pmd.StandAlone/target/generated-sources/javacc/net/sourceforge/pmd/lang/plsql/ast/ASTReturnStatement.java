/* Generated By:JJTree: Do not edit this line. ASTReturnStatement.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=true,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package net.sourceforge.pmd.lang.plsql.ast;

public
class ASTReturnStatement extends net.sourceforge.pmd.lang.plsql.ast.AbstractPLSQLNode {
  public ASTReturnStatement(int id) {
    super(id);
  }

  public ASTReturnStatement(PLSQLParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(PLSQLParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=80af24ae7646deaa02d54344fb94a565 (do not edit this line) */
