/* Generated By:JJTree: Do not edit this line. ASTAlterTypeSpec.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=true,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package net.sourceforge.pmd.lang.plsql.ast;

public
class ASTAlterTypeSpec extends net.sourceforge.pmd.lang.plsql.ast.AbstractPLSQLNode {
  public ASTAlterTypeSpec(int id) {
    super(id);
  }

  public ASTAlterTypeSpec(PLSQLParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(PLSQLParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=02228e6cb7fa6a62c6ed392d4ec9cb8d (do not edit this line) */
