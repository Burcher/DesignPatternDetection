package de.uma.phd.pmd.database;

import java.util.ArrayList;
import java.util.List;

public class Candidate {
	private String packageName = null;
	private int packageNumber = -2;
	
	private String className = null;
	private int	classNumber = -2;
	private boolean activeClass;
	
	private String methodName = null;
	private int methodeNumber = -2;
	
	private String methodInvocationName = null;
	private String methodInvocationClassType = null;
	private String methodInvocationCalledMethodName = null;
	
	private String attributeName = null;
	private String attributeType = null;
	private int attributeTypeID = -2;
	private String attributePackage = null;
	private int attributeID = -2;
	
	private boolean isConstructor = false;

	private int numberOfCases = -2;
	private int SwitchAttributeUseInCases =-2;
	
	private int startLine = -2;
	private int stopLine;
	private ArrayList<Candidate> caseNodteListe;
	private String extendedClassName = null;
	
	private ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
	private ArrayList<Parameter> methodAttrribute = new ArrayList<Parameter>();
	
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	
	public boolean isActiveClass() {
		return activeClass;
	}
	public void setActiveClass(boolean activeClass) {
		this.activeClass = activeClass;
	}
	
	public void setMethodName(String methodeName) {
		this.methodName = methodeName;
	}
	public String getMethodName() {
		return methodName;
	}
	
	public String getAttributeName() {
		return attributeName;
	}
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}
	
	public String getAttributeType() {
		return attributeType;
	}
	public void setAttributeType(String attributeTypeClass) {
		this.attributeType = attributeTypeClass;
	}
	public int getPackageNumber() {
		return packageNumber;
	}
	public void setPackageNumber(int packageNumber) {
		this.packageNumber = packageNumber;
	}
	public int getClassNumber() {
		return classNumber;
	}
	public void setClassNumber(int classNumber) {
		this.classNumber = classNumber;
	}
	
	public void setAttributePackage(String packageName) {
		this.attributePackage = packageName;
	}
	
	public String getAttributePackage() {
		return this.attributePackage;
	}
	
	public void setMethodNumber(int methodeNumber) {
		this.methodeNumber = methodeNumber;	
	}
	
	public int getMethodNumber() {
		return methodeNumber;
	}
	
	public void setMethodInvocationClassType(String invocation) {
		this.methodInvocationClassType = invocation;	
	}
	
	public String getMethodInvocationClassType() {
		return this.methodInvocationClassType;
	}
	public void setAttributeNumber(int id) {
		this.attributeID = id;
	}
	
	public int getAttributeNumber() {
		return this.attributeID;
	}
	
	public int getAttributeTypeID() {
		return attributeTypeID;
	}
	
	public void setAttributeTypeID(int attributeTypeID) {
		this.attributeTypeID = attributeTypeID;
	}
	public boolean isConstructor() {
		return isConstructor;
	}
	public void setConstructor(boolean isConstructor) {
		this.isConstructor = isConstructor;
	}

	public int getMethodeNumber() {
		return methodeNumber;
	}
	public void setMethodeNumber(int methodeNumber) {
		this.methodeNumber = methodeNumber;
	}
	public String getMethodInvocationCalledMethodName() {
		return methodInvocationCalledMethodName;
	}
	public void setMethodInvocationCalledMethodName(
			String methodInvocationCalledMethodName) {
		this.methodInvocationCalledMethodName = methodInvocationCalledMethodName;
	}
	

	public String getMethodInvocationName() {
		return this.methodInvocationName;
	}
	public void setMethodInvocationName(String methodInvocationName) {
		this.methodInvocationName = methodInvocationName;
	}
	
	
	public void setNumberOfCases(int size) {
		this.numberOfCases = size;
		
	}
	public int getNumberOfCases() {
		return this.numberOfCases;
	}
	
	public void setSwitchAttributeUseInCases(int numberOfUseSwitchAttribute) {
		this.SwitchAttributeUseInCases  = numberOfUseSwitchAttribute;
	}
	public int getSwitchAttributeUseInCases() {
		return SwitchAttributeUseInCases;
	}
	public int getStartLine() {
		return startLine;
	}
	public void setStartLine(int startLine) {
		this.startLine = startLine;
	}
	public void setStopLine(int endLine) {
		this.stopLine = endLine;	
	}
	
	public int getStopLine() {
		return this.stopLine;
	}
	public void addNodeList(ArrayList<Candidate> caseNodeList) {
		this.caseNodteListe = caseNodeList;
	}
	
	public ArrayList<Candidate> getCaseNodeList() {
		return this.caseNodteListe;
	}
	
	public void setExtendedClassName(String extendedClassName) {
		this.extendedClassName = extendedClassName;
	}
	public String getExtendedClassName() {
		return this.extendedClassName;
	}
	public ArrayList<Parameter> getParameterList() {
		return parameterList;
	}
	public void addParameterToList(Parameter parameter) {
		this.parameterList.add(parameter);
	}
	
	public void setParameterList(List<Parameter> list) {
		this.parameterList = (ArrayList<Parameter>) list;
	}
	
	public int getParameterListSize() {
		return parameterList.size();
	}
	
	public void addMethodAttributToList(Parameter methodAttribute) {
		this.methodAttrribute.add(methodAttribute);
	}
	
	public void setMethodAttributeList(ArrayList<Parameter> attributList) {
		this.methodAttrribute = attributList;
	}
	
	public List<Parameter> getMethodAttributeList() {
		return methodAttrribute;
	}
}
