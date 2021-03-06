/* Generated By:JavaCC: Do not edit this line. PLSQLParserVisitor.java Version 5.0 */
package net.sourceforge.pmd.lang.plsql.ast;

public interface PLSQLParserVisitor
{
  public Object visit(PLSQLNode node, Object data);
  public Object visit(ASTInput node, Object data);
  public Object visit(ASTDDLCommand node, Object data);
  public Object visit(ASTSqlPlusCommand node, Object data);
  public Object visit(ASTGlobal node, Object data);
  public Object visit(ASTBlock node, Object data);
  public Object visit(ASTPackageSpecification node, Object data);
  public Object visit(ASTPackageBody node, Object data);
  public Object visit(ASTDeclarativeUnit node, Object data);
  public Object visit(ASTDeclarativeSection node, Object data);
  public Object visit(ASTCompilationDeclarationFragment node, Object data);
  public Object visit(ASTProgramUnit node, Object data);
  public Object visit(ASTObjectNameDeclaration node, Object data);
  public Object visit(ASTFormalParameter node, Object data);
  public Object visit(ASTMethodDeclaration node, Object data);
  public Object visit(ASTMethodDeclarator node, Object data);
  public Object visit(ASTFormalParameters node, Object data);
  public Object visit(ASTVariableOrConstantDeclarator node, Object data);
  public Object visit(ASTVariableOrConstantDeclaratorId node, Object data);
  public Object visit(ASTVariableOrConstantInitializer node, Object data);
  public Object visit(ASTDatatype node, Object data);
  public Object visit(ASTCompilationDataType node, Object data);
  public Object visit(ASTCollectionTypeName node, Object data);
  public Object visit(ASTScalarDataTypeName node, Object data);
  public Object visit(ASTDateTimeLiteral node, Object data);
  public Object visit(ASTExceptionHandler node, Object data);
  public Object visit(ASTSkip2NextTerminator node, Object data);
  public Object visit(ASTSkip2NextOccurrence node, Object data);
  public Object visit(ASTSkipPastNextOccurrence node, Object data);
  public Object visit(ASTSkip2NextTokenOccurrence node, Object data);
  public Object visit(ASTSkipPastNextTokenOccurrence node, Object data);
  public Object visit(ASTRead2NextOccurrence node, Object data);
  public Object visit(ASTReadPastNextOccurrence node, Object data);
  public Object visit(ASTSqlStatement node, Object data);
  public Object visit(ASTWrappedObject node, Object data);
  public Object visit(ASTUnlabelledStatement node, Object data);
  public Object visit(ASTStatement node, Object data);
  public Object visit(ASTLabelledStatement node, Object data);
  public Object visit(ASTCaseStatement node, Object data);
  public Object visit(ASTCaseWhenClause node, Object data);
  public Object visit(ASTElseClause node, Object data);
  public Object visit(ASTElsifClause node, Object data);
  public Object visit(ASTLoopStatement node, Object data);
  public Object visit(ASTForStatement node, Object data);
  public Object visit(ASTWhileStatement node, Object data);
  public Object visit(ASTIfStatement node, Object data);
  public Object visit(ASTForIndex node, Object data);
  public Object visit(ASTForAllIndex node, Object data);
  public Object visit(ASTForAllStatement node, Object data);
  public Object visit(ASTGotoStatement node, Object data);
  public Object visit(ASTReturnStatement node, Object data);
  public Object visit(ASTContinueStatement node, Object data);
  public Object visit(ASTExitStatement node, Object data);
  public Object visit(ASTRaiseStatement node, Object data);
  public Object visit(ASTCloseStatement node, Object data);
  public Object visit(ASTOpenStatement node, Object data);
  public Object visit(ASTFetchStatement node, Object data);
  public Object visit(ASTEmbeddedSqlStatement node, Object data);
  public Object visit(ASTPipelineStatement node, Object data);
  public Object visit(ASTConditionalCompilationStatement node, Object data);
  public Object visit(ASTSubTypeDefinition node, Object data);
  public Object visit(ASTFieldDeclaration node, Object data);
  public Object visit(ASTCollectionTypeDefinition node, Object data);
  public Object visit(ASTCollectionDeclaration node, Object data);
  public Object visit(ASTObjectDeclaration node, Object data);
  public Object visit(ASTCallSpecTail node, Object data);
  public Object visit(ASTCursorUnit node, Object data);
  public Object visit(ASTCursorSpecification node, Object data);
  public Object visit(ASTCursorBody node, Object data);
  public Object visit(ASTExpression node, Object data);
  public Object visit(ASTCompilationExpression node, Object data);
  public Object visit(ASTAssignment node, Object data);
  public Object visit(ASTCaseExpression node, Object data);
  public Object visit(ASTLikeExpression node, Object data);
  public Object visit(ASTTrimExpression node, Object data);
  public Object visit(ASTObjectExpression node, Object data);
  public Object visit(ASTConditionalOrExpression node, Object data);
  public Object visit(ASTConditionalAndExpression node, Object data);
  public Object visit(ASTEqualityExpression node, Object data);
  public Object visit(ASTRelationalExpression node, Object data);
  public Object visit(ASTAdditiveExpression node, Object data);
  public Object visit(ASTStringExpression node, Object data);
  public Object visit(ASTMultiplicativeExpression node, Object data);
  public Object visit(ASTUnaryExpression node, Object data);
  public Object visit(ASTUnaryExpressionNotPlusMinus node, Object data);
  public Object visit(ASTPrimaryExpression node, Object data);
  public Object visit(ASTPrimaryPrefix node, Object data);
  public Object visit(ASTPrimarySuffix node, Object data);
  public Object visit(ASTLiteral node, Object data);
  public Object visit(ASTStringLiteral node, Object data);
  public Object visit(ASTBooleanLiteral node, Object data);
  public Object visit(ASTNullLiteral node, Object data);
  public Object visit(ASTMultiSetCondition node, Object data);
  public Object visit(ASTNumericLiteral node, Object data);
  public Object visit(ASTLabel node, Object data);
  public Object visit(ASTName node, Object data);
  public Object visit(ASTQualifiedName node, Object data);
  public Object visit(ASTArguments node, Object data);
  public Object visit(ASTArgumentList node, Object data);
  public Object visit(ASTArgument node, Object data);
  public Object visit(ASTVariableOrConstantDeclaration node, Object data);
  public Object visit(ASTDatatypeDeclaration node, Object data);
  public Object visit(ASTPragma node, Object data);
  public Object visit(ASTInlinePragma node, Object data);
  public Object visit(ASTExceptionDeclaration node, Object data);
  public Object visit(ASTParallelClause node, Object data);
  public Object visit(ASTAccessibleByClause node, Object data);
  public Object visit(ASTTable node, Object data);
  public Object visit(ASTTableColumn node, Object data);
  public Object visit(ASTView node, Object data);
  public Object visit(ASTSynonym node, Object data);
  public Object visit(ASTDirectory node, Object data);
  public Object visit(ASTDatabaseLink node, Object data);
  public Object visit(ASTViewColumn node, Object data);
  public Object visit(ASTComment node, Object data);
  public Object visit(ASTTypeMethod node, Object data);
  public Object visit(ASTTypeSpecification node, Object data);
  public Object visit(ASTAlterTypeSpec node, Object data);
  public Object visit(ASTAttributeDeclaration node, Object data);
  public Object visit(ASTAttribute node, Object data);
  public Object visit(ASTPragmaClause node, Object data);
  public Object visit(ASTTriggerUnit node, Object data);
  public Object visit(ASTTriggerTimingPointSection node, Object data);
  public Object visit(ASTCompoundTriggerBlock node, Object data);
  public Object visit(ASTNonDMLTrigger node, Object data);
  public Object visit(ASTDDLEvent node, Object data);
  public Object visit(ASTDatabaseEvent node, Object data);
  public Object visit(ASTNonDMLEvent node, Object data);
  public Object visit(ASTAlterTrigger node, Object data);
  public Object visit(ASTKEYWORD_RESERVED node, Object data);
  public Object visit(ASTKEYWORD_UNRESERVED node, Object data);
  public Object visit(ASTID node, Object data);
  public Object visit(ASTUnqualifiedID node, Object data);
  public Object visit(ASTQualifiedID node, Object data);
  public Object visit(ASTTypeKeyword node, Object data);
  public Object visit(ASTJavaInterfaceClass node, Object data);
  public Object visit(ASTEqualsOldIDNewID node, Object data);
}
/* JavaCC - OriginalChecksum=1dc86ad1870e76448214fe8f325859ad (do not edit this line) */
