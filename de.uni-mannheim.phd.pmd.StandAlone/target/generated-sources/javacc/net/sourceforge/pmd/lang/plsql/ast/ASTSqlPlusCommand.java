/* Generated By:JJTree: Do not edit this line. ASTSqlPlusCommand.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=true,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package net.sourceforge.pmd.lang.plsql.ast;

public
class ASTSqlPlusCommand extends net.sourceforge.pmd.lang.plsql.ast.AbstractPLSQLNode {
  public ASTSqlPlusCommand(int id) {
    super(id);
  }

  public ASTSqlPlusCommand(PLSQLParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(PLSQLParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=cb925abfad83eec095b19d1581777f54 (do not edit this line) */