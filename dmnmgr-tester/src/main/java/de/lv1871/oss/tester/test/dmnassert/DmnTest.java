package de.lv1871.oss.tester.test.dmnassert;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import de.lv1871.oss.tester.test.dmnassert.model.DecisionSimulationResponse;
import de.lv1871.oss.tester.test.function.ExtendedBiPredicate;

public class DmnTest {

    public static List<Entry<String, Object>> testExpectation(
        DecisionSimulationResponse decisionSimulationResponse,
        Map<String, Object> expectedMap
    ) {

		return expectedMap.entrySet()
			.stream()
			.filter(assertEntry.curryWith(decisionSimulationResponse).negate())
			.collect(Collectors.toList());
	}

	private static ExtendedBiPredicate<DecisionSimulationResponse, Entry<String, Object>> assertEntry =
		DmnAssert::assertEqual;
}