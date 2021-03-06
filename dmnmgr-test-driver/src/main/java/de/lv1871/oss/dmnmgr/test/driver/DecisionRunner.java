package de.lv1871.oss.dmnmgr.test.driver;

import com.fasterxml.jackson.databind.node.ObjectNode;

import de.lv1871.oss.tester.test.dmnassert.model.DecisionSimulationResponse.DecisionTestCaseResponseBuilder;
import de.lv1871.oss.tester.test.domain.DecisionEngine;
import de.lv1871.oss.tester.test.dmnassert.model.DecisionSimulationResponse;

public class DecisionRunner {

	private static VariableMapperService MAPPER = new VariableMapperService();

	public static DecisionSimulationResponse decide(DecisionEngine engine, String decisionKey,
			ObjectNode variablesNode) {

		try {
			var variables = MAPPER.getVariablesFromJsonAsMap(variablesNode);

			var decisionResult = engine.evaluateDecisionByKey(decisionKey, variables);

			if (decisionResult
					.getResultList()
					.stream()
					.filter(result -> result.get(null) != null)
					.count() > 0) {
				return DecisionTestCaseResponseBuilder.create()
						.withMessage("Ein oder meherere Output-Felder haben keinen Namen.").build();
			}

			return DecisionTestCaseResponseBuilder
					.create()
					.withResult(decisionResult.getResultList())
					.build();
			
		} catch (Exception exception) {
			if (exception.getCause() != null) {
				return DecisionTestCaseResponseBuilder
						.create()
						.withMessage(exception.getCause().getMessage())
						.build();
			}
			return DecisionTestCaseResponseBuilder
					.create()
					.withMessage(exception.getMessage())
					.build();
		}
	}
}
