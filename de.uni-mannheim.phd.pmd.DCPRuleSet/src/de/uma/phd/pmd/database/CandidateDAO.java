package de.uma.phd.pmd.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CandidateDAO implements ICandidate {

	//private Statement stmt = null;
	private Connection connection = null;
	private DBConnector connector = DBConnector.getConnector();

	public CandidateDAO() {
		this.connection = connector.getConnection();
	}

	public void addPackage(Candidate candidate) {
		this.searchPackageID(candidate, false);
		if (candidate.getPackageName() != null
				&& candidate.getPackageNumber() == -2) {
			try {
				Statement stmt = connection.createStatement();
				String sql = "INSERT into packages" 
				+ " VALUES (TRANSACTION_ID(),'" + candidate.getPackageName() + "');";
 			
				stmt.executeUpdate(sql);
				stmt.close();
				connection.commit();
				this.searchPackageID(candidate, false);
				// System.out.println("Package Records created successfully");
			} catch (SQLException e) {

				e.printStackTrace();
			}
		}
	}

	@Override
	public void addClass(Candidate candidate) {
		this.searchPackageID(candidate, false);
		int packageIDDirectFromPackageName = candidate.getPackageNumber();
		int classResult = this.searchClassID(candidate, false, true);
		int extendedClassID = -2;

		if (candidate.getExtendedClassName() != null) {
			Candidate extendedClass = new Candidate();
			extendedClass.setClassName(candidate.getExtendedClassName());
			extendedClassID = this.searchClassID(extendedClass, true, false);
		}

		// 1. Fall: Wenn Package unbekannt und Class unbekannt anlegen!
		try {
			if (candidate.getPackageNumber() == -2 && classResult == -1) {
				if (candidate.getClassName() != null) {
					this.searchPackageID(candidate, true);
					Statement stmt = connection.createStatement();
					String sql = "INSERT into Classes (id, name, activeClass, package_id)"
							+ "VALUES (TRANSACTION_ID(), '"
							+ candidate.getClassName()
							+ "','"
							+ Boolean.toString(candidate.isActiveClass())
							+ "'," + candidate.getPackageNumber() + ");";
					stmt.executeUpdate(sql);
					stmt.close();
					connection.commit();
					// System.out
					// .println("Class Records created successfully in Case 1"
					// + candidate.getClassName());
				}
				// 2. Fall: Wenn Package bekannt und Class unbekannt anlegen
			} else if (candidate.getPackageNumber() != -2 && classResult == -1) {
				Statement stmt = connection.createStatement();
				String sql = "INSERT into Classes (id, name, activeClass, package_id)"
						+ "VALUES (TRANSACTION_ID(),'"
						+ candidate.getClassName()
						+ "','"
						+ Boolean.toString(candidate.isActiveClass())
						+ "',"
						+ candidate.getPackageNumber() + ");";
				stmt.executeUpdate(sql);
				stmt.close();
				connection.commit();
				// System.out.println("Class Records created successfully Case 2");
				// Fall 3; Package unbekannt und Class bekannt. Class anlegen
				// mit neuem Package und ID direk mit dem Packagenamen gesucht
				// wird auch nicht gefunden.
			} else if (candidate.getPackageNumber() == -2 && classResult != -1
					&& packageIDDirectFromPackageName == -2) {
				this.searchPackageID(candidate, true);
				// classResult = this.searchClassID(candidate, true);
				Statement stmt = connection.createStatement();
				String sql = "INSERT into Classes (id, name, activeClass, package_id)"
						+ "VALUES (TRANSACTION_ID(),'"
						+ candidate.getClassName()
						+ "','"
						+ Boolean.toString(candidate.isActiveClass())
						+ "',"
						+ candidate.getPackageNumber() + ");";
				stmt.executeUpdate(sql);
				stmt.close();
				connection.commit();
				checkActiveClass(candidate);
				// System.out.println("Class Records created successfully Case 3");
				// Fall 4: Package unbekannt und Class bekannt. Class anlegen
				// mit neuem Package und ID direk mit dem Packagenamen gesucht
				// wird auch gefunden.
			} else if (candidate.getPackageNumber() == -2 && classResult != -1
					&& packageIDDirectFromPackageName != -2) {
				// this.searchPackageID(candidate, true);
				// classResult = this.searchClassID(candidate, true);
				Statement stmt = connection.createStatement();
				String sql = "UPDATE classes set package_id="
						+ packageIDDirectFromPackageName + " where name = '"
						+ candidate.getClassName() + "' AND package_id = '-2';";
				stmt.executeUpdate(sql);
				stmt.close();
				connection.commit();
				checkActiveClass(candidate);
				// System.out.println("Class Records created successfully Case 4");
				// Fall 5: Package bekannt und Classe bekannt aber
				// Class->PackageID != Package.
				// Class mit PackageID anlegen.
			} else if (candidate.getPackageNumber() != -2
					&& classResult != -1
					&& packageIDDirectFromPackageName != candidate
							.getPackageNumber()) {
				// classResult = this.searchClassID(candidate, true);
				Statement stmt = connection.createStatement();
				String sql = "INSERT into Classes (id, name, activeClass, package_id)"
						+ "VALUES (TRANSACTION_ID(),'"
						+ candidate.getClassName()
						+ "','"
						+ Boolean.toString(candidate.isActiveClass())
						+ "',"
						+ packageIDDirectFromPackageName + ");";
				stmt.executeUpdate(sql);
				stmt.close();
				connection.commit();
				checkActiveClass(candidate);
				// System.out.println("Class Records created successfully Case 5");
			} else {
				ResultSet rs;
				int packageID = -2;
				Statement stmt = connection.createStatement();;
				rs = stmt
						.executeQuery("SELECT package_id FROM classes where name = '"
								+ candidate.getClassName() + "';");
				while (rs.next()) {
					packageID = rs.getInt("package_ID");
				}
				rs.close();
				stmt.close();
				if (packageID == -1) {
					this.searchPackageID(candidate, true);
					stmt.executeUpdate("UPDATE classes set package_ID = "
							+ packageID + " where name = '"
							+ candidate.getClassName() + "';");
					stmt.close();
					connection.commit();
				}
			}

			// Erstelle Verbindung zwischen den Klassen
			if (extendedClassID >= 0) {
				int classID = this.searchClassID(candidate, false, false);
				Statement stmt = connection.createStatement();
				String sql = "INSERT into classExtension (extender_id, origin_id)"
						+ "VALUES (" + classID + "," + extendedClassID + ");";
				stmt.executeUpdate(sql);
				stmt.close();
				connection.commit();
			}
		} catch (SQLException e) {

			e.printStackTrace();
		}
	}

	@Override
	public ArrayList<String> getListOfClasses() {
		// TODO Auto-generated method stub
		return null;
	}

	public void addMethode(Candidate candidate) {
		int relatedClass = -2;
		int isConstructor = -1;
		if (candidate.isConstructor() == true) {
			isConstructor = 1;
		}

		candidate.setClassNumber(this.searchClassID(candidate, false, false));
		if ((this.searchMethodID(candidate, false) < 0 && candidate
				.getMethodName() != null)) {
			// || candidate.isConstructor() == true
			// || candidate.getParameterListSize() > 0) {
			try {
				Statement stmt = connection.createStatement();
				relatedClass = checkIfClassAndPackageConsistent(candidate);
				// Methode Anlegen
				String sql = "INSERT into methodes (id, name, class_id, constructor, numberOfParameters)"
						+ "VALUES (TRANSACTION_ID(), '"
						+ candidate.getMethodName()
						+ "',"
						+ relatedClass
						+ ","
						+ isConstructor
						+ ","
						+ candidate.getParameterList().size() + ");";
				stmt.executeUpdate(sql);

				connection.commit();
				this.checkActiveClass(candidate);
				// System.out.println("Methode Records created successfully "
				// + candidate.getMethodName());

				// int methodeID = this.searchMethodID(candidate, false);
				sql = "SELECT MAX(id) as maxId FROM methodes";
				ResultSet rsId = stmt.executeQuery(sql);
				int methodID = -2;
				while (rsId.next() == true) {
					methodID = rsId.getInt("maxId");
				}
				stmt.close();
				ArrayList<Parameter> parameterList = candidate
						.getParameterList();
				for (Iterator<Parameter> iterator = parameterList.iterator(); iterator
						.hasNext();) {
					Statement stmt2 = connection.createStatement();
					Parameter parameter = (Parameter) iterator.next();
					if (parameter.getType() == null
							&& parameter.getName() != null) {
						Candidate classNameSearch = new Candidate();
						classNameSearch.setAttributeName(parameter.getName());
						classNameSearch.setMethodName(candidate
								.getMethodInvocationName());
						classNameSearch.setPackageNumber(candidate
								.getPackageNumber());
						classNameSearch.setClassName(candidate.getClassName());
						classNameSearch.setParameterList(candidate
								.getMethodAttributeList());
						classNameSearch.setMethodName(candidate
								.getMethodInvocationName());
						classNameSearch.setClassNumber(this.searchClassID(
								classNameSearch, false, false));
						classNameSearch.setMethodeNumber(this.searchMethodID(
								classNameSearch, false));
						classNameSearch.setAttributeNumber(this
								.searchAttributeIDByName(classNameSearch));
						parameter.setType(this
								.searchClassNameByAttributID(classNameSearch));
					}
					String paramSql = "INSERT into parameters (id, name, type, method_id)"
							+ "VALUES (TRANSACTION_ID(),'"
							+ parameter.getName()
							+ "','"
							+ parameter.getType() + "'," + methodID + ");";
					stmt2.executeUpdate(paramSql);
					stmt2.close();
					connection.commit();
				}

			} catch (SQLException e) {

				e.printStackTrace();
			}
		} else if (candidate.getMethodName() == null) {
			System.out.println("here");
		}
	}

	public void addAttribute(Candidate candidate, int entryType) {
		int relatedClass = -2;
		Candidate typeCandidate = new Candidate();
		typeCandidate.setClassName(candidate.getAttributeType());
		typeCandidate.setPackageName(candidate.getAttributePackage());
		candidate.setAttributeTypeID(this
				.checkIfClassAndPackageConsistent(typeCandidate));
		candidate.setClassNumber(this.searchClassID(candidate, true, false));
		candidate.setClassNumber(this
				.checkIfClassAndPackageConsistent(candidate));
		int attributeID = this.searchAttributeID(candidate, false, entryType);
		if (attributeID <= 0) {
			try {
				Statement stmt = connection.createStatement();
				relatedClass = this.checkIfClassAndPackageConsistent(candidate);

				candidate.setClassNumber(relatedClass);
				int methodID = this.searchMethodID(candidate, true);
				String sql = "INSERT into attributes (id, name, classType_id, method_id, class_id, entry_type, count)"
						+ "VALUES (TRANSACTION_ID(), '"
						+ candidate.getAttributeName()
						+ "',"
						+ candidate.getAttributeTypeID()
						+ ","
						+ methodID
						+ ","
						+ relatedClass + "," + entryType + "," + 1 + ");";
				stmt.executeUpdate(sql);
				stmt.close();
				connection.commit();
				// System.out.println("Attributes Records created successfully");
			} catch (SQLException e) {

				e.printStackTrace();
			}
		} else {
			try {
				int newCount = -2;
				Statement stmt = connection.createStatement();
				String sql = "SELECT count FROM attributes where id = "
						+ attributeID + ";";
				ResultSet rsCount = stmt.executeQuery(sql);
				while (rsCount.next() == true) {
					newCount = rsCount.getInt("count") + 1;
				}
				stmt.executeUpdate("UPDATE attributes set count = "
						+ newCount + " where id = '" + attributeID + "';");
				stmt.close();
				connection.commit();
				// System.out.println("Attributes Records created successfully");
			} catch (SQLException e) {

				e.printStackTrace();
			}
		}
	}

	public void addMethodInvocation(Candidate candidate) {
		int relateClassID = this.searchClassID(candidate, true, false);

		int methodID = this.searchMethodID(candidate, true);
		candidate.setMethodNumber(methodID);
		candidate.setClassNumber(relateClassID);
		int attributeID = this.searchAttributeID(candidate, true, 5);
		try {
			Statement stmt = connection.createStatement();
			relateClassID = this.checkIfClassAndPackageConsistent(candidate);
			candidate.setClassNumber(relateClassID);
			candidate.setAttributeNumber(this
					.searchAttributeIDByName(candidate));
			this.searchMethodID(candidate, true);
			this.searchMethodInvocationAttribute(candidate);
			Candidate methodTypeCandidate = new Candidate();

			int indexOfDot = candidate.getMethodInvocationName().lastIndexOf(
					'.');
			if (indexOfDot != -1) {
				methodTypeCandidate.setMethodName((candidate
						.getMethodInvocationName().substring(indexOfDot + 1)));

			} else {
				methodTypeCandidate.setMethodName(candidate
						.getMethodInvocationName());
			}
			methodTypeCandidate.setParameterList(candidate
					.getMethodAttributeList());
			methodTypeCandidate.setClassName(candidate.getClassName());
			methodTypeCandidate.setPackageNumber(candidate.getPackageNumber());
			methodTypeCandidate.setClassNumber(candidate.getAttributeTypeID());
			methodTypeCandidate.setMethodInvocationName(candidate
					.getMethodName());
			methodTypeCandidate.setMethodInvocationClassType(candidate
					.getClassName());
			methodTypeCandidate.setMethodAttributeList(candidate
					.getParameterList());
			int medhodID2 = this.searchMethodID(methodTypeCandidate, true);
			String sql = "INSERT into methodinvocations (id, name, classType_id, methodType_id, attribute_id, method_id, class_id, startline)"
					+ "VALUES (TRANSACTION_ID(),'"
					+ candidate.getMethodInvocationName()
					+ "',"
					+ candidate.getAttributeTypeID()
					+ ","
					+ medhodID2
					+ ","
					+ attributeID
					+ ","
					+ methodID
					+ ","
					+ relateClassID
					+ ","
					+ candidate.getStartLine() + ");";
			stmt.executeUpdate(sql);

			stmt.close();
			connection.commit();
			// System.out.println("Attributes Records created successfully");
		} catch (SQLException e) {

			e.printStackTrace();
		}
	}

	public void addSwitchNode(Candidate candidate) {
		String sql = null;
		try {
			Statement stmt = connection.createStatement();
			Candidate methodeType = new Candidate();
			methodeType.setMethodName(candidate.getMethodInvocationName());
			candidate
					.setClassNumber(this.searchClassID(candidate, true, false));
			int methodID = this.searchMethodID(candidate, true);
			int attributID = this.searchAttributeIDByName(candidate);
			int paramID = -1;
			if (attributID < 0) {
				paramID = this.searchParameter(candidate);
			}

			sql = "INSERT into switchnodes (id, name, count, method_id, attribut_id, param_id, methodType_id)"
					+ "VALUES (TRANSACTION_ID(), '"
					+ "SWITCH"
					+ "',"
					+ candidate.getNumberOfCases()
					+ ","
					+ methodID
					+ ","
					+ attributID
					+ ","
					+ paramID
					+ ","
					+ this.searchMethodID(methodeType, true) + ");";

			stmt.execute(sql);
			stmt.close();

			Statement stmt2 = connection.createStatement();
			sql = "SELECT count(id) AS numberOfSwichtNodes FROM switchnodes";
			ResultSet rs = stmt2.executeQuery(sql);
			int currentSwitchNodeID = -2;
			while (rs.next()) {
				currentSwitchNodeID = rs.getInt("numberOfSwichtNodes");
			}
			connection.commit();
			ArrayList<Candidate> caseNodeListe = candidate.getCaseNodeList();
			for (Iterator<Candidate> iterator = caseNodeListe.iterator(); iterator
					.hasNext();) {
				Statement stmt3 = connection.createStatement();
				Candidate candidate2 = (Candidate) iterator.next();
				String caseName = candidate2.getClassName().toLowerCase();
				sql = "INSERT into caseNode (id, name, startline, stopline, switch_id)"
						+ "VALUES (TRANSACTION_ID(), '"
						+ caseName
						+ "',"
						+ candidate2.getStartLine()
						+ ","
						+ candidate2.getStopLine()
						+ ","
						+ currentSwitchNodeID
						+ ");";

				stmt3.execute(sql);
				stmt3.close();
				connection.commit();
			}

			// System.out.println("Package Records created successfully");
		} catch (SQLException e) {
			System.err.println(sql);
			e.printStackTrace();
		}
	}

	/**
	 * Diese Methode ueberprueft ob eine Klasse aktiv ist, wenn nicht wird sie
	 * aktiv gesetzt.
	 * 
	 * @param candidate
	 * @throws SQLException
	 */
	private void checkActiveClass(Candidate candidate) throws SQLException {
		boolean result = false;
		Statement stmt = this.connection.createStatement();

		ResultSet rs = stmt
				.executeQuery("SELECT activeClass FROM classes where name = '"
						+ candidate.getClassName() + "';");
		while (rs.next()) {
			result = rs.getBoolean("activeClass");
		}
		rs.close();
		stmt.close();
		if (result == false) {
			Statement stmt2 = this.connection.createStatement();
			candidate.setActiveClass(true);
			stmt2.executeUpdate("UPDATE classes set activeClass = '"
					+ Boolean.toString(candidate.isActiveClass())
					+ "' where name = '" + candidate.getClassName() + "';");
			stmt2.close();
			connection.commit();
		}
	}

	private void searchPackageID(Candidate candidate, boolean toCreate) {
		int result = -1;

		if (candidate.getPackageName() != null) {
			try {
				Statement stmt = this.connection.createStatement();

				ResultSet rs = stmt
						.executeQuery("SELECT ID FROM packages where name = '"
								+ candidate.getPackageName() + "';");
				while (rs.next()) {
					candidate.setPackageNumber(rs.getInt("id"));
					result = candidate.getPackageNumber();
				}
				rs.close();
				stmt.close();
			} catch (SQLException e) {

				e.printStackTrace();
			}
		}

		if (toCreate == true && result == -1) {
			this.addPackage(candidate);
			this.searchPackageID(candidate, false);
		}
	}

	private int searchClassID(Candidate candidate, boolean toCreate,
			boolean writePackageID) {
		int result = -1;
		int packageID = -3;
		if (candidate.getClassName() != null) {
			try {
				Statement stmt = this.connection.createStatement();
				ResultSet rs = stmt
						.executeQuery("SELECT count(package_id) AS numberOfResults FROM classes where name = '"
								+ candidate.getClassName() + "';");
				if (rs.next() && rs.getInt("numberOfResults") > 1) {
					ResultSet rs2 = null;
					this.searchPackageID(candidate, true);
					rs2 = stmt
							.executeQuery("SELECT id, package_id FROM classes where name = '"
									+ candidate.getClassName()
									+ "' AND package_id = '"
									+ Integer.toString(candidate
											.getPackageNumber()) + "';");
					// TODO java.sql.SQLException: ResultSet closed find
					// solution.
					if (rs2.next() == true) {
						result = rs2.getInt("id");
						packageID = rs2.getInt("package_id");
					}
					rs2.close();
				} else if (rs.getInt("numberOfResults") == 1) {
					ResultSet rs3 = stmt
							.executeQuery("SELECT id, package_id FROM classes where name = '"
									+ candidate.getClassName() + "';");
					if (rs3.next() == true) {
						result = rs3.getInt("id");
						packageID = rs3.getInt("package_id");
					}
					rs3.close();
				}
				rs.close();
				stmt.close();
				if (writePackageID == true && packageID != -3) {
					candidate.setPackageNumber(packageID);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		if (toCreate == true && result == -1) {
			this.addClass(candidate);
			result = this.searchClassID(candidate, false, false);
		}

		return result;
	}

	private String searchClassNameByAttributID(Candidate candidate) {
		String className = null;
		try {
			Statement stmt = this.connection.createStatement();
			String sql = "SELECT classType_id FROM attributes WHERE id = "
					+ candidate.getAttributeNumber() + ";";
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next() == true) {
				Statement stmt2 = this.connection.createStatement();
				sql = "SELECT name FROM classes WHERE id = "
						+ rs.getInt("classType_ID") + ";";
				ResultSet rsClassName = stmt2.executeQuery(sql);
				while (rsClassName.next()) {
					className = rsClassName.getString("name");
				}
			}
			stmt.close();
		} catch (SQLException e) {

			e.printStackTrace();
		}
		return className;
	}

	private int searchClassIDByMethodID(Candidate candidate) {
		int classID = -1;
		try {
			Statement stmt = this.connection.createStatement();
			String sql = "SELECT class_id FROM methodes WHERE id = "
					+ candidate.getMethodeNumber() + ";";
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next() == true) {
				classID = rs.getInt("class_id");
			}
			stmt.close();
		} catch (SQLException e) {

			e.printStackTrace();
		}
		return classID;
	}

	// private int searchMethodID(Candidate candidate, boolean toCreate) {
	// int result = -1;
	// if (candidate.getMethodName() != null) {
	// try {
	// this.stmt = this.connection.createStatement();
	//
	// ResultSet rs = this.stmt
	// .executeQuery("SELECT ID FROM methodes where name = '"
	// + candidate.getMethodName()
	// + "' AND class_id = "
	// + candidate.getClassNumber() + ";");
	// while (rs.next()) {
	// candidate.setMethodNumber(rs.getInt("id"));
	// result = candidate.getMethodNumber();
	// }
	// rs.close();
	// this.stmt.close();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// }
	//
	// if (toCreate == true && result == -1) {
	// this.addMethode(candidate);
	// result = this.searchMethodID(candidate, false);
	// }
	// return result;
	// }

	private int searchMethodID(Candidate candidate, boolean toCreate) {
		int methodID = -1;
		if (candidate.getMethodName() != null) {
			try {
				Statement stmt = this.connection.createStatement();

				String sqlSearchForMethod = "SELECT id, numberOfParameters "
						+ " FROM methodes " + " WHERE name = '"
						+ candidate.getMethodName() + "' AND class_id = "
						+ candidate.getClassNumber() + ";";

				String sqlCountMethodes = "SELECT count(id) as numberOfMethodes "
						+ " FROM methodes"
						+ " WHERE name = '"
						+ candidate.getMethodName()
						+ "' AND class_id = "
						+ candidate.getClassNumber() + ";";

				ResultSet rs = stmt.executeQuery(sqlCountMethodes);
				while (rs.next() == true) {
					int count = rs.getInt("numberOfMethodes");
					if (count >= 1) {
						Statement stmt2 = this.connection.createStatement();
						List<Parameter> parameterList = candidate
								.getParameterList();
						int numberOfParameter;
						boolean isEqual = false;
						ResultSet rsMethodID = stmt2
								.executeQuery(sqlSearchForMethod);

						do {
							Statement stmt3 = this.connection.createStatement();
							rsMethodID.next();
							numberOfParameter = rsMethodID
									.getInt("numberOfParameters");
							if (numberOfParameter > 0
									&& numberOfParameter == candidate
											.getParameterListSize()) {
								int methodSearchId = rsMethodID.getInt("id");
								String sqlSearchParameters = "SELECT type FROM parameters WHERE method_id = "
										+ methodSearchId + ";";
								ResultSet rsParameters = stmt3
										.executeQuery(sqlSearchParameters);
								int countIsEqual = 0;
								Iterator<Parameter> iterator = parameterList
										.iterator();
								while (rsParameters.next()
										&& iterator.hasNext()
										&& isEqual == false) {
									String type = rsParameters
											.getString("type");
									Parameter parameter = (Parameter) iterator
											.next();
									if (type.equals(parameter.getType()) == true) {
										countIsEqual++;
									}
								}
								if (countIsEqual == numberOfParameter) {
									isEqual = true;
									methodID = methodSearchId;
								}
							} else if (numberOfParameter == 0
									&& candidate.getParameterListSize() == 0) {
								isEqual = true;
								methodID = rsMethodID.getInt("id");
							}
							stmt3.close();
						} while (rsMethodID.next() && isEqual == false);
						rsMethodID.close();
						stmt2.close();
					}
				}
				rs.close();
				stmt.close();
			} catch (SQLException e) {
				System.err.println("Error During searching for Method ID");
				e.printStackTrace();
			}

			if (toCreate == true && methodID == -1) {
				this.addMethode(candidate);
				methodID = this.searchMethodID(candidate, false);
			}
		}
		return methodID;
	}

	private int searchAttributeID(Candidate candidate, boolean toCreate,
			int entryType) {
		int result = -1;
		if (candidate.getPackageName() != null) {
			String sql = null;
			try {
				Statement stmt = this.connection.createStatement();

				ResultSet rs = stmt
						.executeQuery("SELECT id, method_id, classType_id FROM attributes where name = '"
								+ candidate.getAttributeName()
								+ "' AND class_id = "
								+ candidate.getClassNumber()
								+ " AND classtype_id = "
								+ candidate.getAttributeTypeID() + ";");

				while (rs.next()) {
					candidate.setAttributeNumber(rs.getInt("id"));
					result = candidate.getAttributeNumber();
					if (rs.getInt("method_id") < 0
							&& candidate.getMethodNumber() >= 0) {
						sql = "UPDATE attributes set method_id ="
								+ candidate.getMethodNumber()
								+ " where name = '"
								+ candidate.getAttributeName()
								+ "' AND class_id = '"
								+ candidate.getClassNumber()
								+ " AND classtype_id = "
								+ candidate.getAttributeTypeID() + "';";
						stmt.executeUpdate(sql);
					} else if (rs.getInt("classType_id") >= 0
							&& candidate.getAttributeTypeID() < 0) {
						candidate.setAttributeTypeID(rs.getInt("classType_id"));
					}
				}
				rs.close();
				stmt.close();
				Statement stmt2 = this.connection.createStatement();
				String sqlMethod = "SELECT id, method_id, classType_id FROM attributes where name = '"
						+ candidate.getAttributeName()
						+ "' AND class_id = "
						+ candidate.getClassNumber()
						+ " AND method_id = "
						+ candidate.getMethodNumber() + ";";
				ResultSet rsByMethod = stmt2.executeQuery(sqlMethod);
				while (rsByMethod.next()) {
					if (rsByMethod.getInt("classType_id") >= 0
							&& candidate.getAttributeNumber() < 0) {
						result = rsByMethod.getInt("id");
						candidate.setAttributeTypeID(rsByMethod
								.getInt("classType_id"));
					} else if (rsByMethod.getInt("classType_id") < 0
							&& candidate.getAttributeNumber() >= 0) {
						sql = "UPDATE attributes set method_id ="
								+ candidate.getMethodNumber()
								+ " where name = '"
								+ candidate.getAttributeName()
								+ "' AND class_id = "
								+ candidate.getClassNumber()
								+ " AND method_id = "
								+ candidate.getMethodNumber() + ";";
						stmt.executeUpdate(sql);
					}
				}
				rsByMethod.close();
			} catch (SQLException e) {

				e.printStackTrace();
			}
		}

		if (toCreate == true && result == -1) {
			this.addAttribute(candidate, 3);
			result = this.searchAttributeID(candidate, false, 3);
		}
		return result;
	}

	private void searchMethodInvocationAttribute(Candidate candidate) {
		if (candidate.getAttributeNumber() >= 0) {
			try {
				Statement stmt = this.connection.createStatement();

				ResultSet rs = stmt
						.executeQuery("SELECT classType_id FROM methodinvocations where attribute_ID = '"
								+ candidate.getAttributeNumber()
								+ "' AND class_id = "
								+ candidate.getClassNumber() + ";");
				while (rs.next()) {
					if (rs.getInt("classType_id") > 0) {
						String sql = "UPDATE methodinvocations set classType_id ="
								+ candidate.getAttributeTypeID()
								+ " where attribute_ID = '"
								+ candidate.getAttributeName()
								+ "' AND class_id = '"
								+ candidate.getClassNumber() + "';";
						stmt.executeUpdate(sql);
					}
				}
				rs.close();
				stmt.close();
			} catch (SQLException e) {

				e.printStackTrace();
			}
		}
	}

	private int searchAttributeIDByName(Candidate candidate) {
		int result = -1;
		if (candidate.getAttributeName() != null) {
			// String sql = null;
			try {
				Statement stmt = this.connection.createStatement();

				ResultSet rs = stmt
						.executeQuery("SELECT id FROM attributes where name = '"
								+ candidate.getAttributeName()
								+ "' AND class_id = "
								+ candidate.getClassNumber() + ";");

				while (rs.next()) {
					candidate.setAttributeNumber(rs.getInt("id"));
					result = candidate.getAttributeNumber();
				}
				rs.close();
				stmt.close();
			} catch (SQLException e) {

				e.printStackTrace();
			}
		}
		return result;
	}

	private int searchParameter(Candidate candidate) {
		int result = -1;
		int methodID = this.searchMethodID(candidate, false);
		if (candidate.getAttributeName() != null) {
			String sql = "SELECT id FROM parameters where name = '"
					+ candidate.getAttributeName() + "' AND method_id = "
					+ methodID + ";";
			try {
				Statement stmt = this.connection.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
				while (rs.next()) {
					result = rs.getInt("id");
				}
				rs.close();
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	private int checkIfClassAndPackageConsistent(Candidate candidate) {
		int relatedClass;
		int relatedPackageDirectSearch = -3;
		this.searchPackageID(candidate, true);
		relatedPackageDirectSearch = candidate.getPackageNumber();
		relatedClass = this.searchClassID(candidate, false, true);
		// Wenn PackageID von PackageSuche und PackageID von Klassensuch
		// ungleich
		if (relatedPackageDirectSearch != candidate.getPackageNumber()) {
			this.addClass(candidate);
			relatedClass = this.searchClassID(candidate, false, false);
		} else if (relatedClass == -1) {
			relatedClass = this.searchClassID(candidate, true, true);
		}
		return relatedClass;
	}

	public void finishDetection() {
		this.connector.closeConnector();
	}

}
