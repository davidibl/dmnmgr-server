package de.lv1871.dms.bpmgr.api.documentation;

import static springfox.documentation.builders.PathSelectors.regex;

import com.google.common.base.Predicate;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

public class DecisionApi {

	private static final String API_TITLE = "DMN API";
	private static final String API_NAME = "dmn-api";
	private static final String API_DESCRIPTION = "Dieses API dient dazu Entscheidungen zu triggern";

	private static ApiInfo apiInfo() {
		return new ApiInfoBuilder().title(API_TITLE).description(API_DESCRIPTION).build();
	}

	public static Docket createApiInfo() {
		// @formatter:off
		return new Docket(DocumentationType.SWAGGER_2)
				.groupName(API_NAME)
				.useDefaultResponseMessages(false)
				.apiInfo(apiInfo())
				.select()
				.paths(dokumentDecisionPath())
				.build();
		// @formatter:on
	}

	private static Predicate<String> dokumentDecisionPath() {
		return regex("/api/.*");
	}
}
