package de.lv1871.oss.dmnmgr.api.model;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class DecisionSimulationRequest extends DecisionRequest {

	private String dmnTableId;
	private ObjectNode variables;

	public ObjectNode getVariables() {
		return variables;
	}

	public void setVariables(ObjectNode variables) {
		this.variables = variables;
	}

	public String getDmnTableId() {
		return dmnTableId;
	}

	public void setDmnTableId(String dmnTableId) {
		this.dmnTableId = dmnTableId;
	}
}
