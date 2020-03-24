package de.lv1871.oss.dmnmgr.test.driver.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DmnProject {

	private String dmnPath;
	private Map<String, DmnProjectTestContainer> testsuite;

	public String getDmnPath() {
		return dmnPath;
	}

	public void setDmnPath(String dmnPath) {
		this.dmnPath = dmnPath;
	}

	public Map<String, DmnProjectTestContainer> getTestsuite() {
		return testsuite;
	}

	public void setTestsuite(Map<String, DmnProjectTestContainer> testsuite) {
		this.testsuite = testsuite;
	}
}
