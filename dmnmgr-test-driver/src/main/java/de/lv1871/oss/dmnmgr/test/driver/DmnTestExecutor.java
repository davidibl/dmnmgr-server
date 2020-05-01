package de.lv1871.oss.dmnmgr.test.driver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.junit.Assert;

import com.fasterxml.jackson.databind.node.ObjectNode;

import de.lv1871.oss.dmnmgr.test.driver.model.DmnTest;
import junit.framework.TestCase;
import static de.lv1871.oss.tester.test.dmnassert.DmnTest.testExpectation;
import de.lv1871.oss.tester.test.dmnassert.model.DecisionSimulationResponse;
import de.lv1871.oss.tester.test.domain.DecisionEngine;

public class DmnTestExecutor extends TestCase {

	private static String ASSERTION_ERROR_MESSAGE_TEMPLATE = 
			"\n--------------------------------------------\n" +
			"Expected Data:\n" +
			"%s\n\n" +
			"--------------------------------------------\n" +
			"Result:\n" +
			"%s";

	private static VariableMapperService MAPPER = new VariableMapperService();

	private final DmnTest test;
    private DecisionEngine dmnEngine;

	public DmnTestExecutor(DmnTest test, DecisionEngine engine) {
		super(test.getName());
		this.test = test;
		this.dmnEngine = engine;
	}

	protected void runTest() throws Throwable {
		testDecision();
	}

	public void testDecision() {
		var decisionSimulationResponse = DecisionRunner.decide(
				dmnEngine,
				this.test.getTableId(),
				this.test.getData());

		if (decisionSimulationResponse.getResult() == null) {
			fail(decisionSimulationResponse.getMessage());
		}

		try {

			List<Entry<String, Object>> expectedDataAssertionFailed = new ArrayList<>();

			for (ObjectNode expectedNode : this.test.getExpectedData()) {
				HashMap<String, Object> expectedObjectMap = MAPPER.getVariablesFromJsonAsMap(expectedNode);
				expectedDataAssertionFailed.addAll(testExpectation(decisionSimulationResponse, expectedObjectMap));
			}

			var testFailed = expectedDataAssertionFailed.size() > 0;

			var resultMessage = testFailed ? getFailureMessage(decisionSimulationResponse) : "";

			Assert.assertFalse(resultMessage, testFailed);

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	private String getFailureMessage(DecisionSimulationResponse decisionSimulationResponse) {
		var expectedData = MAPPER.writeJson(this.test.getExpectedData());
		var result = MAPPER.writeJson(decisionSimulationResponse.getResult());

		return decisionSimulationResponse.getMessage() == null
				? String.format(ASSERTION_ERROR_MESSAGE_TEMPLATE, expectedData, result)
				: decisionSimulationResponse.getMessage();
	}

}
