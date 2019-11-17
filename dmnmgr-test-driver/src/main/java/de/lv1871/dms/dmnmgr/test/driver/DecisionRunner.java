package de.lv1871.dms.dmnmgr.test.driver;

import java.util.HashMap;

import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.engine.ProcessEngine;

import com.fasterxml.jackson.databind.node.ObjectNode;

import de.lv1871.dms.dmnmgr.test.driver.model.DecisionSimulationResponse;
import de.lv1871.dms.dmnmgr.test.driver.model.DecisionSimulationResponse.DecisionTestCaseResponseBuilder;

public class DecisionRunner {

	private static VariableMapperService MAPPER = new VariableMapperService();

	public static DecisionSimulationResponse decide(ProcessEngine engine, String decisionKey,
			ObjectNode variablesNode) {

		// @formatter:off
		try {
			HashMap<String, Object> variables = MAPPER.getVariablesFromJsonAsMap(variablesNode);

			DmnDecisionTableResult decisionResult = engine
					.getDecisionService()
					.evaluateDecisionTableByKey(decisionKey, variables);

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
		// @formatter:on
	}
}
