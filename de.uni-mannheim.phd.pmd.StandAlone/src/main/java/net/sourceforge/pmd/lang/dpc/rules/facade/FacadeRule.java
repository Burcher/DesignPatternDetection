package net.sourceforge.pmd.lang.dpc.rules.facade;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.h2.jdbc.JdbcSQLException;

import net.sourceforge.pmd.lang.dpc.rules.RuleManager;

public class FacadeRule implements PropertyChangeListener {
	private Connection connection = null;
	private Statement stmt = null;

	public FacadeRule(RuleManager rManager, Connection dbLink) {
		this.connection = dbLink;
		rManager.addListner(this);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		this.startAnalysis();
	}

	private void startAnalysis() {
		this.calculateConnections();
		this.calculateClassConnectsions();
		try {
			Statement searchPackages = this.connection.createStatement();
			String sqlPackage = "SELECT id FROM packages";
			ResultSet foundPackages = searchPackages.executeQuery(sqlPackage);
			while (foundPackages.next() == true) {
				int currentPackageID = foundPackages.getInt("id");
				// Analyse possible Candidates and decide if or not.
				analyseCandidates(currentPackageID);
				connection.commit();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	private void analyseCandidates(int currentPackageID) throws SQLException {
		// Get amount of incoming connections.
		ResultSet rsIn = getIncomingConnections(currentPackageID);
		// Get amount of outgoing connections.
		ResultSet rsOut = getOutgoingConnections(currentPackageID);
		// Get amount of internal connections.
		ResultSet rsInteral = getInternalConnections(currentPackageID);
		
		float inToClasses = 0, outToClasses = 0, internalToClasses = 0;
		int in = 0, out = 0, internal = 0, numberOfClasses = 0;
		numberOfClasses = getNumberOfClasses(currentPackageID, numberOfClasses);
		// Calculate relative numbers
		if (numberOfClasses > 0) {
			if (rsIn.next()) {
				in = rsIn.getInt("result");
				inToClasses = in / numberOfClasses;
			}
			if (rsOut.next()) {
				try {
					out = rsOut.getInt("result");
					outToClasses = out / numberOfClasses;	
				} catch (JdbcSQLException e) {
					System.out.println("out");
				}
			}
			if (rsInteral.next()) {
				try {
				internal = rsInteral.getInt("result");
				internalToClasses = internal / numberOfClasses;
				} catch (JdbcSQLException e) {
					System.out.println("internal");
				}
			}
		}
		boolean found = false;
		String level = "";
		String result = " with IN:" + in + ",IN%:" + inToClasses + ",OUT:" + out
				+ ",OUT%:" + outToClasses + ",Internal:" + internal
				+ ",Internal%:" + internalToClasses;
		if (inToClasses >= 0.79 && outToClasses <= 0.06
				&& internalToClasses >= 2.12) {
			level = "Recommended";

			Statement stmt2 = this.connection.createStatement();
			String sqlPackageName = "SELECT name "
					+ "FROM packages WHERE id = " + currentPackageID;
			ResultSet rsPackagaName = stmt2
					.executeQuery(sqlPackageName);
			while (rsPackagaName.next()) {
				System.out.println("Empfohlen: Facade found in " + rsPackagaName.getString("name")  + result);
			}
			rsPackagaName.close();
			
			found = true;
		} else if (inToClasses >= 0.38 && outToClasses <= 0.10
				&& internalToClasses >= 1.87) {
			level = "Useful";
			found = true;
		} else if (inToClasses >= 0.20 && outToClasses <= 0.30
				&& internalToClasses >= 0.51) {
			level = "Possible";
			found = true;
		}

		if (found == true) {
			Statement stmt2 = this.connection.createStatement();
			String sqlResult = "INSERT into ruleResults (ruleName,package_id,smellLevel,result)"
					+ "VALUES ("
					+ "'Facade', "
					+ currentPackageID
					+ ",'"
					+ level + "','" + result + "');";
			stmt2.executeUpdate(sqlResult);
			stmt2.close();
			found = false;
		}
	}

	private ResultSet getInternalConnections(int currentPackageID)
			throws SQLException {
		Statement stmtInternalConnections = this.connection.createStatement();
		String searchInternalConnections = "select sum(con.count) as result from connections as con "
				+ "join classes as clas on con.CLASSTARGET = clas.id "
				+ "where con.PACKAGEORIGIN =  "
				+ currentPackageID
				+ " and con.PACKAGETARGET = "
				+ currentPackageID
				+ "and clas.ACTIVECLASS = true and con.DIRECTION = 'Internal';";
		ResultSet rsInteral = stmtInternalConnections
				.executeQuery(searchInternalConnections);
		return rsInteral;
	}

	private ResultSet getOutgoingConnections(int currentPackageID)
			throws SQLException {
		Statement stmtOutConnections = this.connection.createStatement();
		String searchOutConnections = "select sum(con.count) as result from connections as con "
				+ "join classes as clas on con.CLASSTARGET = clas.id "
				+ "where con.PACKAGEORIGIN = "
				+ currentPackageID
				+ " and con.PACKAGETARGET > -1 and con.PACKAGETARGET <> "
				+ currentPackageID
				+ "and clas.ACTIVECLASS = true and con.DIRECTION = 'out';";
		ResultSet rsOut = stmtOutConnections.executeQuery(searchOutConnections);
		return rsOut;
	}

	/**
	 * Berechnet die Anzahl Eingehender Connections auf ein bestimmtes Package.
	 * Verbindungen innerhalb des gleichen Packages werden ausgeschlosen.
	 * 
	 * @param currentPackageID
	 *            untersuchtes package
	 * @return Anzahl Verbindungen.
	 * @throws SQLException
	 */
	private ResultSet getIncomingConnections(int currentPackageID)
			throws SQLException {
		Statement stmtInConnections = this.connection.createStatement();
		String searchInConnections = "select sum(con.count) as result from connections as con "
				+ "join classes as clas on con.CLASSORIGIN = clas.id "
				+ "where con.PACKAGETARGET =  "
				+ currentPackageID
				+ " and con.PACKAGEORIGIN > -1 "
				+ "and con.PACKAGEORIGIN <> "
				+ currentPackageID
				+ "and clas.ACTIVECLASS = true " + "and con.DIRECTION = 'int';";
		ResultSet rsIn = stmtInConnections.executeQuery(searchInConnections);
		return rsIn;
	}

	private int getNumberOfClasses(int currentPackageID, int numberOfClasses)
			throws SQLException {
		Statement stmtNumberOfClasses = this.connection.createStatement();
		String searchClassesPerPackage = "select count(*) as result from classes as cla "
				+ "join packages as pac on pac.id = cla.package_id  "
				+ "where cla.package_id = " + currentPackageID;
		ResultSet rsNumberOfClasses = stmtNumberOfClasses
				.executeQuery(searchClassesPerPackage);
		if (rsNumberOfClasses.next()) {
			numberOfClasses = rsNumberOfClasses.getInt("result");
		}
		return numberOfClasses;
	}

	private void calculateClassConnectsions() {
		try {
			this.stmt = this.connection.createStatement();
			String searchPackages = "SELECT id FROM packages;";
			ResultSet foundPackages = this.stmt.executeQuery(searchPackages);

			while (foundPackages.next()) {
				Statement stmtSearchForConnections = this.connection
						.createStatement();
				int currentPackageID = foundPackages.getInt("id");
				ResultSet foundInternals = getConnections(
						stmtSearchForConnections, currentPackageID, "=", "=");
				addConnection(foundInternals, "Internal");
				foundInternals.close();
				ResultSet foundOutgoing = getConnections(
						stmtSearchForConnections, currentPackageID, "=", "<>");
				addConnection(foundOutgoing, "out");
				foundOutgoing.close();
				ResultSet foundIncoming = getConnections(
						stmtSearchForConnections, currentPackageID, "<>", "=");
				addConnection(foundIncoming, "int");
				foundIncoming.close();
				stmtSearchForConnections.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private ResultSet getConnections(Statement stmtSearchForConnections,
			int currentPackageID, String operatorIn, String operatorOut)
			throws SQLException {
		String searchForInternals = "Select count(m.class_id) as count, m.CLASS_ID as outC, outC.PACKAGE_ID as outP, m.CLASSTYPE_ID as inC, inC.PACKAGE_ID as inP "
				+ "from METHODINVOCATIONS as m  "
				+ "join classes as outC on m.CLASS_ID = outC.ID "
				+ "join classes as inC on m.CLASSTYPE_ID = inC.id "
				+ "WHERE inC.PACKAGE_ID "
				+ operatorIn
				+ currentPackageID
				+ " and outC.PACKAGE_ID "
				+ operatorOut
				+ currentPackageID
				+ " GROUP BY m.CLASSTYPE_ID, m.CLASS_ID "
				+ "union "
				+ "SELECT a.count(*), a.CLASS_ID as outC, outC.PACKAGE_ID as outP,a.CLASSTYPE_ID as inC, inC.PACKAGE_ID as inP "
				+ "from ATTRIBUTESFORCONNECTIONS as a "
				+ "join CLASSES as outC on a.CLASS_ID = outC.id "
				+ "join CLASSES as inC on a.CLASSTYPE_ID = inC.id "
				+ "WHERE inC.PACKAGE_ID "
				+ operatorIn
				+ currentPackageID
				+ " and outC.PACKAGE_ID "
				+ operatorOut
				+ currentPackageID
				+ "GROUP BY a.CLASSTYPE_ID,a.CLASS_ID;";
		ResultSet foundInternals = stmtSearchForConnections
				.executeQuery(searchForInternals);
		return foundInternals;
	}

	private void addConnection(ResultSet foundInternals, String direction)
			throws SQLException {
		while (foundInternals.next()) {
			// Neue Verbindung anlegen.
			Statement stmtInsertConnections = this.connection.createStatement();
			String insertConnection = "INSERT INTO connections (classOrigin, packageOrigin, classTarget, packageTarget, count, direction) values ("
					+ foundInternals.getInt("outC")
					+ ","
					+ foundInternals.getInt("OutP")
					+ ","
					+ foundInternals.getInt("inC")
					+ ","
					+ foundInternals.getInt("inP")
					+ ","
					+ foundInternals.getInt("count") + ",'" + direction + "');";
			stmtInsertConnections.executeUpdate(insertConnection);
			stmtInsertConnections.close();
		}
	}

	private void calculateConnections() {
		try {
			this.stmt = this.connection.createStatement();
			// Zähle alle Klassen die irgendwo als Attribute angelegt sind und
			// verbinde die Ergebnisse mit allen Klassen die irgendwo von einer
			// Methoden aufgerufen werden.
			String sql = "SELECT COUNT(classtype_id), classtype_id, class_id, package_id "
					+ "FROM attributesForConnections, classes "
					+ "WHERE attributesForConnections.class_id = classes.id "
					+ "GROUP BY classtype_id, class_id "
					+ "UNION ALL "
					+ "SELECT COUNT(classtype_id), classtype_id, class_id, package_id "
					+ "FROM methodinvocations, classes "
					+ "WHERE methodinvocations.class_id = classes.id "
					+ "GROUP BY classType_id, class_id;";
			ResultSet result = this.stmt.executeQuery(sql);

			while (result.next()) {
				Statement stmtSearchForConnections = this.connection
						.createStatement();
				// Hole die Zeilen heraus die eine Verbindung von Klasse A
				// (Original) zu einer anderen Klasse B repräsentiert
				ResultSet rsSearchForConnections = stmtSearchForConnections
						.executeQuery("SELECT count " + "FROM connections "
								+ "WHERE classOrigin = "
								+ result.getInt("class_id")
								+ " AND classTarget = "
								+ result.getInt("classtype_id") + ";");
				if (rsSearchForConnections.next() == true) {
					// Wenn Verbindung schon bekannt dann Update
					Statement stmtUpdateCount = this.connection
							.createStatement();
					int newCount = result.getInt("COUNT(classtype_id)")
							+ rsSearchForConnections.getInt("count");
					sql = "UPDATE connections SET count=" + newCount
							+ " WHERE classOrigin = "
							+ result.getInt("class_id") + " AND classTarget = "
							+ result.getInt("classtype_id") + ";";
					stmtUpdateCount.executeUpdate(sql);
					stmtUpdateCount.close();
				} else {
					int targetPackageID = -2;
					Statement stmtGetPackageForTarget = this.connection
							.createStatement();
					sql = "SELECT package_id FROM classes WHERE id = '"
							+ result.getInt("classtype_id") + "';";
					ResultSet rsTargetPackageID = stmtGetPackageForTarget
							.executeQuery(sql);
					while (rsTargetPackageID.next() == true) {
						targetPackageID = rsTargetPackageID
								.getInt("package_id");
					}
					;
					stmtGetPackageForTarget.close();
					rsTargetPackageID.close();
					// Ansonsten Neue Verbindung anlegen.
					Statement stmtInsertConnections = this.connection
							.createStatement();
					sql = "INSERT INTO connections (classOrigin, packageOrigin, classTarget, packageTarget, count) values ("
							+ result.getInt("class_id")
							+ ","
							+ result.getInt("package_id")
							+ ","
							+ result.getInt("classtype_id")
							+ ","
							+ targetPackageID
							+ ","
							+ result.getInt("COUNT(classtype_id)") + ");";
					stmtInsertConnections.executeUpdate(sql);
					stmtInsertConnections.close();

				}
				connection.commit();
			}
			this.stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
