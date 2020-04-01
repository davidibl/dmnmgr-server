package de.lv1871.oss.dmnmgr.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.node.ObjectNode;

import de.lv1871.oss.dmnmgr.api.model.DecisionTestRequest;
import de.lv1871.oss.dmnmgr.api.model.DecisionTestResponse;
import de.lv1871.oss.dmnmgr.api.model.DecisionTestResponse.DecisionTestResponseBuilder;
import de.lv1871.oss.tester.test.dmnassert.model.DecisionSimulationResponse;
import de.lv1871.oss.tester.test.domain.DecisionEngine;

import static de.lv1871.oss.tester.test.dmnassert.DmnAssert.assertEqual;

@Service
public class DecisionTestService {

	private static final String ERROR_SIZE_NOT_CORRECT = "Die Anzahl der Treffer weicht von der Erwartung ab.";

	@Autowired
	private GenericDecisionService decisionService;

	@Autowired
	private VariableMapperService mapperService;

	public DecisionTestResponse testDecision(DecisionTestRequest request) {

		DecisionEngine engine = decisionService.deployAndCreateEngine(request.getXml());
		DecisionSimulationResponse decisionSimulationResponse = decisionService
				.decide(engine, request.getDmnTableId(), request.getVariables());

		if (decisionSimulationResponse.getResult() == null) {
			// @formatter:off
			return DecisionTestResponseBuilder
					.create()
					.withMessage(decisionSimulationResponse.getMessage())
					.build();
			// @formatter:on
		}

		try {

			List<Entry<String, Object>> expectedDataAssertionFailed = new ArrayList<>();
			String message = decisionSimulationResponse.getMessage();

			for (ObjectNode expectedNode : request.getExpectedData()) {
				expectedDataAssertionFailed.addAll(testExpectation(decisionSimulationResponse, expectedNode));
			}

			if (request.getExpectedData().size() != decisionSimulationResponse.getResult().size()) {
				message = Optional
						.ofNullable(message)
						.map(value -> String.format("%s; %s", value, ERROR_SIZE_NOT_CORRECT))
						.orElse(ERROR_SIZE_NOT_CORRECT);
			}
			// @formatter:off
			return DecisionTestResponseBuilder
					.create()
					.withResult(decisionSimulationResponse.getResult())
					.withResultRuleIds(decisionSimulationResponse.getResultRuleIds())
					.withExpectedDataAssertionFailed(expectedDataAssertionFailed)
					.withTestSucceeded(expectedDataAssertionFailed.size() < 1 &&
						!Optional.ofNullable(message).isPresent())
					.withMessage(message)
					.build();
			// @formatter:on
		} catch (Exception e) {
			return DecisionTestResponseBuilder.create().withMessage(e.getMessage()).build();
		}
	}

	private List<Entry<String, Object>> testExpectation(DecisionSimulationResponse decisionSimulationResponse,
			ObjectNode expectedNode) {
		List<Entry<String, Object>> expectedDataAssertionFailed = new ArrayList<>();

		Map<String, Object> expectedMap = mapperService.getVariablesFromJsonAsMap(expectedNode);

		for (Entry<String, Object> expectedEntry : expectedMap.entrySet()) {
			boolean isEqual = assertEqual(decisionSimulationResponse, expectedEntry);

			if (!isEqual) {
				expectedDataAssertionFailed.add(expectedEntry);
			}
		}
		return expectedDataAssertionFailed;
	}
}
