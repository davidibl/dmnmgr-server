package de.lv1871.dms.bpmgr.api.model;

public class OpenApiDefinitionResponse {

	private String apiUrl;
	private String swaggerDefinition;

	public String getApiUrl() {
		return apiUrl;
	}

	public void setApiUrl(String apiUrl) {
		this.apiUrl = apiUrl;
	}

	public String getSwaggerDefinition() {
		return swaggerDefinition;
	}

	public void setSwaggerDefinition(String swaggerDefinition) {
		this.swaggerDefinition = swaggerDefinition;
	}
}
