package de.uma.phd.pmd.database.initialization;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class FirstTimeDBInit {
	
	private Statement initStatement = null;
	private Connection connection = null;

	public FirstTimeDBInit(Connection dbconn) {
		this.connection = dbconn;
		this.createPackageTable();
		this.createClassTable();
		this.createClassExtensionTable();
		this.createMethodTable();
		this.createAttributeTable();
		this.createParameterTable();
		this.createMethodInvocationTable();
		this.createAnalysisResultTable();
		this.createConnectionTable();
		this.createSwitchNodeTable();
		this.createCaseConnectionTable();
	}

	private void createCaseConnectionTable() {
		try {
			initStatement = this.connection.createStatement();
			String sql = "CREATE TABLE IF NOT EXISTS caseNode "
					+ "(id								INTEGER PRIMARY KEY AUTO_INCREMENT(-1,1) NOT NULL,"
					+ " name	  						TEXT,"
					+ " startline  						INTEGER NOT NULL,"
					+ " stopline						INTEGER NOT NULL,"
					+ " switch_ID 						INTEGER NOT NULL,"
					+ " FOREIGN KEY(switch_id) REFERENCES switchNodes(id))";
			initStatement.executeUpdate(sql);
			initStatement.close();
		} catch (SQLException e) {

			e.printStackTrace();
		}
	}

	private void createSwitchNodeTable() {
		try {
			initStatement = this.connection.createStatement();
			String sql = "CREATE TABLE IF NOT EXISTS switchnodes "
					+ "(id								INTEGER PRIMARY KEY AUTO_INCREMENT(-1,1) NOT NULL,"
					+ " name							TEXT    NOT NULL,"
					+ " count							INTEGER NOT NULL,"
					+ "	method_id						INTEGER NOT NULL,"
					+ " attribut_id						INTEGER NOT NULL,"
					+ " param_ID						INTEGER NOT NULL,"
					+ " methodType_id 					INTEGER NOT NULL,"
					+ " FOREIGN KEY(method_id) REFERENCES methodes(id),"
					+ " FOREIGN KEY(attribut_id) REFERENCES attributes(id),"
					+ " FOREIGN KEY(param_id) REFERENCES parameters(id),"
					+ " FOREIGN KEY(methodType_id) REFERENCES methodes(id))";
			initStatement.executeUpdate(sql);
			initStatement.close();
			connection.commit();
			
			Statement stmt = connection.createStatement();
			sql = "INSERT into switchnodes (name, count, method_id, attribut_id, param_ID, methodType_id) "
					+ " VALUES ('unknown',-1,-1,-1,-1,-1);";

			stmt.executeUpdate(sql);
			stmt.close();
			connection.commit();

		} catch (SQLException e) {

			e.printStackTrace();
		}
	}

	private void createPackageTable() {
		try {
			initStatement = this.connection.createStatement();
			String sql = "CREATE TABLE IF NOT EXISTS packages "
					+ "(id INTEGER PRIMARY KEY AUTO_INCREMENT(-2,1),"
					+ "	name							TEXT    NOT NULL)";

			initStatement.executeUpdate(sql);
			initStatement.close();
			connection.commit();

			Statement stmt = connection.createStatement();
			sql = "INSERT into packages (name) " + " VALUES ('TEST');";

			stmt.executeUpdate(sql);
			stmt.close();
			connection.commit();
			stmt = connection.createStatement();
			sql = "INSERT into packages (name) " + " VALUES ('TEST');";

			stmt.executeUpdate(sql);
			stmt.close();
			connection.commit();
			stmt = connection.createStatement();
			sql = "SELECT * from packages";
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()) {
				System.out.println("ID: " + rs.getInt("id") + "name: " + rs.getString("name"));
			}
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void createClassTable() {
		try {
			// Name ActiveClass InCon OutCon PassivClasses UniqueIn UniqueOut
			// Attributes Methodes Variables Number of Realized Interface
			// Realized Interfaces isInterface isSuperclass Realzied From Number
			// of Subclasses Number of Instances Target Class
			initStatement = this.connection.createStatement();
			String sql = "CREATE TABLE IF NOT EXISTS classes "
					+ "(id								INTEGER PRIMARY KEY AUTO_INCREMENT(-1,1),"
					+ "	name							TEXT    NOT NULL, "
					+ " activeClass						TEXT    NOT NULL, "
					+ " package_id						INTEGER	NOT NULL,"
					+ " FOREIGN KEY(package_id) REFERENCES packages(id))";
			initStatement.executeUpdate(sql);
			initStatement.close();
			connection.commit();
			Statement stmt = connection.createStatement();
			String sql2 = "INSERT into Classes (name, activeClass, package_id)"
					+ "VALUES ('unknown','false',-1);";
			stmt.executeUpdate(sql2);
			stmt.close();
			connection.commit();
		} catch (SQLException e) {

			e.printStackTrace();
		}
	}

	private void createClassExtensionTable() {
		try {
			// Name ActiveClass InCon OutCon PassivClasses UniqueIn UniqueOut
			// Attributes Methodes Variables Number of Realized Interface
			// Realized Interfaces isInterface isSuperclass Realzied From Number
			// of Subclasses Number of Instances Target Class
			initStatement = this.connection.createStatement();
			String sql = "CREATE TABLE IF NOT EXISTS classExtension "
					+ "(extender_id						INTEGER NOT NULL,"
					+ "	origin_id						INTEGER NOT NULL,"
					+ " FOREIGN KEY(extender_id) REFERENCES classes(id),"
					+ " FOREIGN KEY(origin_id) REFERENCES classes(id))";
			initStatement.executeUpdate(sql);
			initStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	private void createMethodTable() {
		try {
			initStatement = this.connection.createStatement();
			String sql = "CREATE TABLE IF NOT EXISTS methodes "
					+ "(id 								INTEGER PRIMARY KEY AUTO_INCREMENT(-1,1),"
					+ " name							TEXT    NOT NULL,"
					+ "	class_id						INTEGER NOT NULL,"
					+ " constructor						INTEGER NOT NULL,"
					+ " numberOfParameters				INTEGER NOT NULL,"
					+ " FOREIGN KEY(class_id) REFERENCES classes(id))";
			initStatement.executeUpdate(sql);
			initStatement.close();

			Statement stmt = connection.createStatement();

			String sql2 = "INSERT into methodes (name, class_id, constructor, numberOfParameters)"
					+ " VALUES ('unknown',-1,-1,-1);";

			stmt.executeUpdate(sql2);
			stmt.close();
			connection.commit();

		} catch (SQLException e) {

			e.printStackTrace();
		}
	}

	private void createParameterTable() {
		try {
			initStatement = this.connection.createStatement();
			String sql = "CREATE TABLE IF NOT EXISTS parameters"
					+ "(id								INTEGER PRIMARY KEY AUTO_INCREMENT(-1,1) NOT NULL,"
					+ " name							TEXT    NOT NULL,"
					+ " type							TEXT    NOT NULL,"
					+ "	method_id						INTEGER NOT NULL,"
					+ "	attribute_id					INTEGER,"
					+ " FOREIGN KEY(method_id) REFERENCES methodes(id),"
					+ " FOREIGN KEY(attribute_id) REFERENCES attributes(id))";
			initStatement.executeUpdate(sql);
			initStatement.close();
			Statement stmt = connection.createStatement();
			sql = "INSERT into parameters (name, type, method_id, attribute_id) "
					+ " VALUES ('unknown','unknown',-1,-1);";

			stmt.executeUpdate(sql);
			stmt.close();
			connection.commit();
		} catch (SQLException e) {

			e.printStackTrace();
		}
	}

	private void createAttributeTable() {
		try {
			initStatement = this.connection.createStatement();
			String sql = "CREATE TABLE IF NOT EXISTS attributes "
					+ "(id								INTEGER PRIMARY KEY AUTO_INCREMENT(-1,1) NOT NULL,"
					+ " name							TEXT    NOT NULL,"
					+ " classType_id					INTEGER	NOT NULL,"
					+ "	method_id						INTEGER,"
					+ "	class_id						INTEGER NOT NULL,"
					+ "	entry_type						INTEGER NOT NULL,"
					+ "	count							INTEGER NOT NULL,"
					+ " FOREIGN KEY(classType_id) REFERENCES classes(id),"
					+ " FOREIGN KEY(method_id) REFERENCES methodes(id),"
					+ " FOREIGN KEY(class_id) REFERENCES classes(id))";
			initStatement.executeUpdate(sql);
			initStatement.close();
			Statement stmt = connection.createStatement();
			sql = "INSERT into attributes (name, classType_id, method_id, class_id, entry_type, count) "
					+ " VALUES ('unknown',-1,-1,-1,-1,-1);";

			stmt.executeUpdate(sql);
			stmt.close();
			connection.commit();
		} catch (SQLException e) {

			e.printStackTrace();
		}
	}

	private void createMethodInvocationTable() {
		try {
			initStatement = this.connection.createStatement();
			String sql = "CREATE TABLE IF NOT EXISTS methodinvocations "
					+ "(id								INTEGER PRIMARY KEY AUTO_INCREMENT(-1,1) NOT NULL,"
					+ " name							TEXT NOT NULL,"
					+ " classType_id					INTEGER	NOT NULL,"
					+ " methodType_id					INTEGER,"
					+ "	attribute_id					INTEGER," + "	method_id						INTEGER,"
					+ "	class_id						INTEGER NOT NULL,"
					+ " startline						INTEGER NOT NULL,"
					+ " FOREIGN KEY(classType_id) REFERENCES classes(id),"
					+ " FOREIGN KEY(methodType_id) REFERENCES methodes(id),"
					+ " FOREIGN KEY(attribute_id) REFERENCES attributes(id),"
					+ " FOREIGN KEY(method_id) REFERENCES methodes(id),"
					+ " FOREIGN KEY(class_id) REFERENCES classes(id))";
			initStatement.executeUpdate(sql);
			initStatement.close();
			Statement stmt = connection.createStatement();
			sql = "INSERT into methodinvocations (name, classType_id, methodType_id, attribute_id, class_id, startline) "
					+ " VALUES ('unknown',-1,-1,-1,-1,-1);";

			stmt.executeUpdate(sql);
			stmt.close();
			connection.commit();
		} catch (SQLException e) {

			e.printStackTrace();
		}
	}

	private void createAnalysisResultTable() {
		try {
			initStatement = this.connection.createStatement();
			String sql = "CREATE TABLE IF NOT EXISTS ruleResults "
					+ "(id								INTEGER PRIMARY KEY AUTO_INCREMENT(0,1) NOT NULL,"
					+ " ruleName						TEXT	NOT NULL,"
					+ "	attribute_id					INTEGER," + "	method_id						INTEGER,"
					+ "	class_id						INTEGER," + " package_id						INTEGER,"
					+ " result							INTEGER,"
					+ " FOREIGN KEY(attribute_id) REFERENCES attributes(id),"
					+ " FOREIGN KEY(method_id) REFERENCES methodes(id),"
					+ " FOREIGN KEY(class_id) REFERENCES classes(id),"
					+ " FOREIGN KEY(package_id) REFERENCES packages(id))";
			initStatement.executeUpdate(sql);
			initStatement.close();
			connection.commit();

		} catch (SQLException e) {

			e.printStackTrace();
		}
	}

	private void createConnectionTable() {
		try {
			initStatement = this.connection.createStatement();
			String sql = "CREATE TABLE IF NOT EXISTS connections"
					+ "(id								INTEGER PRIMARY KEY AUTO_INCREMENT(-1,1) NOT NULL,"
					+ "	attributeOrigin					INTEGER,"
					+ "	methodInvocationOrigin			INTEGER,"
					+ "	classOrigin						INTEGER,"
					+ " packageOrigin					INTEGER,"
					+ "	attributeTarget					INTEGER,"
					+ "	methodInvocationTarget			INTEGER,"
					+ "	classTarget						INTEGER,"
					+ " packageTarget					INTEGER,"
					+ " count							INTEGER,"
					+ " FOREIGN KEY(attributeOrigin) 		REFERENCES attributes(id),"
					+ " FOREIGN KEY(methodInvocationOrigin) REFERENCES methodinvocations (id),"
					+ " FOREIGN KEY(classOrigin) 			REFERENCES classes(id),"
					+ " FOREIGN KEY(packageOrigin) 			REFERENCES packages(id),"
					+ " FOREIGN KEY(attributeTarget) 		REFERENCES attributes(id),"
					+ " FOREIGN KEY(methodInvocationTarget) REFERENCES methodinvocations (id),"
					+ " FOREIGN KEY(classTarget) 			REFERENCES classes(id),"
					+ " FOREIGN KEY(packageTarget) 			REFERENCES packages(id))";
			initStatement.executeUpdate(sql);
			initStatement.close();

			Statement stmt = connection.createStatement();
			sql = "INSERT into connections (attributeOrigin, methodInvocationOrigin, "
					+ "classOrigin, packageOrigin, attributeTarget, methodInvocationTarget,"
					+ "classTarget, packageTarget, count) "
					+ " VALUES (-1,-1,-1,-1,-1,-1,-1,-1,-1);";

			stmt.executeUpdate(sql);
			stmt.close();
			connection.commit();

		} catch (SQLException e) {

			e.printStackTrace();
		}

	}
}