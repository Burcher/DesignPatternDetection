/* Generated By:JJTree: Do not edit this line. ASTSubTypeDefinition.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=true,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package net.sourceforge.pmd.lang.plsql.ast;

public
class ASTSubTypeDefinition extends net.sourceforge.pmd.lang.plsql.ast.AbstractPLSQLNode {
  public ASTSubTypeDefinition(int id) {
    super(id);
  }

  public ASTSubTypeDefinition(PLSQLParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(PLSQLParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=936d35b88d2a8277721c86e5d18efd0e (do not edit this line) */
