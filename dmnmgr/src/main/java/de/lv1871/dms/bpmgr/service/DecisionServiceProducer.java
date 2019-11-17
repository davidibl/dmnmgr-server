package de.lv1871.dms.bpmgr.service;

import org.camunda.bpm.engine.DecisionService;
import org.camunda.bpm.engine.ProcessEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DecisionServiceProducer {

	@Autowired
	private ProcessEngine processEngine;

	@Bean
	public DecisionService createDecisionService() {
		return processEngine.getDecisionService();
	}
}
