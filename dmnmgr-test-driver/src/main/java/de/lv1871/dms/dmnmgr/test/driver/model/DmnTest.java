package de.lv1871.dms.dmnmgr.test.driver.model;

import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class DmnTest {

	private String name;
	private String tableId;
	private ObjectNode data;
	private List<ObjectNode> expectedData;

	public List<ObjectNode> getExpectedData() {
		return expectedData;
	}

	public void setExpectedData(List<ObjectNode> expectedData) {
		this.expectedData = expectedData;
	}

	public ObjectNode getData() {
		return data;
	}

	public void setData(ObjectNode data) {
		this.data = data;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTableId() {
		return tableId;
	}

	public void setTableId(String tableId) {
		this.tableId = tableId;
	}

}
