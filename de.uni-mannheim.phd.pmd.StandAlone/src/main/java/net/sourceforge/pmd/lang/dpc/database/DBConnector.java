package net.sourceforge.pmd.lang.dpc.database;

import java.io.File;
import java.sql.*;

import org.apache.log4j.Logger;

import net.sourceforge.pmd.lang.dpc.database.initialization.FirstTimeDBInit;

public class DBConnector {
	static final Logger log = Logger.getRootLogger();
	private Connection connection = null;
	private Statement stmt = null;
	private static DBConnector instance = null;

	public static DBConnector getConnector() {
		if (instance == null) {
			instance = new DBConnector();
		}
		return instance;
	}

	public Connection getConnection() {
		return connection;
	}

	private DBConnector() {
		try {
			// 1. Registriere JDBC Treiber furr SQLite
			Class.forName("org.h2.Driver");
			this.createConnection();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			// System.exit(0);
		}

	}

	private void createConnection() throws SQLException {
		// 2. Erstelle eine Verbindung zur test.db. Egal ob diese Vorhanden ist
		// oder nicht. Nicht Vorhanden = neue DB erstellen.

			this.connection = DriverManager
					.getConnection("jdbc:h2:~/test2;FILE_LOCK=NO", "sa", "");
			new FirstTimeDBInit(connection);
		//	System.err.println("DB not exists. Create new one");
		//} else {
		//	database.delete();
		//	this.connection = DriverManager
		//			.getConnection("jdbc:sqlite:c:\\test.db");
		//}
		if (connection == null) {
			System.err.println("Keine Verbindung m�glich");
		} else {
			System.out.println("Opened database successfully");
		}
		this.connection.setAutoCommit(false);
	}

//	public void selectData() {
//		try {
//			this.stmt = this.connection.createStatement();
//
//			ResultSet rs = this.stmt.executeQuery("SELECT * FROM COMPANY;");
//			while (rs.next()) {
//				int id = rs.getInt("id");
//				String name = rs.getString("name");
//				int age = rs.getInt("age");
//				String address = rs.getString("address");
//				float salary = rs.getFloat("salary");
//				System.out.println("ID = " + id);
//				System.out.println("NAME = " + name);
//				System.out.println("AGE = " + age);
//				System.out.println("ADDRESS = " + address);
//				System.out.println("SALARY = " + salary);
//				System.out.println();
//			}
//			rs.close();
//			this.stmt.close();
//		} catch (SQLException e) {
//			System.err.println("SQL-Error: Select Operation");
//			System.err.println(e.getClass().getName() + ": " + e.getMessage());
//			log.error( "failed!", e );;
//			
//		}
//	}

	public void closeConnector() {
		try {
			this.connection.close();
		} catch (SQLException e) {
			System.err.println("SQL-Error: Closing Conneciton");
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			log.error( "failed!", e );;
		}
	}
}
