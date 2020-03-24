package de.lv1871.oss.dmnmgr.api.model;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class DecisionTestResponse {

	private final String message;
	private final List<Map<String, Object>> result;
	private final List<String> resultRuleIds;
	private final Boolean testSucceded;
	private final List<Entry<String, Object>> expectedDataAssertionFailed;

	private DecisionTestResponse(String message, List<Map<String, Object>> result, List<String> resultRuleIds,
			Boolean testSucceded, List<Entry<String, Object>> expectedDataAssertionFailed) {
		this.message = message;
		this.result = result;
		this.resultRuleIds = resultRuleIds;
		this.testSucceded = testSucceded;
		this.expectedDataAssertionFailed = expectedDataAssertionFailed;
	}

	public String getMessage() {
		return message;
	}

	public List<Map<String, Object>> getResult() {
		return result;
	}

	public List<String> getResultRuleIds() {
		return resultRuleIds;
	}

	public Boolean getTestSucceded() {
		return testSucceded;
	}

	public List<Entry<String, Object>> getExpectedDataAssertionFailed() {
		return expectedDataAssertionFailed;
	}

	public static class DecisionTestResponseBuilder {

		private String message = null;
		private List<Map<String, Object>> result = null;
		private List<String> resultRuleIds = null;
		private List<Entry<String, Object>> expectedDataAssertionFailed;
		private Boolean testSucceded = false;

		public static DecisionTestResponseBuilder create() {
			return new DecisionTestResponseBuilder();
		}

		public DecisionTestResponseBuilder withMessage(String message) {
			this.message = message;
			return this;
		}

		public DecisionTestResponseBuilder withResultRuleIds(List<String> resultRuleIds) {
			this.resultRuleIds = resultRuleIds;
			return this;
		}

		public DecisionTestResponseBuilder withResult(List<Map<String, Object>> result) {
			this.result = result;
			return this;
		}

		public DecisionTestResponseBuilder withExpectedDataAssertionFailed(
				List<Entry<String, Object>> expectedDataAssertionFailed) {
			this.expectedDataAssertionFailed = expectedDataAssertionFailed;
			return this;
		}

		public DecisionTestResponseBuilder withTestSucceeded(Boolean testSucceded) {
			this.testSucceded = testSucceded;
			return this;
		}

		public DecisionTestResponse build() {
			return new DecisionTestResponse(message, result, resultRuleIds, testSucceded, expectedDataAssertionFailed);
		}
	}
}
