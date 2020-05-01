package de.lv1871.oss.dmnmgr.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.node.ObjectNode;

import de.lv1871.oss.dmnmgr.api.model.DecisionTestRequest;
import de.lv1871.oss.dmnmgr.api.model.DecisionTestResponse;
import de.lv1871.oss.dmnmgr.api.model.DecisionTestResponse.DecisionTestResponseBuilder;

import static de.lv1871.oss.tester.test.dmnassert.DmnTest.testExpectation;

@Service
public class DecisionTestService {

	private static final String ERROR_SIZE_NOT_CORRECT = "Die Anzahl der Treffer weicht von der Erwartung ab.";

	@Autowired
	private GenericDecisionService decisionService;

	@Autowired
	private VariableMapperService mapperService;

	public DecisionTestResponse testDecision(DecisionTestRequest request) {

		var engine = decisionService.deployAndCreateEngine(request.getXml());
		var decisionSimulationResponse = decisionService
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
			var message = decisionSimulationResponse.getMessage();

			for (ObjectNode expectedNode : request.getExpectedData()) {
				var expectedObjectMap = mapperService.getVariablesFromJsonAsMap(expectedNode);
				expectedDataAssertionFailed.addAll(testExpectation(decisionSimulationResponse, expectedObjectMap));
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

}
