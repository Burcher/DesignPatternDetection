package net.sourceforge.pmd.lang.dpc.rules.builder;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.sourceforge.pmd.lang.dpc.rules.RuleManager;

public class BuilderRule implements PropertyChangeListener {

	private Statement stmt = null;
	private Connection connection;

	/**
	 * @param args
	 */
	public BuilderRule(RuleManager rManager, Connection dbLink) {
		this.connection = dbLink;
		rManager.addListner(this);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		this.startAnalysis();
	}

	private void startAnalysis() {
		try {
			this.stmt = this.connection.createStatement();
			countConstructors();
			countParameters();
			this.stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Zaehle die Anzahl der Methoden die als Konstruktor gekennzeichnet sind
	 * pro Klasse.
	 * 
	 * @throws SQLException
	 */
	private void countParameters() throws SQLException {
		String sqlParameterCount = "SELECT id, numberOfParameters, class_id, name "
				+ "FROM methodes WHERE constructor = 1 "
				+ "AND numberOfParameters >= 2;";
		ResultSet rsNumberOfParameter = stmt.executeQuery(sqlParameterCount);
		while (rsNumberOfParameter.next()) {
			int parameterCount = rsNumberOfParameter
					.getInt("numberOfParameters");
			if (parameterCount >= 2) {
				int methodID = rsNumberOfParameter.getInt("id");
				int classID =  rsNumberOfParameter.getInt("class_id");
				String result = " Builder found in class: "
						+ classID
						+ " in Construtor " + methodID
						+ " with " + parameterCount + " Parameters.";

				if (parameterCount > 5) {
					insertCandidateInDB(methodID,
							classID,
							"Recommended", result);
					Statement stmt2 = this.connection.createStatement();;
					String sqlClassName = "SELECT name " + "FROM classes WHERE id = " + classID;
					ResultSet rsClassName = stmt2.executeQuery(sqlClassName);
					while(rsClassName.next()) {
						System.out.println("Empfohlen: " + " Builder found in class: "
								+ rsClassName.getString("name")
								+ " in Construtor " + rsNumberOfParameter.getString("name")
								+ " with " + parameterCount + " Parameters.");						
					}
					rsClassName.close();
				} else if (parameterCount >= 4) {
					insertCandidateInDB(rsNumberOfParameter.getInt("id"),
							rsNumberOfParameter.getInt("class_id"), "Possible",
							result);
				} else if (parameterCount >= 3) {
					insertCandidateInDB(rsNumberOfParameter.getInt("id"),
							rsNumberOfParameter.getInt("class_id"), "Useful",
							result);
				}
			}
		}
		rsNumberOfParameter.close();
	}

	private void insertCandidateInDB(int methodID, int classID, String level,
			String result) throws SQLException {
		Statement stmt2 = this.connection.createStatement();
		String sql = "";
		if (methodID == -3) {
			sql = "INSERT into ruleResults (ruleName,class_id,smellLevel,result)"
					+ "VALUES ("
					+ "'Builder Count Constructor', "
					+ classID
					+ ",'" + level + "','" + result + "');";
		} else {
			sql = "INSERT into ruleResults (ruleName, method_id, class_id, smellLevel,result)"
					+ "VALUES ("
					+ "'Builder Count Parameter', "
					+ methodID
					+ "," + classID + ",'" + level + "','" + result + "');";
		}
		stmt2.executeUpdate(sql);
		stmt2.close();
		connection.commit();
	}

	/**
	 * Zählt die Parameter pro Konstruktor.
	 * 
	 * @throws SQLException
	 */
	private void countConstructors() throws SQLException {
		ResultSet rsNumberOfClasses = this.stmt
				.executeQuery("SELECT COUNT(constructor) AS conamount, class_id"
						+ " FROM methodes "
						+ " WHERE  constructor = 1"
						+ " GROUP BY class_id HAVING conamount >= 2;");
		while (rsNumberOfClasses.next()) {
			int setSize = rsNumberOfClasses.getInt("conamount");
			if (setSize > 1) {
				String result = "Builder found in class "
						+ rsNumberOfClasses.getInt("class_id")
						+ " Number of Constructors = " + setSize;
				if (setSize > 5) {
					insertCandidateInDB(-3,
							rsNumberOfClasses.getInt("class_id"),
							"Recommended", result);
					Statement stmt2 = this.connection.createStatement();;
					String sqlClassName = "SELECT name " + "FROM classes WHERE id = " + rsNumberOfClasses.getInt("class_id");
					ResultSet rsClassName = stmt2.executeQuery(sqlClassName);
					while(rsClassName.next()) {
						System.out.println("Empfohlen: Builder found in class "
						+ rsClassName.getString("name")	
						+ " Number of Constructors = " + setSize);
					}
					rsClassName.close();
				} else if (setSize >= 4) {
					insertCandidateInDB(-3,
							rsNumberOfClasses.getInt("class_id"), "Possible",
							result);
				} else if (setSize >= 3) {
					insertCandidateInDB(-3,
							rsNumberOfClasses.getInt("class_id"), "Useful",
							result);
				}
			}
		}
		rsNumberOfClasses.close();
	}
}
