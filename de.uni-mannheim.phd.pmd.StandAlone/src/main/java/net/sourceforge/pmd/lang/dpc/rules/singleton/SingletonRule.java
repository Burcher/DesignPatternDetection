package net.sourceforge.pmd.lang.dpc.rules.singleton;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.sourceforge.pmd.lang.dpc.database.DBConnector;
import net.sourceforge.pmd.lang.dpc.rules.RuleManager;



public class SingletonRule implements PropertyChangeListener {
	private Connection connection = null;
	private Statement stmt = null;

	public SingletonRule(RuleManager rManager, DBConnector dbLink) {
		this.connection = dbLink.getConnection();
		rManager.addListner(this);
	}


	public void propertyChange(PropertyChangeEvent evt) {
		this.startAnalysis();
	}

	private void startAnalysis() {
		try {
			//
			
			this.stmt = this.connection.createStatement();
			String sql = "SELECT classtype_id, count(count) as result FROM attributes WHERE classtype_id <> -1 GROUP BY classtype_id;";
			ResultSet rs = this.stmt.executeQuery(sql);
			
			while(rs.next() == true) {
				Statement stmt2 = this.connection.createStatement();
				int count = rs.getInt("result");
				if(count > 0) {
					sql = "INSERT INTO ruleResults (ruleName, class_id, result)" 
							+ "values ("
							+ "'Singleton'"
							+ ","
							+ rs.getInt("classtype_id")
							+ ","
							+ rs.getInt("result")
							+ ");";
					stmt2.execute(sql);	
					stmt2.close();
					this.connection.commit();
				}	
			}
			this.stmt.close();
			
			this.connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
