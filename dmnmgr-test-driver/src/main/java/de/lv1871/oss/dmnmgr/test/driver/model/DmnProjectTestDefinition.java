package de.lv1871.oss.dmnmgr.test.driver.model;

import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class DmnProjectTestDefinition {

	private String name;
	private ObjectNode data;
	private List<ObjectNode> expectedData;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ObjectNode getData() {
		return data;
	}

	public void setData(ObjectNode data) {
		this.data = data;
	}

	public List<ObjectNode> getExpectedData() {
		return expectedData;
	}

	public void setExpectedData(List<ObjectNode> expectedData) {
		this.expectedData = expectedData;
	}
}
