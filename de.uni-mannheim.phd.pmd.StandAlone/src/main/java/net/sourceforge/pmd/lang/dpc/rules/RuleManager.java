package net.sourceforge.pmd.lang.dpc.rules;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.util.ArrayList;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.dpc.database.DBConnector;
import net.sourceforge.pmd.lang.dpc.rules.builder.BuilderRule;
import net.sourceforge.pmd.lang.dpc.rules.decorator.DecoratorRule;
import net.sourceforge.pmd.lang.dpc.rules.facade.FacadeRule;
import net.sourceforge.pmd.lang.dpc.rules.mediator.MediatorRule;
import net.sourceforge.pmd.lang.dpc.rules.singleton.SingletonRule;
import net.sourceforge.pmd.lang.dpc.rules.state.StateRule;

public class RuleManager {
	private ArrayList<PropertyChangeListener> listener = new ArrayList<PropertyChangeListener>();

	public RuleManager(Connection connection, RuleContext ctx) {
		@SuppressWarnings("unused")
		BuilderRule builder = new BuilderRule(this, connection);
		@SuppressWarnings("unused")
		FacadeRule facade = new FacadeRule(this, connection);
		@SuppressWarnings("unused")
		StateRule state = new StateRule(this, connection);
		@SuppressWarnings("unused")
		MediatorRule mediator = new MediatorRule(this, connection);
		// @SuppressWarnings("unused")
		// SingletonRule singleton = new SingletonRule(this, connector);
		@SuppressWarnings("unused")
		DecoratorRule decorator = new DecoratorRule(this, connection);
	}

	public void addListner(PropertyChangeListener builderRule) {
		this.listener.add(builderRule);
	}

	public void startAnalysis() {
		this.notifyListeners("Start", "StartAnalysis", "StartAnalysis",
				"StartAnalysis");
	}

	private void notifyListeners(Object object, String property,
			String oldValue, String newValue) {
		for (PropertyChangeListener name : listener) {
			name.propertyChange(new PropertyChangeEvent(this, "StartAnalysis",
					oldValue, newValue));
		}
	}
}
