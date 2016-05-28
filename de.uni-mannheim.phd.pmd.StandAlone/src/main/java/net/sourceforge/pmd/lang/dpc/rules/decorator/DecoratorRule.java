package net.sourceforge.pmd.lang.dpc.rules.decorator;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.lang.dpc.rules.RuleManager;

import org.jgraph.graph.DefaultEdge;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.DefaultDirectedGraph;

public class DecoratorRule implements PropertyChangeListener {

	private Statement stmt = null;
	private Connection connection;

	public DecoratorRule(RuleManager rManager, Connection dbLink) {
		this.connection = dbLink;
		rManager.addListner(this);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		this.startAnalysis();
	}

	private void startAnalysis() {
		DirectedGraph<Integer, DefaultEdge> packageCallGraph = new DefaultDirectedGraph<Integer, DefaultEdge>(
				DefaultEdge.class);
		createClassHierarchy(packageCallGraph);
		analyseClassHierarchy(packageCallGraph);
	}

	private void analyseClassHierarchy(
			DirectedGraph<Integer, DefaultEdge> packageCallGraph) {
		ConnectivityInspector<Integer, DefaultEdge> inspector = new ConnectivityInspector<Integer, DefaultEdge>(
				packageCallGraph);
		// Returns a list of Set s, where each set contains all vertices that
		// are in the same maximally connected component.

		List<Set<Integer>> classHierachies = inspector.connectedSets();
		try {
			for (Iterator<Set<Integer>> iterator = classHierachies.iterator(); iterator
					.hasNext();) {
				int classID = -2;
				Set<Integer> set = (Set<Integer>) iterator.next();
				int setSize = set.size();
				if (setSize > 3) {
					// Hole Root-Klasse ID
					for (Integer integer : set) {
						if (packageCallGraph.inDegreeOf(integer) == 0) {
							classID = integer;
							break;
						}
					}

					Statement stmtGetLevel1 = this.connection.createStatement();
					String sqlGetLevel1 = "SELECT e.EXTENDER_ID "
							+ "FROM CLASSEXTENSION as e "
							+ "WHERE e.ORIGIN_ID = " + classID + ";";
					ResultSet rsLevel1Classes = stmtGetLevel1
							.executeQuery(sqlGetLevel1);

					Statement stmtGetLevel1Size = this.connection
							.createStatement();
					String sqlGetLevel1Size = "SELECT count(e.EXTENDER_ID) as size "
							+ "FROM CLASSEXTENSION as e "
							+ "WHERE e.ORIGIN_ID = " + classID + ";";
					ResultSet rsLevel1Size = stmtGetLevel1Size
							.executeQuery(sqlGetLevel1Size);

					if (rsLevel1Size.next() == true
							&& rsLevel1Size.getInt("size") >= 4) {
						int kandidateSize = rsLevel1Size.getInt("size");
						String level1Classes = "";
						while (rsLevel1Classes.next()) {
							level1Classes = level1Classes
									+ rsLevel1Classes.getInt("EXTENDER_ID")
									+ ";";
						}

						String result = "Decorator found with root class "
								+ classID + " and classes " + kandidateSize
								+ " : " + level1Classes;
						String level = "";
						if (kandidateSize > 8) {
							level = "Recommended";

							Statement stmt2 = this.connection.createStatement();
							;
							String sqlClassName = "SELECT name "
									+ "FROM classes WHERE id = " + classID;
							ResultSet rsClassName = stmt2
									.executeQuery(sqlClassName);
							while (rsClassName.next()) {
								System.out
										.println("Empfohlen: Decorator found with root class "
												+ rsClassName.getString("name"));
							}
							rsClassName.close();
						} else if (kandidateSize >= 6) {
							level = "Possible";
						} else if (kandidateSize >= 4) {
							level = "Useful";
						}

						Statement stmt2 = this.connection.createStatement();
						String sql = "INSERT INTO ruleResults (ruleName, class_id, smellLevel, result)"
								+ "values ("
								+ "'Decorator'"
								+ ","
								+ classID
								+ ",'" + level + "','" + result + "');";
						stmt2.execute(sql);
						stmt2.close();
						this.connection.commit();
						stmt2.close();
					}

					stmtGetLevel1.close();
					stmtGetLevel1Size.close();
					rsLevel1Classes.close();
					rsLevel1Size.close();

				}
			}
			this.connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void createClassHierarchy(
			DirectedGraph<Integer, DefaultEdge> packageCallGraph) {
		try {
			// TODO Graph speichern (Nur wo?)
			this.stmt = this.connection.createStatement();
			String sql = "SELECT * FROM classExtension";
			ResultSet rs = this.stmt.executeQuery(sql);
			while (rs.next()) {
				int extenderID = rs.getInt("extender_id");
				int originID = rs.getInt("origin_id");
				if (packageCallGraph.containsVertex(extenderID) == false) {
					packageCallGraph.addVertex(extenderID);
				}
				if (packageCallGraph.containsVertex(originID) == false) {
					packageCallGraph.addVertex(originID);
				}
				if (packageCallGraph.containsEdge(extenderID, originID) == false) {
					packageCallGraph.addEdge(originID, extenderID);
				}
			}
			this.stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
