package de.lv1871.dms.bpmgr.api.model;

import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class DecisionTestRequest {

	private String dmnTableId;
	private String decisionRequirementsId;
	private ObjectNode variables;
	private List<ObjectNode> expectedData;

	public ObjectNode getVariables() {
		return variables;
	}

	public void setVariables(ObjectNode variables) {
		this.variables = variables;
	}

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

	public String getDecisionRequirementsId() {
		return decisionRequirementsId;
	}

	public void setDecisionRequirementsId(String decisionRequirementsId) {
		this.decisionRequirementsId = decisionRequirementsId;
	}
}
