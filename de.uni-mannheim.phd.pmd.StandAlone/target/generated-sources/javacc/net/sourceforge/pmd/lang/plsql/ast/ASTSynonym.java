/* Generated By:JJTree: Do not edit this line. ASTSynonym.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=true,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package net.sourceforge.pmd.lang.plsql.ast;

public
class ASTSynonym extends net.sourceforge.pmd.lang.plsql.ast.AbstractPLSQLNode {
  public ASTSynonym(int id) {
    super(id);
  }

  public ASTSynonym(PLSQLParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(PLSQLParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=ccd42b074ebb00f7fb7c5c20042731d4 (do not edit this line) */
