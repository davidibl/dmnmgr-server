package de.lv1871.dms.dmnmgr.test.driver.model;

import java.util.List;

public class DmnTestSuite {

	private String xml;
	private String name;
	private List<DmnTest> test;

	public List<DmnTest> getTest() {
		return test;
	}

	public void setTest(List<DmnTest> test) {
		this.test = test;
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
