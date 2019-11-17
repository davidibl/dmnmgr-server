package de.lv1871.dms.tester.test.dmnassert;

import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import de.lv1871.dms.tester.test.dmnassert.model.DecisionSimulationResponse;
import static de.lv1871.dms.tester.test.dmnassert.DmnVariableAssert.equal;

public class DmnAssert {

    public static boolean assertEqual(
        final DecisionSimulationResponse decisionSimulationResponse,
        final Entry<String, Object> expectedEntry
    ) {
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
    
}