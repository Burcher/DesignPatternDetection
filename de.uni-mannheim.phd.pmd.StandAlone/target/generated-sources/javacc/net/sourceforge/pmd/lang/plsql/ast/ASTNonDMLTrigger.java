/* Generated By:JJTree: Do not edit this line. ASTNonDMLTrigger.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=true,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package net.sourceforge.pmd.lang.plsql.ast;

public
class ASTNonDMLTrigger extends net.sourceforge.pmd.lang.plsql.ast.AbstractPLSQLNode {
  public ASTNonDMLTrigger(int id) {
    super(id);
  }

  public ASTNonDMLTrigger(PLSQLParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(PLSQLParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=01ea13243fb760b4c5368b5569513b4b (do not edit this line) */
