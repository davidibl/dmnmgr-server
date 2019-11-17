package de.lv1871.dms.bpmgr.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import de.lv1871.dms.bpmgr.api.model.DecisionDeployment;
import de.lv1871.dms.bpmgr.api.model.DecisionDeploymentResponse;
import de.lv1871.dms.bpmgr.api.model.DecisionSimulationRequest;
import de.lv1871.dms.bpmgr.api.model.DecisionSimulationResponse;
import de.lv1871.dms.bpmgr.api.model.DecisionTestRequest;
import de.lv1871.dms.bpmgr.api.model.DecisionTestResponse;
import de.lv1871.dms.bpmgr.api.model.ServiceResult;
import de.lv1871.dms.bpmgr.api.model.ServiceResult.ServiceResultBuilder;
import de.lv1871.dms.bpmgr.service.DecisionTestService;
import de.lv1871.dms.bpmgr.service.GenericDecisionService;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Controller
@CrossOrigin
public class DecisionResource {

	@Autowired
	private GenericDecisionService decisionService;

	@Autowired
	private DecisionTestService testService;

	@RequestMapping(value = "/api/decision", method = RequestMethod.POST)
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 406, message = "Data Not Acceptable"),
			@ApiResponse(code = 403, message = "Not Authorized") })
	public @ResponseBody DecisionDeploymentResponse deploy(@RequestBody DecisionDeployment decisionDeploymentRequest) {
		return decisionService.deploy(decisionDeploymentRequest.getXml());
	}

	@RequestMapping(value = "/api/decision", method = RequestMethod.DELETE)
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 406, message = "Data Not Acceptable"),
			@ApiResponse(code = 403, message = "Not Authorized") })
	public @ResponseBody ServiceResult clear() {
		decisionService.clear();
		return ServiceResultBuilder.create().withResult(true).build();
	}

	@RequestMapping(value = "/api/decision/simulation", method = RequestMethod.POST)
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 406, message = "Data Not Acceptable"),
			@ApiResponse(code = 403, message = "Not Authorized") })
	public @ResponseBody DecisionSimulationResponse evaluateTestCase(
			@RequestBody DecisionSimulationRequest decisionRequest) {
		return decisionService.decide(decisionRequest);
	}

	@RequestMapping(value = "/api/decision/test", method = RequestMethod.POST)
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 406, message = "Data Not Acceptable"),
			@ApiResponse(code = 403, message = "Not Authorized") })
	public @ResponseBody DecisionTestResponse test(@RequestBody DecisionTestRequest decisionRequest) {
		return testService.testDecision(decisionRequest);
	}

}
