package de.lv1871.dms.dmnmgr.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import de.lv1871.dms.dmnmgr.api.model.DmnValidationResponse;
import de.lv1871.dms.dmnmgr.api.model.DmnValidationResult;

public class AdvancedDmnCheckServiceTest {

    private AdvancedDmnCheckService cut;

    @Before
    public void init() {
        cut = new AdvancedDmnCheckService();
        cut.init();
    }

    @Test
    public void testCheckHiddenRule() {
        DmnValidationResponse results = cut.validateDecision(this.getClass().getResourceAsStream("/meineTestDecision.dmn"));

        List<String> decisionTable = results.getErrors().stream().map(DmnValidationResult::getTableId).distinct().collect(Collectors.toList());
        List<String> decisionRules = results.getErrors().stream().map(DmnValidationResult::getRuleId).distinct().collect(Collectors.toList());

        assertNotNull(results);
        
        assertEquals(1, decisionTable.size());
        assertEquals("decision", decisionTable.get(0));

        assertEquals(2, decisionRules.size());
        assertTrue(allContained(decisionRules, Arrays.asList("DecisionRule_0fuuqy7", "DecisionRule_1op4sed")));
    }

    @Test
    public void testInputExpressionRequired() {
        DmnValidationResponse results = cut.validateDecision(this.getClass().getResourceAsStream("/requiredInputExpressionTest.dmn"));

        List<String> decisionTable = results.getErrors().stream().map(DmnValidationResult::getTableId).distinct().collect(Collectors.toList());

        assertNotNull(results);
        assertEquals(0, results.getWarnings().size());
        assertEquals(1, results.getErrors().size());
        assertEquals("decision", decisionTable.get(0));
        assertEquals("InputExpression has no expressiontext", results.getErrors().get(0).getMessage());
        
    }

    @Test
    public void testOutputNameRequired() {
        DmnValidationResponse results = cut.validateDecision(this.getClass().getResourceAsStream("/requiredOutputNameTest.dmn"));

        List<String> decisionTable = results.getErrors().stream().map(DmnValidationResult::getTableId).distinct().collect(Collectors.toList());

        assertNotNull(results);
        assertEquals(0, results.getWarnings().size());
        assertEquals(1, results.getErrors().size());
        assertEquals("decision", decisionTable.get(0));
        assertEquals("Output has no name", results.getErrors().get(0).getMessage());
        
    }

    @Test
    public void testJuelGetsBasicValidation() {
        DmnValidationResponse results = cut.validateDecision(this.getClass().getResourceAsStream("/juelfeel.dmn"));

        List<String> decisionTable = results.getErrors().stream().map(DmnValidationResult::getTableId).distinct().collect(Collectors.toList());

        assertNotNull(results);
        assertEquals(0, results.getWarnings().size());
        assertEquals(3, results.getErrors().size());
        assertEquals("decision", decisionTable.get(0));        
    }

    private boolean allContained(List<String> decisionRules, List<String> expectedToBeContained) {
        return expectedToBeContained
            .stream()
            .filter(value -> !decisionRules.contains(value))
            .count() < 1;
    }
}