package net.sourceforge.pmd.lang.dpc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.dpc.database.Candidate;
import net.sourceforge.pmd.lang.dpc.database.CandidateDAO;
import net.sourceforge.pmd.lang.dpc.database.DBConnector;
import net.sourceforge.pmd.lang.dpc.database.Parameter;
import net.sourceforge.pmd.lang.dpc.rules.RuleManager;
import net.sourceforge.pmd.lang.ecmascript.ast.ASTBreakStatement;
import net.sourceforge.pmd.lang.java.ast.ASTAllocationExpression;
import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.java.ast.ASTBooleanLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExtendsList;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameters;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPackageDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimitiveType;
import net.sourceforge.pmd.lang.java.ast.ASTReferenceType;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLabel;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.AbstractJavaAccessNode;
import net.sourceforge.pmd.lang.java.ast.AbstractJavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.plsql.ast.ASTPackageBody;
import net.sourceforge.pmd.lang.plsql.ast.ASTPackageSpecification;

public class DesignPatternDetection extends AbstractJavaRule {
	static final Logger log = Logger.getRootLogger();

	private CandidateDAO candidateCollector = new CandidateDAO();
	private String currentPackageName = null;
	private String packageAttributName = new String("currentPackage");

	// private ASTPrimaryExpression expression;

	@Override
	public void start(RuleContext ctx) {

		try {
			SimpleLayout layout = new SimpleLayout();

			ConsoleAppender consoleAppender = new ConsoleAppender(layout);
			log.addAppender(consoleAppender);
			Appender fileAppender = new FileAppender(layout,
					"logs/MeineLogDatei.log", false);
			log.addAppender(fileAppender);
			// ALL | DEBUG | INFO | WARN | ERROR | FATAL | OFF:
			log.setLevel(Level.WARN);
		} catch (Exception ex) {
			System.out.println(ex);
		}
		// ctx.setAttribute("currentPackage", new String());
		System.out.println("Start Data Collection");
		super.start(ctx);
	}

	public Object visit(ASTPackageDeclaration packageNode, Object data) {
		packageNode.childrenAccept(this, data);
		Candidate candidate = new Candidate();
		RuleContext result = (RuleContext) data;

		currentPackageName = packageNode.getFirstChildOfType(ASTName.class)
				.getImage();
		result.setAttribute(packageAttributName, currentPackageName);
		candidate.setPackageName(currentPackageName);
		if (candidate.getPackageName() != null) {
			candidateCollector.addPackage(candidate);
		}
		return result;
	}

	public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
		node.childrenAccept(this, data);
		// TODO unterschied Klasse und Interface einf�gen.
		Candidate candidate = new Candidate();
		RuleContext result = (RuleContext) data;

		candidate.setActiveClass(true);
		candidate.setClassName(node.getImage());
		candidate.setPackageName(node
				.getParentsOfType(ASTCompilationUnit.class).get(0)
				.findChildrenOfType(ASTPackageDeclaration.class).get(0)
				.getFirstChildOfType(ASTName.class).getImage());
		// Suche nach vererbten Klassen
		if (node.getFirstChildOfType(ASTExtendsList.class) != null) {
			if (node.getFirstChildOfType(ASTExtendsList.class)
					.getFirstChildOfType(ASTClassOrInterfaceType.class) != null) {
				candidate.setExtendedClassName(node
						.getFirstChildOfType(ASTExtendsList.class)
						.getFirstChildOfType(ASTClassOrInterfaceType.class)
						.getImage());
			}
		}

		candidateCollector.addClass(candidate);
		return result;
	}

	public Object visit(ASTConstructorDeclaration method, Object data) {
		method.childrenAccept(this, data);
		Candidate candidate = new Candidate();
		RuleContext result = (RuleContext) data;
		// Hole den Klassenamen f�r den aktuellen Kontruktor.
		
		if (method.getParentsOfType(ASTClassOrInterfaceDeclaration.class)
				.size() == 0) {
			candidate.setClassName(method
					.getParentsOfType(ASTEnumDeclaration.class).get(0)
					.getImage());
		} else {
			candidate.setClassName(method
					.getParentsOfType(ASTClassOrInterfaceDeclaration.class)
					.get(0).getImage());
		}
//		
//		candidate.setClassName(method
//				.getParentsOfType(ASTClassOrInterfaceDeclaration.class).get(0)
//				.getImage());
		candidate.setMethodName(candidate.getClassName());
		// Hole Package Name der aktuelle Klasse
		candidate.setPackageName(method
				.getParentsOfType(ASTCompilationUnit.class).get(0)
				.findChildrenOfType(ASTPackageDeclaration.class).get(0)
				.getFirstChildOfType(ASTName.class).getImage());
		candidate.setConstructor(true);
		// Hole �bergabeparameter aus dem AST.
		this.extraxtParameter(method, candidate);
		// Alle Daten in die Datenbank
		candidateCollector.addMethode(candidate);
		return result;
	}

	public Object visit(ASTMethodDeclaration method, Object data) {
		method.childrenAccept(this, data);
		Candidate candidate = new Candidate();
		RuleContext result = (RuleContext) data;
		// Hole Methodennamen aus Node. Geht leider nicht anders. Anfrage im
		// Forum gestelt.
		String methodeName = ((ASTMethodDeclarator) ((ASTMethodDeclaration) method)
				.findChildrenOfType(ASTMethodDeclarator.class).get(0))
				.getImage();
		candidate.setMethodName(methodeName);
		// Hole zugehoerigen Klassennamen vom Eltern Knoten
		if (method.getParentsOfType(ASTClassOrInterfaceDeclaration.class)
				.size() == 0) {
			candidate.setClassName(method
					.getParentsOfType(ASTEnumDeclaration.class).get(0)
					.getImage());
		} else {
			candidate.setClassName(method
					.getParentsOfType(ASTClassOrInterfaceDeclaration.class)
					.get(0).getImage());
		}
//		
//		candidate.setClassName(method
//				.getParentsOfType(ASTClassOrInterfaceDeclaration.class).get(0)
//				.getImage());

		candidate.setPackageName(method
				.getParentsOfType(ASTCompilationUnit.class).get(0)
				.findChildrenOfType(ASTPackageDeclaration.class).get(0)
				.getFirstChildOfType(ASTName.class).getImage());
		// Hole Parameter der Methode.
		this.extraxtParameter(method, candidate);

		// Uebergebe das ganze an den Collector (die Datenbank).
		candidateCollector.addMethode(candidate);
		return result;
	}

	private void extraxtParameter(AbstractJavaAccessNode method,
			Candidate candidate) {
		if (method.hasDescendantOfType(ASTFormalParameter.class) == true) {

			List<ASTFormalParameter> parameterListe = method
					.findDescendantsOfType(ASTFormalParameters.class).get(0)
					.findChildrenOfType(ASTFormalParameter.class);
			for (Iterator<ASTFormalParameter> iterator = parameterListe
					.iterator(); iterator.hasNext();) {
				Parameter parameter = new Parameter();
				ASTFormalParameter astFormalParameter = (ASTFormalParameter) iterator
						.next();
				parameter.setType(astFormalParameter.getFirstChildOfType(
						ASTType.class).getTypeImage());
				parameter.setName(astFormalParameter.getFirstChildOfType(
						ASTVariableDeclaratorId.class).getImage());
				candidate.addParameterToList(parameter);
			}
		}
	}

	/**
	 * This Visitor visits every Attribute declaration in a class.
	 * 
	 * @param context
	 *            Attribute or Field
	 * @param data
	 *            contains information about the rule
	 * @return rules violation information
	 */
	public Object visit(ASTFieldDeclaration context, Object data) {
		context.childrenAccept(this, data);
		Candidate candidate = new Candidate();
		RuleContext result = (RuleContext) data;

		if (context.getParentsOfType(ASTClassOrInterfaceDeclaration.class)
				.size() == 0) {
			candidate.setClassName(context
					.getParentsOfType(ASTEnumDeclaration.class).get(0)
					.getImage());
		} else {
			candidate.setClassName(context
					.getParentsOfType(ASTClassOrInterfaceDeclaration.class)
					.get(0).getImage());
		}

		candidate.setPackageName(context
				.getParentsOfType(ASTCompilationUnit.class).get(0)
				.findChildrenOfType(ASTPackageDeclaration.class).get(0)
				.getFirstChildOfType(ASTName.class).getImage());
		candidate.setAttributeName(context
				.getFirstChildOfType(ASTVariableDeclarator.class)
				.getFirstChildOfType(ASTVariableDeclaratorId.class).getImage());
		if (context.findDescendantsOfType(ASTPrimitiveType.class).size() == 0) {
			candidate.setAttributeType(context
					.getFirstChildOfType(ASTType.class)
					.getFirstChildOfType(ASTReferenceType.class)
					.getFirstChildOfType(ASTClassOrInterfaceType.class)
					.getImage());
		} else {
			candidate.setAttributeType(context
					.findDescendantsOfType(ASTPrimitiveType.class).get(0)
					.getImage());
		}
		this.getRelatePackageImport(context, candidate);
		candidate.setStartLine(context.getBeginLine());
		candidateCollector.addAttribute(candidate, 2);
		return result;
	}

	private void getRelatePackageImport(AbstractJavaAccessNode context,
			Candidate candidate) {
		ArrayList<ASTImportDeclaration> imports = (ArrayList<ASTImportDeclaration>) (context
				.getParentsOfType(ASTCompilationUnit.class).get(0))
				.findChildrenOfType(ASTImportDeclaration.class);

		for (Iterator<ASTImportDeclaration> iterator = imports.iterator(); iterator
				.hasNext();) {
			ASTImportDeclaration astImportDeclaration = (ASTImportDeclaration) iterator
					.next();
			if (astImportDeclaration.getImportedName().contains(
					candidate.getAttributeType()) == true) {
				candidate.setAttributePackage(astImportDeclaration
						.getPackageName());
				break;
			}
		}
	}

	/**
	 * Factory method to convert a given primary expression into MethodCalls. In
	 * case the primary expression represents a method chain call, then multiple
	 * MethodCalls are returned.
	 * 
	 * @author PMD based on PMD Rule LawOfDemeterRule
	 * @return a list of MethodCalls, might be empty.
	 */
	public Object visit(ASTPrimaryExpression expression, Object data) {
		String baseName = "unknown";
		boolean islocal = false;

		Candidate candidate = new Candidate();
		RuleContext result = (RuleContext) data;
		// List<MethodCall> result = new ArrayList<MethodCall>();
		// wenn die Expression keinen Nachfolger vom Type AllocationExpression
		// hat
		// und wenn das ganze keine Grundtype (Literal z.B. Integer) ist
		// und wenn der Knoten Argument und ob this oder Super vorne stehen hat.
		if (expression.getFirstChildOfType(ASTAllocationExpression.class) == null
				&& isNotLiteral(expression)) {
			// Pruefe Suffix auf Parameter
			if (expression.hasDescendantOfType(ASTArgumentList.class) == true) {
				Parameter parameter = null;
				ASTArgumentList parameterListe = expression
						.findDescendantsOfType(ASTArgumentList.class).get(0);
				List<ASTLiteral> literalList = parameterListe
						.findDescendantsOfType(ASTLiteral.class);
				List<ASTName> objectList = parameterListe
						.findDescendantsOfType(ASTName.class);

				for (int i = 0; i < literalList.size(); i++) {
					parameter = checkLiteralType(literalList, i);
					candidate.addMethodAttributToList(parameter);
				}
				for (int i = 0; i < objectList.size(); i++) {
					parameter = new Parameter();
					parameter.setName(objectList.get(i).getImage());
					candidate.addMethodAttributToList(parameter);
				}
			}

			// Hole den Knoten PrimaryPrefix.
			ASTPrimaryPrefix prefixNode = expression
					.getFirstDescendantOfType(ASTPrimaryPrefix.class);
			// Dieser Teil extrahiert den Attributennahmen und den
			// Ziel-Methodennamen.
			List<ASTName> names = prefixNode
					.findDescendantsOfType(ASTName.class);
			baseName = "unknown";
			if (!names.isEmpty()) {
				baseName = names.get(0).getImage();

				if (names.get(0).getType() != null) {
					String classType[] = names.get(0).getType().getName()
							.split("\\.");
					candidate.setAttributeType(classType[classType.length - 1]);
				}
				candidate.setMethodInvocationName(baseName);
				int indexOfDot = baseName.lastIndexOf('.');
				if (indexOfDot == -1) {
					candidate.setMethodInvocationClassType(baseName);
					candidate.setAttributeName("THIS");
					islocal = true;

				} else {
					candidate.setMethodInvocationCalledMethodName(baseName
							.substring(indexOfDot + 1));
					candidate.setAttributeName(baseName
							.substring(0, indexOfDot));
				}
			} else {
				if (prefixNode.usesThisModifier()) {
					candidate.setAttributeName("THIS");
				} else if (prefixNode.usesSuperModifier()) {
					candidate.setAttributeName("SUPER");
				}
				islocal = true;
			}
			// =================================================
			if (islocal == false) {
				candidate.setPackageName(expression
						.getParentsOfType(ASTCompilationUnit.class).get(0)
						.findChildrenOfType(ASTPackageDeclaration.class).get(0)
						.getFirstChildOfType(ASTName.class).getImage());
//				ArrayList<ASTClassOrInterfaceDeclaration> classDeclaration = (ArrayList<ASTClassOrInterfaceDeclaration>) expression
//						.getParentsOfType(ASTClassOrInterfaceDeclaration.class);
				
				if (expression.getParentsOfType(ASTClassOrInterfaceDeclaration.class)
						.size() == 0) {
					candidate.setClassName(expression
							.getParentsOfType(ASTEnumDeclaration.class).get(0)
							.getImage());
				} else {
					candidate.setClassName(expression
							.getParentsOfType(ASTClassOrInterfaceDeclaration.class)
							.get(0).getImage());
				}
				//candidate.setClassName(classDeclaration.get(0).getImage());
				ArrayList<ASTMethodDeclaration> methodDeclaration = (ArrayList<ASTMethodDeclaration>) expression
						.getParentsOfType(ASTMethodDeclaration.class);
				if (methodDeclaration.size() != 0) {
					candidate.setMethodName(methodDeclaration.get(0)
							.getFirstChildOfType(ASTMethodDeclarator.class)
							.getImage());
					this.extraxtParameter(methodDeclaration.get(0), candidate);
				} else {
					ArrayList<ASTConstructorDeclaration> constructorDeclaration = (ArrayList<ASTConstructorDeclaration>) expression
							.getParentsOfType(ASTConstructorDeclaration.class);
					candidate.setMethodName(candidate.getClassName());
					if (constructorDeclaration.size() > 0) {
						this.extraxtParameter(constructorDeclaration.get(0),
								candidate);
					}
				}
				if (prefixNode
						.getFirstChildOfType(ASTAllocationExpression.class) != null
						&& prefixNode.getFirstChildOfType(
								ASTAllocationExpression.class)
								.getFirstChildOfType(
										ASTClassOrInterfaceType.class) != null) {
					candidate.setMethodInvocationClassType(prefixNode
							.getFirstChildOfType(ASTAllocationExpression.class)
							.getFirstChildOfType(ASTClassOrInterfaceType.class)
							.getImage());
				}
				candidate.setStartLine(expression.getBeginLine());
				candidateCollector.addMethodInvocation(candidate);
			}
		} else if (expression
				.hasDescendantOfType(ASTAllocationExpression.class) == true) {

			// is A ConstructorCall
			candidate.setStartLine(expression.getBeginLine());
			candidate.setPackageName(expression
					.getParentsOfType(ASTCompilationUnit.class).get(0)
					.findChildrenOfType(ASTPackageDeclaration.class).get(0)
					.getFirstChildOfType(ASTName.class).getImage());
//			ArrayList<ASTClassOrInterfaceDeclaration> classDeclaration = (ArrayList<ASTClassOrInterfaceDeclaration>) expression
//					.getParentsOfType(ASTClassOrInterfaceDeclaration.class);
//			candidate.setClassName(classDeclaration.get(0).getImage());
			
			if (expression.getParentsOfType(ASTClassOrInterfaceDeclaration.class)
					.size() == 0) {
				candidate.setClassName(expression
						.getParentsOfType(ASTEnumDeclaration.class).get(0)
						.getImage());
			} else {
				candidate.setClassName(expression
						.getParentsOfType(ASTClassOrInterfaceDeclaration.class)
						.get(0).getImage());
			}
			
			ArrayList<ASTMethodDeclaration> methodDeclaration = (ArrayList<ASTMethodDeclaration>) expression
					.getParentsOfType(ASTMethodDeclaration.class);
			if (methodDeclaration.size() != 0) {
				candidate.setMethodName(methodDeclaration.get(0)
						.getFirstChildOfType(ASTMethodDeclarator.class)
						.getImage());
				this.extraxtParameter(methodDeclaration.get(0), candidate);
			}
			if (expression.getFirstChildOfType(ASTPrimaryPrefix.class) != null) {
				if (expression.getFirstChildOfType(ASTPrimaryPrefix.class)
						.getFirstChildOfType(ASTAllocationExpression.class) != null) {
					if (expression.getFirstChildOfType(ASTPrimaryPrefix.class)
							.getFirstChildOfType(ASTAllocationExpression.class)
							.getFirstChildOfType(ASTAllocationExpression.class) != null) {
						if (expression
								.getFirstChildOfType(ASTPrimaryPrefix.class)
								.getFirstChildOfType(
										ASTAllocationExpression.class)
								.getFirstChildOfType(
										ASTAllocationExpression.class)
								.getFirstChildOfType(
										ASTClassOrInterfaceType.class) != null) {
							candidate
									.setMethodInvocationName(expression
											.getFirstChildOfType(
													ASTPrimaryPrefix.class)
											.getFirstChildOfType(
													ASTAllocationExpression.class)
											.getFirstChildOfType(
													ASTClassOrInterfaceType.class)
											.getImage());
							candidate.setAttributeType(candidate
									.getMethodInvocationName());
							candidate.setAttributeName("ConstructorCall"
									+ candidate.getMethodInvocationName());
							candidateCollector.addMethodInvocation(candidate);
						}
					}
				}
			}
		}
		expression.childrenAccept(this, data);
		return result;
	}

	private Parameter checkLiteralType(List<ASTLiteral> literalList, int i) {
		Parameter parameter;
		parameter = new Parameter();
		if (literalList.get(i).isCharLiteral() == true) {
			parameter.setType("char");
		} else if (literalList.get(i).isFloatLiteral() == true) {
			parameter.setType("double");
		} else if (literalList.get(i).isIntLiteral() == true) {
			parameter.setType("int");
		} else if (literalList.get(i).isStringLiteral() == true) {
			parameter.setType("String");
		} else if (literalList.get(i).hasDescendantOfType(
				ASTBooleanLiteral.class) == true) {
			parameter.setType("boolean");
		}
		return parameter;
	}

	/**
	 * Ueberprueft ob die Method invocation ob es ein Literal als Basistyp ist.
	 * 
	 * @param expression
	 * @return
	 */
	private boolean isNotLiteral(ASTPrimaryExpression expression) {
		ASTPrimaryPrefix prefix = expression
				.getFirstDescendantOfType(ASTPrimaryPrefix.class);
		// Haengt ein Prefix knoten an dann
		if (prefix != null) {
			// dann teste ob dieser einen Knoten ein Literal hat.
			// Ja dann liefere false zurueck da Literal
			if (prefix.hasDescendantOfType(ASTLiteral.class) == true) {
				return false;
			}
		}
		// Anonsten kein Literal
		return true;
	}

	/**
	 * Pr�ft ob eine Knoten vom Type PrimaryExpression �bergabeparameter
	 * besitzt.
	 * 
	 * @param expr
	 * @return
	 */
	private boolean hasSuffixesWithArguments(AbstractJavaNode expr) {
		boolean result = false;
		// pr�ft erst auf THIS und Super gibt es diese nicht dann
		if (hasRealPrefix(expr)) {
			List<ASTPrimarySuffix> suffixes = expr
					.findDescendantsOfType(ASTPrimarySuffix.class);
			// pr�fe jeden Knoten vom Type Suffix auf Argument. Sobald einer
			// gefunden liefere True zur�ck
			for (ASTPrimarySuffix suffix : suffixes) {
				if (suffix.isArguments()) {
					result = true;
					break;
				}
			}
		}
		return result;
	}

	/**
	 * Pr�ft ab ein Knoten vom Type PrimaryExpression einen THIS oder einen
	 * SUPER Keyword verwendt. Wenn ja dann gibt die Methode False zur�ck. This
	 * und Super weisen auf Lokale bzw. Vererbte MEthoden hin die man als Lokal
	 * betrachten kann.
	 * 
	 * @param expr
	 * @return
	 */
	private boolean hasRealPrefix(AbstractJavaNode expr) {
		ASTPrimaryPrefix prefix = expr
				.getFirstDescendantOfType(ASTPrimaryPrefix.class);
		return !prefix.usesThisModifier() && !prefix.usesSuperModifier();
	}

	/**
	 * Add LocalVariable with check to method, class and package. A
	 * LocalVariable has reference to their type Class (which kind the object is
	 * from) and to the related method (where the variable is declarated).
	 * Primitive Type will ignored,
	 * 
	 * @param context
	 *            Local Variable.
	 * @param data
	 *            contains information about the rule
	 * @return rules violation information
	 */
	public Object visit(ASTLocalVariableDeclaration context, Object data) {
		context.childrenAccept(this, data);
		Candidate candidate = new Candidate();
		RuleContext result = (RuleContext) data;

		// if (context.getFirstChildOfType(ASTType.class).getFirstChildOfType(
		// ASTPrimitiveType.class) == null) {
		if (context.getParentsOfType(ASTClassOrInterfaceDeclaration.class)
				.size() == 0) {
			candidate.setClassName(context
					.getParentsOfType(ASTEnumDeclaration.class).get(0)
					.getImage());
		} else {
			candidate.setClassName(context
					.getParentsOfType(ASTClassOrInterfaceDeclaration.class)
					.get(0).getImage());
		}
		
//		candidate.setClassName(context
//				.getParentsOfType(ASTClassOrInterfaceDeclaration.class).get(0)
//				.getImage());
		candidate.setPackageName(context
				.getParentsOfType(ASTCompilationUnit.class).get(0)
				.findChildrenOfType(ASTPackageDeclaration.class).get(0)
				.getFirstChildOfType(ASTName.class).getImage());
		candidate.setAttributeName(context.getVariableName());

		try {
			if (context.findDescendantsOfType(ASTPrimitiveType.class).size() == 0) {
				candidate.setAttributeType(context
						.getFirstChildOfType(ASTType.class)
						.getFirstChildOfType(ASTReferenceType.class)
						.getFirstChildOfType(ASTClassOrInterfaceType.class)
						.getImage());
			} else {
				candidate.setAttributeType(context
						.findDescendantsOfType(ASTPrimitiveType.class).get(0)
						.getImage());
			}
			// candidate.setMethodName(context
			// .getFirstParentOfType(ASTBlockStatement.class)
			// .getFirstParentOfType(ASTBlock.class)
			// .getFirstParentOfType(ASTMethodDeclaration.class)
			// .getMethodName());
			if (context.getFirstParentOfType(ASTMethodDeclaration.class) != null) {
				candidate.setMethodName(context.getFirstParentOfType(
						ASTMethodDeclaration.class).getMethodName());
			} else if (context
					.getFirstParentOfType(ASTConstructorDeclaration.class) != null) {
				candidate.setMethodName(context.getFirstParentOfType(
						ASTConstructorDeclaration.class).getImage());
			}
		} catch (Exception e) {
			System.err.println("BeginnLine " + context.getBeginLine() + " "
					+ candidate.getClassName());
			e.printStackTrace();
		}

		candidate.setAttributeType(context.getFirstChildOfType(ASTType.class)
				.getTypeImage());
		this.getRelatePackageImport(context, candidate);
		ArrayList<ASTMethodDeclaration> methodDeclaration = (ArrayList<ASTMethodDeclaration>) context
				.getParentsOfType(ASTMethodDeclaration.class);
		if (methodDeclaration.size() != 0) {
			candidate.setMethodName(methodDeclaration.get(0)
					.getFirstChildOfType(ASTMethodDeclarator.class).getImage());
			this.extraxtParameter(methodDeclaration.get(0), candidate);
		}
		candidate.setStartLine(context.getBeginLine());
		candidateCollector.addAttribute(candidate, 1);
		return result;
	}

	/**
	 * Die Methode untersucht (extrathiert Namen und Verbindungen zu Attributen
	 * bzw. Methoden) jeden Switch und jeden Case Knoten
	 */
	public Object visit(ASTSwitchStatement context, Object data) {
		int numberOfUseSwitchAttribute = 0;
		context.childrenAccept(this, data);
		Candidate candidate = new Candidate();
		RuleContext result = (RuleContext) data;
		// Hole Package Name
		candidate.setPackageName(context
				.getParentsOfType(ASTCompilationUnit.class).get(0)
				.findChildrenOfType(ASTPackageDeclaration.class).get(0)
				.getFirstChildOfType(ASTName.class).getImage());
		candidate.setClassName(context
				.getParentsOfType(ASTClassOrInterfaceDeclaration.class).get(0)
				.getImage());
		// Hole Switch Header Attribute
		candidate.setAttributeName(context
				.getFirstChildOfType(ASTExpression.class)
				.findDescendantsOfType(ASTName.class).get(0).getImage());

		// Methodenname wo das Switch dazu gehoert.
		AbstractJavaAccessNode method = context
				.getFirstParentOfType(ASTMethodDeclaration.class);
		if (method == null) {
			method = context
					.getFirstParentOfType(ASTConstructorDeclaration.class);
			candidate.setMethodName(method.getFirstParentOfType(
					ASTClassOrInterfaceDeclaration.class).getImage());
		} else {
			candidate.setMethodName(method.getFirstChildOfType(
					ASTMethodDeclarator.class).getImage());
		}
		// Hole Parameter
		this.extraxtParameter(method, candidate);
		// Hole den Knoten PrimaryPrefix.
		ASTPrimaryPrefix prefixNode = context
				.getFirstDescendantOfType(ASTPrimaryPrefix.class);
		//
		List<ASTName> names = prefixNode.findDescendantsOfType(ASTName.class);
		if (!names.isEmpty()) {
			String baseName = names.get(0).getImage();
			int indexOfDot = baseName.lastIndexOf('.');
			if (indexOfDot == -1) {
				candidate.setAttributeName(baseName);
			} else {
				// SwitchHead Attribute beinhaltet . Entweder This oder
				// Methodenaufruf
				candidate.setMethodInvocationName(baseName
						.substring(indexOfDot + 1));
				candidate.setAttributeName(baseName.substring(0, indexOfDot));
				// Checke ob Methode oder THIS
				boolean isMethod = this.hasSuffixesWithArguments(context);
				if (isMethod == true) { // Wenn Methode dann
					// TODO Was tun wenn Methodenaufruf mit oder ohne
					// Parameter??
					candidate.setAttributeName(baseName);
				} else { // Attribut
					if (candidate.getAttributeName().equals("This") == true) {
						candidate.setAttributeName(candidate
								.getMethodInvocationName());
						candidate.setMethodInvocationName("attrib");
					} else if (candidate.getAttributeName().equals("super") == true) {
						candidate.setAttributeName(candidate
								.getMethodInvocationName());
						candidate.setMethodInvocationName("attrib");
					} else {
						candidate.setAttributeName(baseName.substring(0,
								indexOfDot));
						candidate.setMethodInvocationName("attrib");
					}
				}
			}
			ArrayList<Candidate> caseNodeList = setEndlinesForCaseNodes(
					context, candidate);
			candidate.addNodeList(caseNodeList);
		}
		List<Candidate> caseList = candidate.getCaseNodeList();
		for (int i = 0; i < caseList.size(); i++) {
			if (i < caseList.size() - 1) {
				int stopLine = caseList.get(i).getStartLine()
						+ (caseList.get(i + 1).getStartLine() - caseList.get(i)
								.getStartLine()) - 1;
				caseList.get(i).setStopLine(stopLine);
			} else {
				int startLine = caseList.get(i).getStartLine();
				if (context.findChildrenOfType(ASTBlockStatement.class) != null) {
					List<ASTBlockStatement> blockList = context
							.findChildrenOfType(ASTBlockStatement.class);
					boolean go = true;
					int size = blockList.size() - 1;
					while (go == true && size >= 0) {
						try {
							if((blockList.get(size).findChildrenOfType(ASTBreakStatement.class)).size() > -1) {
								caseList.get(i).setStopLine(
										blockList.get(size).getEndLine());
								go = false;
							} else if (blockList.get(size).getBeginLine() < startLine) {
								if (size + 1 < blockList.size()) {
									caseList.get(i).setStopLine(
											blockList.get(size + 1)
													.getEndLine());
								} else {
									caseList.get(i).setStopLine(
											blockList.get(size).getEndLine());
								}
								go = false;
							} else {
								size--;
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				}
			}
		}

		candidate.setSwitchAttributeUseInCases(numberOfUseSwitchAttribute);
		candidateCollector.addSwitchNode(candidate);
		return result;
	}

	private ArrayList<Candidate> setEndlinesForCaseNodes(
			ASTSwitchStatement context, Candidate candidate) {
		// Hole Case Knoten aus dem AST
		ArrayList<ASTSwitchLabel> caseList = (ArrayList<ASTSwitchLabel>) context
				.findChildrenOfType(ASTSwitchLabel.class);
		// Speichere Anzahl der CASE Anweisungen inkl. Default
		candidate.setNumberOfCases(caseList.size());
		ArrayList<Candidate> caseNodeList = new ArrayList<Candidate>();
		for (ASTSwitchLabel astCaseLabel : caseList) {
			Candidate caseNode = new Candidate();
			if (astCaseLabel.findDescendantsOfType(ASTName.class).size() > 0) {
				caseNode.setClassName(astCaseLabel
						.findDescendantsOfType(ASTName.class).get(0).getImage());

			} else if (astCaseLabel.findDescendantsOfType(ASTLiteral.class)
					.size() > 0) {
				caseNode.setClassName(astCaseLabel
						.findDescendantsOfType(ASTLiteral.class).get(0)
						.getImage());
			} else {
				caseNode.setClassName("DEFAULT");
			}
			caseNode.setStartLine(astCaseLabel.getBeginLine());
			caseNode.setStopLine(astCaseLabel.getEndLine());
			caseNodeList.add(caseNode);
		}
		return caseNodeList;
	}

	@Override
	public void end(RuleContext ctx) {
		System.out.println("Start Data Analysis");
		candidateCollector.finishDetection(ctx);
		ctx.removeAttribute("currentPackage");
		System.out.println("Done");
		super.end(ctx);
	}
}
