/* Generated By:JJTree: Do not edit this line. ASTMultiSetCondition.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=true,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package net.sourceforge.pmd.lang.plsql.ast;

public
class ASTMultiSetCondition extends net.sourceforge.pmd.lang.plsql.ast.AbstractPLSQLNode {
  public ASTMultiSetCondition(int id) {
    super(id);
  }

  public ASTMultiSetCondition(PLSQLParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(PLSQLParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=2d214b6e9b2247b0f5eaf78abdc8bd80 (do not edit this line) */
