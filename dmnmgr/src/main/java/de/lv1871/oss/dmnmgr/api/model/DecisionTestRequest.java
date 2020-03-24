package de.lv1871.oss.dmnmgr.api.model;

import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class DecisionTestRequest extends DecisionSimulationRequest {

	private String dmnTableId;
	private List<ObjectNode> expectedData;

	public List<ObjectNode> getExpectedData() {
		return expectedData;
	}

	public void setExpectedData(List<ObjectNode> expectedData) {
		this.expectedData = expectedData;
	}

	public String getDmnTableId() {
		return dmnTableId;
	}

	public void setDmnTableId(String dmnTableId) {
		this.dmnTableId = dmnTableId;
	}
}
