package de.uma.phd.pmd.rules;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import de.uma.phd.pmd.database.DBConnector;
import de.uma.phd.pmd.rules.builder.BuilderRule;

public class RuleManager {
	private ArrayList<PropertyChangeListener> listener = new ArrayList<PropertyChangeListener>();
	private DBConnector connector = DBConnector.getConnector();
	
	public RuleManager() {
		BuilderRule builder = new BuilderRule(this, connector);
	}
	
	public void addListner(BuilderRule builderRule) {
		this.listener.add(builderRule);
	}
	
	public void startAnalysis() {
		this.notifyListeners("Start", "StartAnalysis", "StartAnalysis", "StartAnalysis");
	}
	
	private void notifyListeners(Object object, String property, String oldValue, String newValue) {
	    for (PropertyChangeListener name : listener) {
	      name.propertyChange(new PropertyChangeEvent(this, "StartAnalysis", oldValue, newValue));
	    }
	  }
}
