/* Generated By:JJTree: Do not edit this line. ASTReadPastNextOccurrence.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=true,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package net.sourceforge.pmd.lang.plsql.ast;

public
class ASTReadPastNextOccurrence extends net.sourceforge.pmd.lang.plsql.ast.AbstractPLSQLNode {
  public ASTReadPastNextOccurrence(int id) {
    super(id);
  }

  public ASTReadPastNextOccurrence(PLSQLParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(PLSQLParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=4c340a58a0c66eed68218f80dc18106a (do not edit this line) */
