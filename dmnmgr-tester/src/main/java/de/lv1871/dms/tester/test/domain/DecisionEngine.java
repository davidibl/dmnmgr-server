package de.lv1871.dms.tester.test.domain;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnDecisionRequirementsGraph;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.DmnEngine;
import org.camunda.bpm.dmn.engine.DmnEngineConfiguration;
import org.camunda.bpm.dmn.engine.delegate.DmnDecisionTableEvaluationEvent;
import org.camunda.bpm.dmn.engine.delegate.DmnDecisionTableEvaluationListener;
import org.camunda.bpm.dmn.engine.delegate.DmnEvaluatedDecisionRule;
import org.camunda.bpm.dmn.engine.impl.DefaultDmnEngineConfiguration;
import org.camunda.feel.integration.CamundaFeelEngineFactory;

public class DecisionEngine {

    private DmnEngine dmnEngine;
    private Map<String, DmnDecision> decisions;
    private Map<String, DmnDecisionRequirementsGraph> decisionRequirementsGraphs;
    private MyTableEvaluationListener tableEvaluationListener = new MyTableEvaluationListener();

    public static DecisionEngine createEngine() {
        DecisionEngine engine = new DecisionEngine();
        engine.init();
        return engine;
    }

    public void parseDecision(String dmnXml) {
        DmnDecisionRequirementsGraph drg = dmnEngine
                .parseDecisionRequirementsGraph(new ByteArrayInputStream(dmnXml.getBytes(StandardCharsets.UTF_8)));
        decisionRequirementsGraphs.put(drg.getKey(), drg);
        drg.getDecisionKeys().stream().forEach(key -> {
            this.decisions.put(key, drg.getDecision(key));
        });
    }

    public DmnDecisionTableResult evaluateDecisionByKey(String key, Map<String, Object> variables) {
        return dmnEngine.evaluateDecisionTable(decisions.get(key), variables);
    }

    public List<String> getDecisionKeyByDrgKey(String key) {
        return decisionRequirementsGraphs.get(key).getDecisionKeys().stream().collect(Collectors.toList());
    }

    public List<String> getResulRules(String dmnTableId) {
		return tableEvaluationListener.getLatestMatchedRules(dmnTableId);
	}

    private void init() {

        decisions = new HashMap<>();
        decisionRequirementsGraphs = new HashMap<>();

        DefaultDmnEngineConfiguration dmnEngineConfig = (DefaultDmnEngineConfiguration) DmnEngineConfiguration
                .createDefaultDmnEngineConfiguration();
        dmnEngineConfig.setDefaultInputEntryExpressionLanguage("feel-scala-unary-tests");
        dmnEngineConfig.setDefaultOutputEntryExpressionLanguage("feel-scala");
        dmnEngineConfig.setDefaultLiteralExpressionLanguage("feel-scala");
        dmnEngineConfig.setDefaultInputExpressionExpressionLanguage("feel-scala");

        List<DmnDecisionTableEvaluationListener> decisionTableEvaluationListeners = new ArrayList<>();
        decisionTableEvaluationListeners.add(tableEvaluationListener);
        dmnEngineConfig.customPostDecisionTableEvaluationListeners(decisionTableEvaluationListeners);

        dmnEngine = dmnEngineConfig.feelEngineFactory(new CamundaFeelEngineFactory()).buildEngine();
    }

    private static class MyTableEvaluationListener implements DmnDecisionTableEvaluationListener {

        private Map<String, List<String>> latestMatchedRules = new HashMap<>();
        
        @Override
        public void notify(DmnDecisionTableEvaluationEvent evaluationEvent) {
            latestMatchedRules.put(
                evaluationEvent.getDecisionTable().getKey(),
                evaluationEvent.getMatchingRules()
                    .stream()
                    .map(DmnEvaluatedDecisionRule::getId)
                    .collect(Collectors.toList())
            );
        }

        public List<String> getLatestMatchedRules(String tableId) {
            if (!latestMatchedRules.containsKey(tableId)) {
                return new ArrayList<>();
            }
            return latestMatchedRules.get(tableId);
        }

    }
    
}