package de.lv1871.oss.tester.test.dmnassert;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import de.lv1871.oss.tester.test.dmnassert.model.DecisionSimulationResponse;

public class DmnAssertTest {

    private static final int VALUE_3 = 42;
    private static final double VALUE_2 = 2.23;
    private static final int VALUE_1 = 1;
    private static final String FIELDNAME = "result";

    @Test
    public void assertsFoundEntryAsEqual() {
        DecisionSimulationResponse decisionSimulationResponse = createDecisionResult(Arrays.asList(
            createResult(FIELDNAME, VALUE_1),
            createResult(FIELDNAME, VALUE_2)));

        boolean assertionResult = DmnAssert.assertEqual(decisionSimulationResponse, getEntry(FIELDNAME, VALUE_1));
        
        assertTrue(assertionResult);
    }

    @Test
    public void assertsNullNotEqualSomeExpectedNumber() {
        DecisionSimulationResponse decisionSimulationResponse = createDecisionResult(Arrays.asList(
            createResult(FIELDNAME, null)));

        boolean assertionResult = DmnAssert.assertEqual(decisionSimulationResponse, getEntry(FIELDNAME, VALUE_1));
        
        assertFalse(assertionResult);
    }

    @Test
    public void assertsNullNotEqualSomeFoundNumber() {
        DecisionSimulationResponse decisionSimulationResponse = createDecisionResult(Arrays.asList(
            createResult(FIELDNAME, VALUE_1)));

        boolean assertionResult = DmnAssert.assertEqual(decisionSimulationResponse, getEntry(FIELDNAME, null));
        
        assertFalse(assertionResult);
    }

    @Test
    public void assertsNullValueEqualNoFoundNumber() {
        DecisionSimulationResponse decisionSimulationResponse = createDecisionResult(Arrays.asList());

        boolean assertionResult = DmnAssert.assertEqual(decisionSimulationResponse, getEntry(FIELDNAME, null));
        
        assertTrue(assertionResult);
    }

    @Test
    public void assertsNumberValueNotEqualNoFoundNumber() {
        DecisionSimulationResponse decisionSimulationResponse = createDecisionResult(Arrays.asList());

        boolean assertionResult = DmnAssert.assertEqual(decisionSimulationResponse, getEntry(FIELDNAME, VALUE_1));
        
        assertFalse(assertionResult);
    }

    @Test
    public void assertsEntryNotFoundAsNotEqual() {
        DecisionSimulationResponse decisionSimulationResponse = createDecisionResult(Arrays.asList(
            createResult(FIELDNAME, VALUE_1),
            createResult(FIELDNAME, VALUE_2)));
        
        boolean assertionResult = DmnAssert.assertEqual(decisionSimulationResponse, getEntry(FIELDNAME, VALUE_3));
        
        assertFalse(assertionResult);
    }

    @Test
    public void assertsEntryFoundTwiceAsEqual() {
		DecisionSimulationResponse decisionSimulationResponse = createDecisionResult(Arrays.asList(
            createResult(FIELDNAME, VALUE_1),
            createResult(FIELDNAME, VALUE_2),
            createResult(FIELDNAME, VALUE_2)));
        
        boolean assertionResult = DmnAssert.assertEqual(decisionSimulationResponse, getEntry(FIELDNAME, VALUE_2));
        
        assertTrue(assertionResult);
    }

    private DecisionSimulationResponse createDecisionResult(List<Map<String, Object>> result) {
        return DecisionSimulationResponse.DecisionTestCaseResponseBuilder
            .create()
            .withResult(result)
            .build();
    }

    private Map<String, Object> createResult(String key, Object value) {
        Map<String, Object> row = new HashMap<>();
        row.put(key, value);
        return row;
    }

    private Entry<String, Object> getEntry(String key, Object value) {
        return new AbstractMap.SimpleEntry<String, Object>(key, value);
    }
    
}