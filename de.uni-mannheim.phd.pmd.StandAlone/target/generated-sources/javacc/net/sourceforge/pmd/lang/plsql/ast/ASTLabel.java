/* Generated By:JJTree: Do not edit this line. ASTLabel.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=true,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package net.sourceforge.pmd.lang.plsql.ast;

public
class ASTLabel extends net.sourceforge.pmd.lang.plsql.ast.AbstractPLSQLNode {
  public ASTLabel(int id) {
    super(id);
  }

  public ASTLabel(PLSQLParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(PLSQLParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=95995d346fcf13885cd3dcc3e84bf098 (do not edit this line) */
