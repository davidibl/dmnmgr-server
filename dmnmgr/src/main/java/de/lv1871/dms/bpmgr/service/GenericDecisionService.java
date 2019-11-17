package de.lv1871.dms.bpmgr.service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.history.HistoricDecisionInstance;
import org.camunda.bpm.engine.history.HistoricDecisionOutputInstance;
import org.camunda.bpm.engine.repository.DecisionDefinition;
import org.camunda.bpm.engine.repository.DecisionRequirementsDefinition;
import org.camunda.bpm.engine.repository.Deployment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.node.ObjectNode;

import de.lv1871.dms.bpmgr.api.model.DecisionDeploymentResponse;
import de.lv1871.dms.bpmgr.api.model.DecisionSimulationRequest;
import de.lv1871.dms.tester.test.dmnassert.model.DecisionSimulationResponse;
import de.lv1871.dms.tester.test.dmnassert.model.DecisionSimulationResponse.DecisionTestCaseResponseBuilder;

@Service
public class GenericDecisionService {

	@Autowired
	private ProcessEngine processEngine;

	@Autowired
	private VariableMapperService mapperService;

	public DecisionSimulationResponse decide(DecisionSimulationRequest decisionRequest) {
		DecisionDeploymentResponse deploymentResponse = deploy(decisionRequest.getXml());
		return decide(deploymentResponse.getDecisionRequirementsId(), decisionRequest.getDmnTableId(),
				decisionRequest.getVariables());
	}

	public DecisionSimulationResponse decide(String decisionRequirementsId, String decisionKey,
			ObjectNode variablesNode) {
		try {

			// @formatter:off
			Optional<DecisionDefinition> decisionDefinition = processEngine
				.getRepositoryService()
				.createDecisionDefinitionQuery()
				.decisionRequirementsDefinitionId(decisionRequirementsId)
				.decisionDefinitionKey(decisionKey)
				.list()
				.stream()
				.findFirst();
			
			if (!decisionDefinition.isPresent()) {
				decisionDefinition = processEngine
					.getRepositoryService()
					.createDecisionDefinitionQuery()
					.decisionDefinitionId(decisionRequirementsId)
					.list()
					.stream()
					.findFirst();
			}
			
			DecisionDefinition decisionDefinitionResolved =
					decisionDefinition.orElseThrow(() -> new ResourceNotFoundException());
			
			HashMap<String, Object> variables = mapperService.getVariablesFromJsonAsMap(variablesNode);

			DmnDecisionTableResult decisionResult = processEngine.getDecisionService()
					.evaluateDecisionTableById(decisionDefinitionResolved.getId(), variables);
			
			HistoricDecisionInstance historicDecisionInstance = processEngine
					.getHistoryService()
					.createHistoricDecisionInstanceQuery()
					.decisionDefinitionId(decisionDefinitionResolved.getId())
					.includeOutputs()
					.orderByEvaluationTime()
					.desc()
					.list()
					.stream()
					.findFirst()
					.orElse(null);
			
			List<String> resultRules = historicDecisionInstance
					.getOutputs()
					.stream()
					.map(HistoricDecisionOutputInstance::getRuleId)
					.collect(Collectors.toList());
			// @formatter:on

			if (decisionResult.getResultList().stream().filter(result -> result.get(null) != null).count() > 0) {
				return DecisionTestCaseResponseBuilder.create()
						.withMessage("Ein oder meherere Output-Felder haben keinen Namen.").build();
			}

			return DecisionTestCaseResponseBuilder.create().withResultRuleIds(resultRules)
					.withResult(decisionResult.getResultList()).build();
		} catch (Exception exception) {
			exception.printStackTrace();
			if (exception.getCause() != null) {
				return DecisionTestCaseResponseBuilder.create().withMessage(exception.getCause().getMessage()).build();
			}
			return DecisionTestCaseResponseBuilder.create().withMessage(exception.getMessage()).build();
		}
	}

	public DecisionDeploymentResponse deploy(String dmnXml) {
		UUID uuid = UUID.randomUUID();
		String deploymentName = String.format("%s.dmn", uuid.toString());

		// @formatter:off
		processEngine
			.getRepositoryService()
			.createDeployment()
			.addString(deploymentName, dmnXml)
			.deploy();

		Optional<DecisionDeploymentResponse> deploymentResponse = processEngine
				.getRepositoryService()
				.createDecisionRequirementsDefinitionQuery()
				.orderByDecisionRequirementsDefinitionVersion()
				.desc()
				.list()
				.stream()
				.filter(definition -> definition.getResourceName().equals(deploymentName))
				.map(this::toDecisionDeploymentResource)
				.findFirst();
		
		if (deploymentResponse.isPresent()) {
			return deploymentResponse.get();
		}
		
		return processEngine
				.getRepositoryService()
				.createDecisionDefinitionQuery()
				.orderByDecisionDefinitionVersion()
				.desc()
				.list()
				.stream()
				.filter(definition -> definition.getResourceName().equals(deploymentName))
				.map(this::toDecisionDeploymentResource)
				.findFirst()
				.orElseThrow(() -> new ResourceNotFoundException());
		// @formatter:on
	}

	private DecisionDeploymentResponse toDecisionDeploymentResource(DecisionRequirementsDefinition definition) {
		DecisionDeploymentResponse response = new DecisionDeploymentResponse();
		response.setDecisionRequirementsId(definition.getId());
		return response;
	}

	private DecisionDeploymentResponse toDecisionDeploymentResource(DecisionDefinition definition) {
		DecisionDeploymentResponse response = new DecisionDeploymentResponse();
		response.setDecisionRequirementsId(definition.getId());
		return response;
	}

	public void clear() {
		// @formatter:off
		List<Deployment> deployments = this.processEngine
			.getRepositoryService()
			.createDeploymentQuery()
			.list();
		
		for (Deployment deployment : deployments) {
			this.processEngine
				.getRepositoryService()
				.deleteDeployment(deployment.getId());
		}
		// @formatter:on
	}

}
