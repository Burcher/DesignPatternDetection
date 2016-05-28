package net.sourceforge.pmd.lang.dpc.rules.state;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.dpc.rules.RuleManager;

public class StateRule implements PropertyChangeListener {
	// Connection to Database
	private Connection connection;

	// private RuleContext context;

	public StateRule(RuleManager rManager, Connection connection) {
		this.connection = connection;
		rManager.addListner(this);
		// this.context = ctx;
	}

	/**
	 * Call Back Method to start analysis.
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		this.startAnalysis();
	}

	/**
	 * Analysis method. Controlls the analysis for State and Strategy pattern.
	 */
	private void startAnalysis() {
		try {
			// 1. Hole alle Classen IDs die mehr als einmal in SwitchNodes
			// vorkommen
			Statement classIDSearchStmnt = this.connection.createStatement();
			String sql = "SELECT count(s.method_id) AS caseno, c.id as classID "
					+ "FROM switchnodes AS s "
					+ "JOIN methodes AS m ON m.id = s.method_id "
					+ "JOIN classes AS c ON c.id = m.class_id "
					+ "WHERE s.count > 2 "
					+ "GROUP BY c.id "
					+ "HAVING caseno > 1;";
			ResultSet rsClassIDList = classIDSearchStmnt.executeQuery(sql);
			while (rsClassIDList.next()) {
				Statement switchAppearenceSearch = this.connection
						.createStatement();
				// 2. Zaehle die Anzahl an Cases pro Switch und entferne die mit
				// kleiner 2
				String sqlGetNumberOfSwitchAppearence = "SELECT s.count, count(s.count) AS numberofAppearence, c.id "
						+ " FROM switchnodes AS s "
						+ " JOIN methodes AS m ON m.id = s.method_id "
						+ " JOIN classes AS c ON c.id = m.class_id "
						+ " WHERE c.id = "
						+ rsClassIDList.getInt("classID")
						+ " AND s.count > 2 "
						+ " GROUP BY s.count "
						+ " HAVING numberofAppearence>1;";
				ResultSet rsAppearence = switchAppearenceSearch
						.executeQuery(sqlGetNumberOfSwitchAppearence);

				while (rsAppearence.next()) {
					// Check with Parameters
					ResultSet rsParamList = checkStrategyWithParameters(
							rsClassIDList, rsAppearence);
					// Check with Attributes
					checkStrategyWithAttributes(rsClassIDList, rsAppearence,
							rsParamList);
					// Check State with Attributes
					checkStateWithAttributes(rsClassIDList, rsAppearence);
				}
				switchAppearenceSearch.close();
			}
			classIDSearchStmnt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void checkStateWithAttributes(ResultSet rsClassIDList,
			ResultSet rsAppearence) throws SQLException {
		/** State Abfrage */
		// Zähle alle Attribute aus der Tabelle Switches die die KlassenID aus
		// rsClassIDList und > (s.count) cases besitzen. Sortierte die aus
		// die nur 2 und weniger mal vorkommen, keine AttributesID haben oder
		// nur einmal in der Klasse vorkommen.
		Statement stmtArgumentList = this.connection.createStatement();
		String sqlGetAttribList = "SELECT count(s.attribut_id) as appearence, s.attribut_id"
				+ " FROM switchnodes AS s "
				+ " JOIN methodes AS m ON m.id = s.method_id "
				+ " JOIN classes AS c ON c.id = m.class_id "
				+ " WHERE c.id =  "
				+ rsClassIDList.getInt("classID")
				+ " AND s.count = "
				+ rsAppearence.getInt("count")
				+ " AND s.count > 2 "
				+ " AND s.attribut_id > -1 "
				+ " GROUP BY s.attribut_id " + " HAVING appearence > 1;";
		ResultSet rsAttribList = stmtArgumentList
				.executeQuery(sqlGetAttribList);

		while (rsAttribList.next()) {
			// Hole die IDs die den gleichen Attribute und in der
			// gleichen Klasse sind.
			int attributeID = rsAttribList.getInt("attribut_id");
			// Hole die IDs der Switches der passenden Switches.
			ResultSet rsSwitchIDs = getSwitchIDs(rsClassIDList, rsAppearence,
					attributeID, "s.attribut_id");
			HashMap<String, Integer> caseNames = new HashMap<String, Integer>();
			int numberOfSwitchIDs = 0;
			int numberOfSwitchesWithPotentialState = 0;
			ArrayList<Integer> methodID = new ArrayList<Integer>();
			ArrayList<Integer> switchID = new ArrayList<Integer>();
			while (rsSwitchIDs.next()) {
				methodID.add(rsSwitchIDs.getInt("method_id"));
				switchID.add(rsSwitchIDs.getInt("id"));
				// Hole die verschiedenen Bedienungen aus den Cases
				// und gleiche sie ab
				int startLine = -2;
				int stopLine = -2;
				numberOfSwitchIDs = extractCaseAttributes(rsSwitchIDs,
						caseNames, numberOfSwitchIDs, startLine, stopLine);

				// hole Case nodes vom selectierten Switch
				Statement stmtGetCaseNodes = connection.createStatement();
				String sqlCaseNodes = "SELECT * FROM casenode WHERE switch_id ="
						+ rsSwitchIDs.getInt("id") + ";";
				ResultSet rsCaseNodes = stmtGetCaseNodes
						.executeQuery(sqlCaseNodes);

				// Hole passende Attribute der passenden Klasse
				int AttrbuteAppearenceCounter = 0;
				while (rsCaseNodes.next() == true) {
					Statement stmtAttributList = connection.createStatement();
					String sqlAttributList = "SELECT * FROM METHODINVOCATIONS where class_id = "
							+ rsClassIDList.getInt("classID")
							+ " AND "
							+ " startline > "
							+ rsCaseNodes.getInt("startline")
							+ " AND "
							+ " startline < "
							+ (rsCaseNodes.getInt("stopline") + 1)
							+ " AND "
							+ " attribute_id = '" + attributeID + "';";
					ResultSet rsAttributList = stmtAttributList
							.executeQuery(sqlAttributList);

					if (rsAttributList.next() == true) {
						AttrbuteAppearenceCounter++;
					}
					rsAttributList.close();
					stmtAttributList.close();
				}
				if (AttrbuteAppearenceCounter >= caseNames.size()) {
					numberOfSwitchesWithPotentialState++;
				}
				rsCaseNodes.close();
				stmtGetCaseNodes.close();
			}
			// Analyse candidates and decide if or not.
			analyseCandidates(rsClassIDList, caseNames, numberOfSwitchIDs,
					numberOfSwitchesWithPotentialState, methodID);
			rsSwitchIDs.close();
		}
	}

	private void analyseCandidates(ResultSet rsClassIDList,
			HashMap<String, Integer> caseNames, int numberOfSwitchIDs,
			int numberOfSwitchesWithPotentialState, ArrayList<Integer> methodID)
			throws SQLException {
		if (numberOfSwitchesWithPotentialState >= numberOfSwitchIDs) {
			String result = "State found by class "
					+ rsClassIDList.getInt("classID") + " in Methodes "
					+ methodID.toString() + " Number of cases "
					+ numberOfSwitchesWithPotentialState + ":"
					+ caseNames.toString();

			int numberOfMethodes = methodID.size();
			int numberOfStates = caseNames.size();
			boolean found = false;
			String level = "";
			if (numberOfMethodes >= 6 && numberOfStates >= 7) {
				level = "Recommended";
				System.out.println("Empfohlen: " + result);
				found = true;
			} else if (numberOfMethodes >= 4 && numberOfStates >= 5) {
				level = "Possible";
				found = true;
			} else if (numberOfMethodes > 2 && numberOfStates > 2) {
				level = "Useful";
				found = true;
			}

			if (found == true) {
				Statement stmt2 = this.connection.createStatement();
				String sqlResult = "INSERT into ruleResults (ruleName,class_id,smellLevel,result)"
						+ "VALUES ("
						+ "'State', "
						+ rsClassIDList.getInt("classID")
						+ ",'"
						+ level
						+ "','" + result + "');";
				stmt2.executeUpdate(sqlResult);
				System.out.println("Ergebnis: " + sqlResult);
				stmt2.close();
			}
			connection.commit();
		}
	}

	/**
	 * select * from RULERESULTS where RULENAME = 'Strategy'
	 * 
	 * DELETE FROM RULERESULTS where RULENAME = 'Strategy'
	 */

	private void checkStrategyWithAttributes(ResultSet rsClassIDList,
			ResultSet rsAppearence, ResultSet rsParamList) throws SQLException {
		Statement stmtArgumentList = this.connection.createStatement();
		String sqlGetAttribList = " SELECT count(s.attribut_id) as appearence, s.attribut_id"
				+ " FROM switchnodes AS s "
				+ " JOIN methodes AS m ON m.id = s.method_id "
				+ " JOIN classes AS c ON c.id = m.class_id "
				+ " WHERE c.id =  "
				+ rsClassIDList.getInt("classID")
				+ " AND s.count = "
				+ rsAppearence.getInt("count")
				+ " AND s.count > 2 "
				+ " AND s.attribut_id > -1 "
				+ " GROUP BY s.attribut_id " + " HAVING appearence > 1;";
		ResultSet rsAttribList = stmtArgumentList
				.executeQuery(sqlGetAttribList);
		while (rsAttribList.next()) {
			// Hole die IDs die den gleichen Parameter und in der
			// gleichen Klasse sind.
			int attributeID = rsAttribList.getInt("attribut_id");
			// Hole die Switches die gleiche Attributs ID verwenden.
			ResultSet rsSwitchIDs = getSwitchIDs(rsClassIDList, rsAppearence,
					attributeID, "s.attribut_id");
			HashMap<String, Integer> caseNames = new HashMap<String, Integer>();
			int numberOfSwitchIDs = 0;
			ArrayList<Integer> methodID = new ArrayList<Integer>();
			while (rsSwitchIDs.next()) {
				methodID.add(rsSwitchIDs.getInt("method_id"));
				// Hole die verschiedenen Bedienungen aus den Cases
				// und gleiche sie ab
				int startLine = -2;
				int stopLine = -2;
				/**
				 * Hole die switchIDs aus dem Strategy-Ergebnis die auch im Body
				 * das gleich Attribute haben
				 */
				numberOfSwitchIDs = extractCaseAttributes(rsSwitchIDs,
						caseNames, numberOfSwitchIDs, startLine, stopLine);
			}
			// Verarbeite Ergebnisse.
			findStrategyPattern(rsClassIDList, caseNames, numberOfSwitchIDs,
					methodID);
			rsSwitchIDs.close();
		}
		rsAttribList.close();
		stmtArgumentList.close();
		rsParamList.close();
	}

	private ResultSet checkStrategyWithParameters(ResultSet rsClassIDList,
			ResultSet rsAppearence) throws SQLException {
		// 3. Hole die Parameter ID Liste von den Switch nodes die
		// zuvor markiert wurden und deren Anzahl > 1 ist
		Statement stmtParamList = this.connection.createStatement();
		String sqlGetParamList = " SELECT count(s.param_id) as appearence, s.param_id as pid "
				+ " FROM switchnodes AS s "
				+ " JOIN methodes AS m ON m.id = s.method_id "
				+ " JOIN classes AS c ON c.id = m.class_id "
				+ " WHERE c.id = "
				+ rsClassIDList.getInt("classID")
				+ " AND s.count = "
				+ rsAppearence.getInt("count")
				+ " AND s.count > 2 "
				+ " AND s.param_id > -1 "
				+ " GROUP BY s.param_id "
				+ " HAVING appearence > 1;";
		ResultSet rsParamList = stmtParamList.executeQuery(sqlGetParamList);
		while (rsParamList.next()) {
			// Hole die IDs die den gleichen Parameter und in der
			// gleichen Klasse sind.
			ResultSet rsSwitchIDs = getSwitchIDs(rsClassIDList, rsAppearence,
					rsParamList.getInt("param_id"), "s.param_id");
			HashMap<String, Integer> caseNames = new HashMap<String, Integer>();
			int numberOfSwitchIDs = 0;
			ArrayList<Integer> methodID = new ArrayList<Integer>();
			// ArrayList<Integer> switchID = new ArrayList<Integer>();
			while (rsSwitchIDs.next()) {
				methodID.add(rsSwitchIDs.getInt("method_id"));
				// Hole die verschiedenen Bedienungen aus den Cases
				// und gleiche sie ab
				int startLine = -2;
				int stopLine = -2;
				numberOfSwitchIDs = extractCaseAttributes(rsSwitchIDs,
						caseNames, numberOfSwitchIDs, startLine, stopLine);
			}
			// Verarbeite Ergebnisse.
			findStrategyPattern(rsClassIDList, caseNames, numberOfSwitchIDs,
					methodID);
		}
		rsParamList.close();
		return rsParamList;
	}

	private int extractCaseAttributes(ResultSet rsSwitchIDs,
			HashMap<String, Integer> caseNames, int numberOfSwitchIDs,
			int startLine, int stopLine) throws SQLException {
		Statement caseNodeSearch = this.connection.createStatement();
		String sqlGetCases = "SELECT *" + " FROM casenode "
				+ "WHERE switch_id = " + rsSwitchIDs.getString("id") + " ;";
		ResultSet rsCaseNodes = caseNodeSearch.executeQuery(sqlGetCases);
		numberOfSwitchIDs++;
		boolean firstNode = true;
		while (rsCaseNodes.next()) {
			if (firstNode == true) {
				startLine = rsCaseNodes.getInt("startline");
				firstNode = false;
			}
			if (caseNames.containsKey(rsCaseNodes.getString("name")) == false) {
				caseNames.put(rsCaseNodes.getString("name"), 1);
			} else {
				int countValue = caseNames.get(rsCaseNodes.getString("name")) + 1;
				caseNames.put(rsCaseNodes.getString("name"), countValue);
			}
			if (rsCaseNodes.isLast() == true) {
				stopLine = rsCaseNodes.getInt("stopline");
			}
		}
		return numberOfSwitchIDs;
	}

	private void findStrategyPattern(ResultSet rsClassIDList,
			HashMap<String, Integer> caseNames, int numberOfSwitchIDs,
			List<Integer> methodID) throws SQLException {
		int result = 0;
		// Vergleiche die Abzahl der Casenodes ob sie übereinstimmen.
		for (Iterator<Entry<String, Integer>> nameIter = caseNames.entrySet()
				.iterator(); nameIter.hasNext();) {
			Entry<String, Integer> name = nameIter.next();
			if (name.getValue() == numberOfSwitchIDs) {
				result++;
			}
		}
		// Wenn Namen und Anzahl gleich dann Strategy found.
		if (result == caseNames.size()) {
			int anzahlAnMethoden = methodID.size();
			int anzahlAnCases = caseNames.size();

			String resultText = "Strategy found by class id "
					+ rsClassIDList.getInt("classID") + " in Methodes M1.1: "
					+ anzahlAnMethoden + ", ";

			for (Integer integer : methodID) {
				resultText = resultText.concat(integer + ", ");
			}
			resultText = resultText.concat("M1.3: " + anzahlAnCases + " "
					+ caseNames.toString());
			String level = "";
			boolean found = false;
			if (anzahlAnMethoden == 2 && anzahlAnCases >= 3) {
				level = "Possible";
				found = true;
			} else if (anzahlAnMethoden >= 3 && anzahlAnCases == 2) {
				level = "Possible";
				found = true;
			} else if (anzahlAnMethoden == 3 && anzahlAnCases >= 4) {
				level = "Useful";
				found = true;
			} else if (anzahlAnMethoden >= 4 && anzahlAnCases == 3) {
				level = "Useful";
				found = true;
			} else if (anzahlAnMethoden >= 4 && anzahlAnCases >= 4) {
				level = "Recommended";
				
				Statement stmt2 = this.connection.createStatement();;
				String sqlClassName = "SELECT name " + "FROM classes WHERE id = " + rsClassIDList.getInt("classID");
				ResultSet rsClassName = stmt2.executeQuery(sqlClassName);
				while(rsClassName.next()) {
					System.out.println("Empfohlen: " + "Strategy found by class id "
							+ rsClassName.getString("name") + " in Methodes M1.1: "
							+ anzahlAnMethoden + ", M1.3: " + anzahlAnCases + " "
									+ caseNames.toString());						
				}
				rsClassName.close();
				found = true;
			}
			if (found == true) {
				resultText = resultText.concat(" by CaseSize:"
						+ caseNames.size());
				Statement stmt2 = this.connection.createStatement();
				String sqlResult = "INSERT into ruleResults (ruleName,class_id,smellLevel,result)"
						+ "VALUES ("
						+ "'Strategy', "
						+ rsClassIDList.getInt("classID")
						+ ",'"
						+ level
						+ "','" + resultText + "');";
				stmt2.executeUpdate(sqlResult);
				stmt2.close();
				connection.commit();
			}
		}
	}

	private ResultSet getSwitchIDs(ResultSet rsClassIDList,
			ResultSet rsAppearence, int rsList, String listType)
			throws SQLException {
		Statement switchIDSearch = this.connection.createStatement();
		String sqlGetSwitchID = "SELECT s.id, s.attribut_id, s.param_id, s.method_id"
				+ " FROM switchnodes AS s"
				+ " JOIN methodes AS m ON m.id = s.method_id"
				+ " JOIN classes AS c ON c.id = m.class_id"
				+ " WHERE c.id = "
				+ rsClassIDList.getInt("classID")
				+ " AND s.count = "
				+ rsAppearence.getInt("count")
				+ " AND s.count > 2"
				+ " AND "
				+ listType + " = " + rsList + ";";
		ResultSet rsSwitchIDs = switchIDSearch.executeQuery(sqlGetSwitchID);
		return rsSwitchIDs;
	}
}
