/* Generated By:JJTree: Do not edit this line. ASTBlock.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=true,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package net.sourceforge.pmd.lang.plsql.ast;

public
class ASTBlock extends net.sourceforge.pmd.lang.plsql.ast.AbstractPLSQLNode {
  public ASTBlock(int id) {
    super(id);
  }

  public ASTBlock(PLSQLParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(PLSQLParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=d2add5d66a5496d91e8a778a673fa54d (do not edit this line) */