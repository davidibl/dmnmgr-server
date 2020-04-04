package de.lv1871.oss.tester.test.dmnassert;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import de.lv1871.oss.tester.test.dmnassert.model.DecisionSimulationResponse;

public class DmnTestTest {

    @Test
    public void testTestExpactationAllEqual() {
        DecisionSimulationResponse decisionSimulationResponse = createResponse(Arrays.asList(
            createResponseEntry("test", 1),
            createResponseEntry("zweitesAttribut", 3)
        ));
        Map<String, Object> expectedMap = createResponseEntry("test", 1);

        List<Entry<String, Object>> testExpectation = DmnTest.testExpectation(decisionSimulationResponse, expectedMap);

        assertEquals(0, testExpectation.size());
    }

    @Test
    public void testTestFailsWhenValueNotContained() {
        DecisionSimulationResponse decisionSimulationResponse = createResponse(Arrays.asList(
            createResponseEntry("test", 1),
            createResponseEntry("zweitesAttribut", 3)
        ));
        Map<String, Object> expectedMap = createResponseEntry("test", 2);

        List<Entry<String, Object>> testExpectation = DmnTest.testExpectation(decisionSimulationResponse, expectedMap);

        assertEquals(1, testExpectation.size());
        assertEquals("test", testExpectation.get(0).getKey());
        assertEquals(2, testExpectation.get(0).getValue());
    }

    @Test
    public void testTestWorksWhenExpectedValueIsNullAndNotContained() {
        DecisionSimulationResponse decisionSimulationResponse = createResponse(Arrays.asList(
            createResponseEntry("test", 1)
        ));
        Map<String, Object> expectedMap = createResponseEntry("nullValue", null);

        List<Entry<String, Object>> testExpectation = DmnTest.testExpectation(decisionSimulationResponse, expectedMap);

        assertEquals(0, testExpectation.size());
    }

    @Test
    public void testFailesWhenNotAllExpectedValuesContained() {
        DecisionSimulationResponse decisionSimulationResponse = createResponse(Arrays.asList(
            createResponseEntry("test", 1),
            createResponseEntry("test2", "Hallo"),
            createResponseEntry("test3", "Welt")
        ));
        Map<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("test", 1);
        expectedMap.put("test2", "Hallo");
        expectedMap.put("test3", "Welt");
        expectedMap.put("test4", 1);

        List<Entry<String, Object>> testExpectation = DmnTest.testExpectation(decisionSimulationResponse, expectedMap);

        assertEquals(1, testExpectation.size());
        assertEquals("test4", testExpectation.get(0).getKey());
    }

    @Test
    public void testRunsWhenAllExpectedValuesContained() {
        DecisionSimulationResponse decisionSimulationResponse = createResponse(Arrays.asList(
            createResponseEntry("test", 1),
            createResponseEntry("test2", "Hallo"),
            createResponseEntry("test3", "Welt"),
            createResponseEntry("test4", 1)
        ));
        Map<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("test", 1);
        expectedMap.put("test2", "Hallo");
        expectedMap.put("test3", "Welt");
        expectedMap.put("test4", 1);

        List<Entry<String, Object>> testExpectation = DmnTest.testExpectation(decisionSimulationResponse, expectedMap);

        assertEquals(0, testExpectation.size());
    }

    private DecisionSimulationResponse createResponse(List<Map<String, Object>> results) {
		return DecisionSimulationResponse.DecisionTestCaseResponseBuilder
            .create()
            .withResult(results)
            .build();
    }

    private Map<String, Object> createResponseEntry(String key, Object value) {
        Map<String, Object> entry = new HashMap<>();
        entry.put(key, value);
        return entry;
    }
}