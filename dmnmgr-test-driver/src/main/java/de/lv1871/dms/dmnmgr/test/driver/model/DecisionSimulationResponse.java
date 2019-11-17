package de.lv1871.dms.dmnmgr.test.driver.model;

import java.util.List;
import java.util.Map;

public class DecisionSimulationResponse {

	private final String message;
	private final List<Map<String, Object>> result;

	private DecisionSimulationResponse(String message, List<Map<String, Object>> result) {
		this.message = message;
		this.result = result;
	}

	public List<Map<String, Object>> getResult() {
		return result;
	}

	public String getMessage() {
		return message;
	}

	public static class DecisionTestCaseResponseBuilder {

		private String message = null;
		private List<Map<String, Object>> result = null;

		public static DecisionTestCaseResponseBuilder create() {
			return new DecisionTestCaseResponseBuilder();
		}

		public DecisionTestCaseResponseBuilder withMessage(String message) {
			this.message = message;
			return this;
		}

		public DecisionTestCaseResponseBuilder withResult(List<Map<String, Object>> result) {
			this.result = result;
			return this;
		}

		public DecisionSimulationResponse build() {
			return new DecisionSimulationResponse(message, result);
		}
	}

}
