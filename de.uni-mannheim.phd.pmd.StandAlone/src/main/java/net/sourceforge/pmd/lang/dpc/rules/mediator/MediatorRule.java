package net.sourceforge.pmd.lang.dpc.rules.mediator;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.lang.dpc.rules.RuleManager;

import org.jgraph.graph.DefaultEdge;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.StrongConnectivityInspector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DirectedSubgraph;

public class MediatorRule implements PropertyChangeListener {

	private Connection connection = null;
	private Statement stmt = null;

	public MediatorRule(RuleManager rManager, Connection connection) {
		this.connection = connection;
		rManager.addListner(this);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		this.startAnalysis();
	}

	private void startAnalysis() {

		// HashSet<Integer> alreadyUsedClasses = new HashSet<Integer>();

		try {
			this.stmt = this.connection.createStatement();
			String sql = "SELECT id FROM packages";
			ResultSet rsPackages = this.stmt.executeQuery(sql);
			DirectedGraph<Integer, DefaultEdge> packageCallGraph = null;
			while (rsPackages.next()) {
				packageCallGraph = buildConnectionGraph(rsPackages);
				analyseResults(rsPackages, packageCallGraph);
			}
			rsPackages.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	private DirectedGraph<Integer, DefaultEdge> buildConnectionGraph(
			ResultSet rsPackages) throws SQLException {
		DirectedGraph<Integer, DefaultEdge> packageCallGraph;
		packageCallGraph = new DefaultDirectedGraph<Integer, DefaultEdge>(
				DefaultEdge.class);
		HashMap<Integer, Integer> alreadyUsedClasses = new HashMap<Integer, Integer>();
		Statement stmtConnections = this.connection.createStatement();
		String sql2 = "SELECT con.id, con.classOrigin, con.classTarget "
				+ "FROM connections AS con WHERE con.classOrigin IN "
				+ "(SELECT id FROM classes AS ca " + "WHERE ca.package_id = "
				+ rsPackages.getInt("id") + ") " + "AND con.classTarget IN "
				+ "(SELECT id FROM classes AS ca " + "WHERE ca.package_id = "
				+ rsPackages.getInt("id") + ");";
		ResultSet rsConnections = stmtConnections.executeQuery(sql2);
		while (rsConnections.next()) {
			addClassVertex(packageCallGraph, alreadyUsedClasses, rsConnections,
					"classOrigin", rsPackages.getInt("id"));
			addClassVertex(packageCallGraph, alreadyUsedClasses, rsConnections,
					"classTarget", rsPackages.getInt("id"));
			// Add connection between Origin and Target
			packageCallGraph.addEdge(rsConnections.getInt("classOrigin"),
					rsConnections.getInt("classTarget"));
		}
		return packageCallGraph;
	}

	private void analyseResults(ResultSet rsPackages,
			DirectedGraph<Integer, DefaultEdge> packageCallGraph)
			throws SQLException {
		StrongConnectivityInspector<Integer, DefaultEdge> strongConnectivitys = new StrongConnectivityInspector<Integer, DefaultEdge>(
				packageCallGraph);
		List<DirectedSubgraph<Integer, DefaultEdge>> strongConnectsSets = strongConnectivitys
				.stronglyConnectedSubgraphs();

		for (int i = 0; i < strongConnectsSets.size(); i++) {

			int numberOfConnectionPartners = strongConnectsSets.get(i)
					.vertexSet().size();

			if (numberOfConnectionPartners > 2) {
				String result = ("Mediator found in package "
						+ rsPackages.getInt("id") + " with classes " + strongConnectsSets
						.get(i).toString());
				String level = "";
				if (numberOfConnectionPartners > 4) {
					level = "Recommended";

					ArrayList<String> classList = new ArrayList<String>();
					for (DirectedSubgraph<Integer, DefaultEdge> directedSubgraph : strongConnectsSets) {
						Set<Integer> set = directedSubgraph.vertexSet();

						for (Integer integer2 : set) {
							Statement stmt2 = this.connection.createStatement();
							;
							String sqlClassName = "SELECT name "
									+ "FROM classes WHERE id = "
									+ integer2.intValue();
							ResultSet rsClassName = stmt2
									.executeQuery(sqlClassName);
							while (rsClassName.next()) {
								classList.add(rsClassName.getString("name"));
							}
							rsClassName.close();
						}
					}

					Statement stmt2 = this.connection.createStatement();
					String sqlPackageName = "SELECT name "
							+ "FROM packages WHERE id = "
							+ rsPackages.getInt("id");
					ResultSet rsPackagaName = stmt2
							.executeQuery(sqlPackageName);

					while (rsPackagaName.next()) {
						System.out.println("Empfohlen: Mediator "
								+ rsPackagaName.getString("name") + classList.toString());
					}
					rsPackagaName.close();
					insertCandidateInDB(rsPackages, result, level);
				} else if (numberOfConnectionPartners == 3) {
					level = "Possible";
					insertCandidateInDB(rsPackages, result, level);
				} else if (numberOfConnectionPartners == 2) {
					level = "Useful";
					insertCandidateInDB(rsPackages, result, level);
				}
			}
		}
	}

	private void insertCandidateInDB(ResultSet rsPackages, String result,
			String level) throws SQLException {
		Statement stmt2 = this.connection.createStatement();
		String sqlResult = "INSERT into ruleResults (ruleName,package_id, smellLevel, result)"
				+ "VALUES ("
				+ "'Mediator', "
				+ rsPackages.getInt("id")
				+ ",'"
				+ level + "','" + result + "');";
		stmt2.executeUpdate(sqlResult);
		stmt2.close();
		connection.commit();
	}

	private void addClassVertex(
			DirectedGraph<Integer, DefaultEdge> packageCallGraph,
			HashMap<Integer, Integer> alreadyUsedClasses,
			ResultSet rsConnections, String tableIDName, int packageID)
			throws SQLException {
		if (alreadyUsedClasses.containsKey(rsConnections.getInt(tableIDName)) == false) {
			// Add Orgin Class to Graph
			packageCallGraph.addVertex(rsConnections.getInt(tableIDName));
			// Add Class to already used Classes
			alreadyUsedClasses
					.put(rsConnections.getInt(tableIDName), packageID);
		}
	}
}
