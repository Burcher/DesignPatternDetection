package de.uma.phd.pmd.rules.builder;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import de.uma.phd.pmd.database.DBConnector;
import de.uma.phd.pmd.rules.RuleManager;

public class BuilderRule implements PropertyChangeListener {

	private Statement stmt = null;
	private Connection connection;

	/**
	 * @param args
	 */
	public BuilderRule(RuleManager rManager, DBConnector dbLink) {
		this.connection = dbLink.getConnection();
		rManager.addListner(this);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		this.startAnalysis();

	}

	private void startAnalysis() {
		try {
			this.stmt = this.connection.createStatement();

			ResultSet rsNumberofClasses = this.stmt
					.executeQuery("SELECT COUNT(id) FROM classes");
			for (int i = 0; i < rsNumberofClasses.getInt("Count")-1; i++) {
				ResultSet rs = this.stmt
						.executeQuery("SELECT COUNT(constructor) FROM methodes"
								+ "WHERE class_id = " + i + "and constructor = 1");
				
				while (rs.next()) {		
					String sql = "INSERT into ruleResults (ruleName,class_id,result)" + "VALUES ('"
							+ "'Builder'," + i + ","+ rs.getInt("Count") + "');";
					this.stmt.executeQuery(sql);
				}
				rs.close();
			}
			rsNumberofClasses.close();
			this.stmt.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}
}
