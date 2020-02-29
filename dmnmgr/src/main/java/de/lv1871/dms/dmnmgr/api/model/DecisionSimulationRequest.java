package de.lv1871.dms.dmnmgr.api.model;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class DecisionSimulationRequest {

	private String dmnTableId;
	private String xml;
	private ObjectNode variables;

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

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
