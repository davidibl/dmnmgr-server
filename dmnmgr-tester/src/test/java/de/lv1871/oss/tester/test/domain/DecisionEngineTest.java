package de.lv1871.oss.tester.test.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.model.xml.impl.util.IoUtil;
import org.junit.Test;

public class DecisionEngineTest {

    @Test
    public void testReadsAllDecisionsWithKeys() throws IOException {
        DecisionEngine engine = DecisionEngine.createEngine()
            .parseDecision(getDmnXml("testDrgTwoDecisions.dmn"));

        assertNotNull(engine.getDecisionByKey("decisionA"));
        assertNotNull(engine.getDecisionByKey("decisionB"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReadsUnknownKeyCausesIllegalArgumentException() throws IOException {
        DecisionEngine engine = DecisionEngine.createEngine()
            .parseDecision(getDmnXml("testDrgTwoDecisions.dmn"));

        engine.getDecisionByKey("decisionC");
    }

    @Test()
    public void testExecutesDecisionAndReturnsResult() throws IOException {
        DecisionEngine engine = DecisionEngine.createEngine()
            .parseDecision(getDmnXml("testDrgTwoDecisions.dmn"));

        DmnDecisionTableResult result = engine.evaluateDecisionByKey("decisionA", getVariables("input", "a"));

        assertEquals("b", result.get(0).get("result"));
    }

    private String getDmnXml(String filename) throws IOException {
        return IoUtil.getStringFromInputStream(this.getClass().getResourceAsStream("/" + filename));
    }

    private Map<String, Object> getVariables(String key, Object value) {
        Map<String, Object> variables = new HashMap<>();
        variables.put(key, value);
        return variables;
    }
    
}