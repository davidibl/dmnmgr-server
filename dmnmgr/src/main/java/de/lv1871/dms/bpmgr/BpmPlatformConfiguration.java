package de.lv1871.dms.bpmgr;

import org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin;
import org.camunda.feel.CamundaFeelEnginePlugin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BpmPlatformConfiguration {

	@Bean
	public static ProcessEnginePlugin feelScalaPlugin() {
		return new CamundaFeelEnginePlugin();
	}
}