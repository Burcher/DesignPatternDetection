package net.sourceforge.pmd.lang.dpc.database;

import java.util.ArrayList;

public interface ICandidate {

	public void addClass(Candidate candidate);
	public ArrayList<String> getListOfClasses();
}
