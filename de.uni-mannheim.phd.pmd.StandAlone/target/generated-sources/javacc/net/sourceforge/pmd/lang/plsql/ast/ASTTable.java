/* Generated By:JJTree: Do not edit this line. ASTTable.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=true,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package net.sourceforge.pmd.lang.plsql.ast;

public
class ASTTable extends net.sourceforge.pmd.lang.plsql.ast.AbstractPLSQLNode {
  public ASTTable(int id) {
    super(id);
  }

  public ASTTable(PLSQLParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(PLSQLParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=c6ec312fc38f60fe3f04a8c3acbeea97 (do not edit this line) */
