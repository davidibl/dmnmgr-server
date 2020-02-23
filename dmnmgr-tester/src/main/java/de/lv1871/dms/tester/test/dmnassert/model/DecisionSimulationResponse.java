package de.lv1871.dms.tester.test.dmnassert.model;

import java.util.List;
import java.util.Map;

public class DecisionSimulationResponse {

	private final String message;
	private final List<Map<String, Object>> result;
	private final List<String> resultRuleIds;
	private final Map<String, List<String>> resultTableRuleIds;

	private DecisionSimulationResponse(String message, List<Map<String, Object>> result, List<String> resultRuleIds,
			Map<String, List<String>> resultTableRuleIds) {
		this.message = message;
		this.result = result;
		this.resultRuleIds = resultRuleIds;
		this.resultTableRuleIds = resultTableRuleIds;
	}

	public Map<String, List<String>> getResultTableRuleIds() {
		return resultTableRuleIds;
	}

	public List<Map<String, Object>> getResult() {
		return result;
	}

	public String getMessage() {
		return message;
	}

	public List<String> getResultRuleIds() {
		return resultRuleIds;
	}

	public static class DecisionTestCaseResponseBuilder {

		private String message = null;
		private List<Map<String, Object>> result = null;
		private List<String> resultRuleIds = null;
		private Map<String, List<String>> resultTableRuleIds = null;

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
			return new DecisionSimulationResponse(message, result, resultRuleIds, resultTableRuleIds);
		}

		public DecisionTestCaseResponseBuilder withResultRuleIds(List<String> resultRuleIds) {
			this.resultRuleIds = resultRuleIds;
			return this;
		}

		public DecisionTestCaseResponseBuilder withResultTableRuleIds(Map<String, List<String>> resultTableRuleIds) {
			this.resultTableRuleIds = resultTableRuleIds;
			return this;
		}
	}

}
