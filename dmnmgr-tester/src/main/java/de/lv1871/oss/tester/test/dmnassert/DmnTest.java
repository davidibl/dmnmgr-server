package de.lv1871.oss.tester.test.dmnassert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.lv1871.oss.tester.test.dmnassert.model.DecisionSimulationResponse;

import static de.lv1871.oss.tester.test.dmnassert.DmnAssert.assertEqual;

public class DmnTest {

    public static List<Entry<String, Object>> testExpectation(
        DecisionSimulationResponse decisionSimulationResponse,
        Map<String, Object> expectedMap
    ) {
		List<Entry<String, Object>> expectedDataAssertionFailed = new ArrayList<>();

		for (Entry<String, Object> expectedEntry : expectedMap.entrySet()) {

			boolean isEqual = assertEqual(decisionSimulationResponse, expectedEntry);

			if (!isEqual) {
				expectedDataAssertionFailed.add(expectedEntry);
			}
		}
		return expectedDataAssertionFailed;
	}
}