package de.lv1871.dms.dmnmgr.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import de.lv1871.dms.dmnmgr.api.model.DecisionRequest;
import de.lv1871.dms.dmnmgr.api.model.DecisionSimulationRequest;
import de.lv1871.dms.dmnmgr.api.model.DecisionTestRequest;
import de.lv1871.dms.dmnmgr.api.model.DecisionTestResponse;
import de.lv1871.dms.dmnmgr.api.model.DmnValidationResult;
import de.lv1871.dms.dmnmgr.service.AdvancedDmnCheckService;
import de.lv1871.dms.dmnmgr.service.DecisionTestService;
import de.lv1871.dms.dmnmgr.service.GenericDecisionService;
import de.lv1871.dms.tester.test.dmnassert.model.DecisionSimulationResponse;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Controller
@CrossOrigin
public class DecisionResource {

	@Autowired
	private GenericDecisionService decisionService;

	@Autowired
	private DecisionTestService testService;

	@Autowired
	private AdvancedDmnCheckService advancedDmnCheckService;

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

	@RequestMapping(value = "/api/decision/validate", method = RequestMethod.POST)
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 406, message = "Data Not Acceptable"),
			@ApiResponse(code = 403, message = "Not Authorized") })
	public @ResponseBody List<DmnValidationResult> validate(@RequestBody DecisionRequest decisionRequest) {
		return advancedDmnCheckService.validateDecision(decisionRequest);
	}

}
