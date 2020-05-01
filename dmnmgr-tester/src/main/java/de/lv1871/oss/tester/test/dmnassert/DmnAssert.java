package de.lv1871.oss.tester.test.dmnassert;

import java.util.Map.Entry;

import de.lv1871.oss.tester.test.dmnassert.model.DecisionSimulationResponse;
import static de.lv1871.oss.tester.test.dmnassert.DmnVariableAssert.equal;

public class DmnAssert {

    public static <T extends Entry<String, Object>> boolean assertEqual(
        final DecisionSimulationResponse decisionSimulationResponse,
        final T expectedEntry
    ) {

		if (expectedEntry.getValue() == null) {
			return decisionSimulationResponse
				.getResult()
				.stream()
				.flatMap(map -> map.entrySet().stream())
				.filter(entry -> entry.getKey() == expectedEntry.getKey() && entry.getValue() != null)
				.count() < 1;
		}

		return decisionSimulationResponse
					.getResult()
					.stream()
					.flatMap(map -> map.entrySet().stream())
					.filter(resultEntry -> equal(resultEntry, expectedEntry))
					.count() >= 1;
    }
    
}