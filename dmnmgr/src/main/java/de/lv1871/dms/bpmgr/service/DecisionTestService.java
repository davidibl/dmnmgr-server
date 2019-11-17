package de.lv1871.dms.bpmgr.service;

import static de.lv1871.dms.tester.test.function.LambdaExtension.notNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.node.ObjectNode;

import de.lv1871.dms.bpmgr.api.model.DecisionSimulationResponse;
import de.lv1871.dms.bpmgr.api.model.DecisionTestRequest;
import de.lv1871.dms.bpmgr.api.model.DecisionTestResponse;
import de.lv1871.dms.bpmgr.api.model.DecisionTestResponse.DecisionTestResponseBuilder;

@Service
public class DecisionTestService {

	private static final String ERROR_SIZE_NOT_CORRECT = "Die Anzahl der Treffer weicht von der Erwartung ab.";

	@Autowired
	private GenericDecisionService decisionService;

	@Autowired
	private VariableMapperService mapperService;

	public DecisionTestResponse testDecision(DecisionTestRequest request) {

		DecisionSimulationResponse decisionSimulationResponse = decisionService
				.decide(request.getDecisionRequirementsId(), request.getDmnTableId(), request.getVariables());

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

		Map<String, Object> expectedMapRaw = mapperService.getVariablesFromJsonAsMap(expectedNode);

		// @formatter:off
		Map<String, Object> expectedMap = expectedMapRaw
				.entrySet()
				.stream()
				.filter(notNull(Entry::getValue))
				.collect(Collectors.toMap(Entry::getKey,  Entry::getValue));
		// @formatter:on

		for (Entry<String, Object> expectedEntry : expectedMap.entrySet()) {
			boolean isEqual = assertEqual(decisionSimulationResponse, expectedEntry);

			if (!isEqual) {
				expectedDataAssertionFailed.add(expectedEntry);
			}
		}
		return expectedDataAssertionFailed;
	}

	private boolean assertEqual(DecisionSimulationResponse decisionSimulationResponse,
			Entry<String, Object> expectedEntry) {
		// @formatter:off
		List<Entry<String, Object>> expectedDataNotFound =
				decisionSimulationResponse
					.getResult()
					.stream()
					.flatMap(map -> map.entrySet().stream())
					.filter(resultEntry -> equal(resultEntry, expectedEntry))
					.collect(Collectors.toList());
		// @formatter:on
		return expectedDataNotFound.size() >= 1;
	}

	private boolean equal(Entry<String, Object> resultEntry, Entry<String, Object> expectedEntry) {
		if (!resultEntry.getKey().equals(expectedEntry.getKey())) {
			return false;
		}
		return equal(resultEntry.getValue(), expectedEntry.getValue());
	}

	private boolean equal(Object result, Object expected) {
		if (result == null && expected == null) {
			return true;
		}
		if (result == null || expected == null) {
			return false;
		}
		if (result instanceof Number && expected instanceof Number) {
			return numbersEqual((Number) result, (Number) expected);
		}
		return result.equals(expected);
	}

	private boolean numbersEqual(Number n1, Number n2) {
		BigDecimal b1 = new BigDecimal(n1.doubleValue());
		BigDecimal b2 = new BigDecimal(n2.doubleValue());
		return b1.compareTo(b2) == 0;
	}
}
