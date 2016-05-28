package de.uma.phd.pmd.basic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.java.ast.ASTAllocationExpression;
import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTBooleanLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
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
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLabel;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.AbstractJavaAccessNode;
import net.sourceforge.pmd.lang.java.ast.AbstractJavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.rule.JavaRuleViolation;
import de.uma.phd.pmd.database.Candidate;
import de.uma.phd.pmd.database.CandidateDAO;
import de.uma.phd.pmd.database.Parameter;

public class DesignPatternDetection extends AbstractJavaRule {

	private CandidateDAO candidateCollector = new CandidateDAO();
	private String currentPackageName = null;
	private String packageAttributName = new String("currentPackage");
	//private RuleManager rules;

	// private ASTPrimaryExpression expression;

	@Override
	public void start(RuleContext ctx) {
		ctx.setAttribute("currentPackage", new String());
		super.start(ctx);
		System.err.println("TESTTESTTESTTESTTESTTESTTESTTESTTEST");
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
		result.getReport().addRuleViolation(
				new JavaRuleViolation(this, result, packageNode,
						"Get Package!!"));

		return result;
	}

	public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
		node.childrenAccept(this, data);
		//TODO unterschied Klasse und Interface einfuegen.
		Candidate candidate = new Candidate();
		RuleContext result = (RuleContext) data;

		candidate.setActiveClass(true);
		candidate.setClassName(node.getImage());
		candidate.setPackageName((String) result
				.getAttribute(packageAttributName));
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

		result.getReport().addRuleViolation(
				new JavaRuleViolation(this, result, node, "Get Class!!"));
		return result;
	}

	public Object visit(ASTConstructorDeclaration method, Object data) {
		method.childrenAccept(this, data);
		Candidate candidate = new Candidate();
		RuleContext result = (RuleContext) data;
		// Hole den Klassenamen fuer den aktuellen Kontruktor. 
		candidate.setClassName(method
				.getParentsOfType(ASTClassOrInterfaceDeclaration.class).get(0)
				.getImage());
		candidate.setMethodName(candidate.getClassName());
		// Hole Package Name der aktuelle Klasse
		candidate.setPackageName((String) result
				.getAttribute(packageAttributName));
		candidate.setConstructor(true);
		// Hole uebergabeparameter aus dem AST. 
		this.extraxtParameter(method, candidate);
		// Alle Daten in die Datenbank
		candidateCollector.addMethode(candidate);
		
		result.getReport().addRuleViolation(
				new JavaRuleViolation(this, result, method, "Constructor!!"));
		
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
		candidate.setClassName(method
				.getParentsOfType(ASTClassOrInterfaceDeclaration.class).get(0)
				.getImage());
		candidate.setPackageName((String) result
				.getAttribute(packageAttributName));
		// Hole Parameter der Methode.
		this.extraxtParameter(method, candidate);

		// uebergebe das ganze an den Collector (die Datenbank).
		candidateCollector.addMethode(candidate);
		result.getReport().addRuleViolation(
				new JavaRuleViolation(this, result, method, "Get Methode!!"));
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
				
				/*if (astFormalParameter
						.hasDescendantOfType(ASTPrimitiveType.class) == true) {
					astFormalParameter
							.findDescendantsOfType(ASTPrimitiveType.class)
							.get(0).getImage();
				} else if (astFormalParameter
						.hasDescendantOfType(ASTClassOrInterfaceType.class)) {
					astFormalParameter
							.findDescendantsOfType(ASTReferenceType.class)
							.get(0)
							.getFirstChildOfType(ASTClassOrInterfaceType.class)
							.getImage();
				}*/

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
		
		candidate.setClassName(context
				.getParentsOfType(ASTClassOrInterfaceDeclaration.class)
				.get(0).getImage());
		candidate.setPackageName((String) result
				.getAttribute(packageAttributName));
		candidate.setAttributeName(context
				.getFirstChildOfType(ASTVariableDeclarator.class)
				.getFirstChildOfType(ASTVariableDeclaratorId.class)
				.getImage());
		if (context.findDescendantsOfType(ASTPrimitiveType.class).size() == 0) {		
			candidate.setAttributeType(context
					.getFirstChildOfType(ASTType.class)
					.getFirstChildOfType(ASTReferenceType.class)
					.getFirstChildOfType(ASTClassOrInterfaceType.class)
					.getImage());
		} else {
			candidate.setAttributeType(context.findDescendantsOfType(ASTPrimitiveType.class).get(0).getImage());
		}
		this.getRelatePackageImport(context, candidate);

		candidateCollector.addAttribute(candidate, 2);

		result.getReport().addRuleViolation(
				new JavaRuleViolation(this, result, context,
						"Get Attribut!!"));
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
				candidate.setPackageName((String) result
						.getAttribute(packageAttributName));
				ArrayList<ASTClassOrInterfaceDeclaration> classDeclaration = (ArrayList<ASTClassOrInterfaceDeclaration>) expression
						.getParentsOfType(ASTClassOrInterfaceDeclaration.class);
				candidate.setClassName(classDeclaration.get(0).getImage());
				ArrayList<ASTMethodDeclaration> methodDeclaration = (ArrayList<ASTMethodDeclaration>) expression
						.getParentsOfType(ASTMethodDeclaration.class);
				if (methodDeclaration.size() != 0) {
					candidate.setMethodName(methodDeclaration.get(0)
							.getFirstChildOfType(ASTMethodDeclarator.class)
							.getImage());
					this.extraxtParameter(methodDeclaration.get(0), candidate);
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
			result.getReport().addRuleViolation(
					new JavaRuleViolation(this, result, expression,
							"Get MethodInvocation!!"));
		} else if (expression
				.hasDescendantOfType(ASTAllocationExpression.class) == true) {

			// is A ConstructorCall
			candidate.setStartLine(expression.getBeginLine());
			candidate.setPackageName((String) result
					.getAttribute(packageAttributName));
			ArrayList<ASTClassOrInterfaceDeclaration> classDeclaration = (ArrayList<ASTClassOrInterfaceDeclaration>) expression
					.getParentsOfType(ASTClassOrInterfaceDeclaration.class);
			candidate.setClassName(classDeclaration.get(0).getImage());
			ArrayList<ASTMethodDeclaration> methodDeclaration = (ArrayList<ASTMethodDeclaration>) expression
					.getParentsOfType(ASTMethodDeclaration.class);
			if (methodDeclaration.size() != 0) {
				candidate.setMethodName(methodDeclaration.get(0)
						.getFirstChildOfType(ASTMethodDeclarator.class)
						.getImage());
				this.extraxtParameter(methodDeclaration.get(0), candidate);
			}
			if (expression.getFirstChildOfType(ASTPrimaryPrefix.class) != null) {
				if(expression.getFirstChildOfType(ASTPrimaryPrefix.class).getFirstChildOfType(ASTAllocationExpression.class) != null) {
					if(expression.getFirstChildOfType(ASTPrimaryPrefix.class).getFirstChildOfType(ASTAllocationExpression.class).getFirstChildOfType(ASTAllocationExpression.class) != null) {
						if(expression.getFirstChildOfType(ASTPrimaryPrefix.class).getFirstChildOfType(ASTAllocationExpression.class).getFirstChildOfType(ASTAllocationExpression.class).getFirstChildOfType(ASTClassOrInterfaceType.class) != null) {
							candidate.setMethodInvocationName(expression
									.getFirstChildOfType(ASTPrimaryPrefix.class)
									.getFirstChildOfType(ASTAllocationExpression.class)
									.getFirstChildOfType(ASTClassOrInterfaceType.class)
									.getImage());
							candidate.setAttributeType(candidate.getMethodInvocationName());
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
	 * ueberprueft ob die Method invocation ob es ein Literal als Basistyp ist.
	 * 
	 * @param expression
	 * @return
	 */
	private boolean isNotLiteral(ASTPrimaryExpression expression) {
		ASTPrimaryPrefix prefix = expression
				.getFirstDescendantOfType(ASTPrimaryPrefix.class);
		// Haengt ein Prefix knoten an dann
		if (prefix != null) {
			// dann test ob dieser einen Knoten Literal hat.
			// Ja dann liefere false zurueck da Literal
			if (prefix.hasDescendantOfType(ASTLiteral.class) == true) {
				return false;
			}
		}
		// Anonsten kein Literal
		return true;
	}

	/**
	 * Prueft ob eine Knoten vom Type PrimaryExpression uebergabeparameter
	 * besitzt.
	 * 
	 * @param expr
	 * @return
	 */
	private boolean hasSuffixesWithArguments(AbstractJavaNode expr) {
		boolean result = false;
		// prueft erst auf THIS und Super gibt es diese nicht dann
		if (hasRealPrefix(expr)) {
			List<ASTPrimarySuffix> suffixes = expr
					.findDescendantsOfType(ASTPrimarySuffix.class);
			// pruefe jeden Knoten vom Type Suffix auf Argument. Sobald einer
			// gefunden liefere True zurueck
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
	 * Prueft ab ein Knoten vom Type PrimaryExpression einen THIS oder einen
	 * SUPER Keyword verwendt. Wenn ja dann gibt die Methode False zurueck. This
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
		candidate.setClassName(context
				.getParentsOfType(ASTClassOrInterfaceDeclaration.class).get(0)
				.getImage());
		candidate.setPackageName((String) result
				.getAttribute(packageAttributName));
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
		candidateCollector.addAttribute(candidate, 1);
		result.getReport().addRuleViolation(
				new JavaRuleViolation(this, result, context,
						"Get LocalVariable!!"));
		// }
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
		candidate.setPackageName((String) result
				.getAttribute(packageAttributName));
		candidate.setClassName(context
				.getParentsOfType(ASTClassOrInterfaceDeclaration.class).get(0)
				.getImage());
		// Hole Switch Header Attribute
		candidate.setAttributeName(context.getFirstChildOfType(ASTExpression.class).findDescendantsOfType(ASTName.class).get(0).getImage());
		
		// Methodenname wo das Switch dazu gehoert.
		ASTMethodDeclaration method = context
				.getFirstParentOfType(ASTMethodDeclaration.class);
		candidate.setMethodName(method
				.getFirstChildOfType(ASTMethodDeclarator.class).getImage());
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
				// SwitchHead Attribute beinhaltet . Entweder This oder Methodenaufruf
				candidate.setMethodInvocationName(baseName
						.substring(indexOfDot + 1));
				candidate.setAttributeName(baseName
						.substring(0, indexOfDot));
				// Checke ob Methode oder THIS
				boolean isMethod = this.hasSuffixesWithArguments(context);
				if (isMethod == true) { // Wenn Methode dann
					//TODO Was tun wenn Methodenaufruf mit oder ohne Parameter??
					candidate.setAttributeName(baseName);
				} else { // Attribut
					if(candidate.getAttributeName().equals("This") == true) {
						candidate.setAttributeName(candidate.getMethodInvocationName());
						candidate.setMethodInvocationName("attrib");
					} else if(candidate.getAttributeName().equals("super") == true) {
						candidate.setAttributeName(candidate.getMethodInvocationName());
						candidate.setMethodInvocationName("attrib");
					} else {
						System.err.println("Duerfte es nicht geben. Wenn . und keine Methoden dann THIS oder super");
					}
				}
			}
			// Hole Case Knoten aus dem AST
			ArrayList<ASTSwitchLabel> caseList = (ArrayList<ASTSwitchLabel>) context
					.findChildrenOfType(ASTSwitchLabel.class);
			//Speichere Anzahl der CASE Anweisungen inkl. Default
			candidate.setNumberOfCases(caseList.size());
			ArrayList<Candidate> caseNodeList = new ArrayList<Candidate>();
	//		ArrayList<ASTBlockStatement> blockList = (ArrayList<ASTBlockStatement>) context
	//				.findChildrenOfType(ASTBlockStatement.class);
	//		ArrayList<ASTSwitchLabel> caseLabelList = (ArrayList<ASTSwitchLabel>) context
	//				.findDescendantsOfType(ASTSwitchLabel.class);
			for (ASTSwitchLabel astCaseLabel : caseList) {
				Candidate caseNode = new Candidate();
				if (astCaseLabel.findDescendantsOfType(ASTName.class).size() > 0) {
					caseNode.setClassName(astCaseLabel
							.findDescendantsOfType(ASTName.class).get(0)
							.getImage());
					
				} else if (astCaseLabel.findDescendantsOfType(ASTLiteral.class).size() > 0) {
					caseNode.setClassName(astCaseLabel.findDescendantsOfType(ASTLiteral.class).get(0).getImage());
				} else {
					caseNode.setClassName("DEFAULT");
				}
				caseNode.setStartLine(astCaseLabel.getBeginLine());
				caseNode.setStopLine(astCaseLabel.getEndLine());
				caseNodeList.add(caseNode);
			}
			// }
			candidate.addNodeList(caseNodeList);
		}

		candidate.setSwitchAttributeUseInCases(numberOfUseSwitchAttribute);
		candidateCollector.addSwitchNode(candidate);

		result.getReport().addRuleViolation(
				new JavaRuleViolation(this, result, context,
						"Get Switch/Case!!"));
		return result;
	}

	@Override
	public void end(RuleContext ctx) {
		// AtomicLong total = (AtomicLong)ctx.getAttribute(COUNT);
		// addViolation(ctx, null, new Object[] { total });
		//rules = new RuleManager();
		//rules.startAnalysis();
		System.out.println("ENDE");
		candidateCollector.finishDetection();
		ctx.removeAttribute("currentPackage");
		super.end(ctx);
	}
}
