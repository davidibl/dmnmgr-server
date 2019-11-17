package de.lv1871.dms.bpmgr.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import de.lv1871.dms.bpmgr.api.model.OpenApiDefinitionResponse;
import de.lv1871.dms.bpmgr.service.DownloadService;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Controller
@CrossOrigin
public class UtilityResource {

	@Autowired
	private DownloadService downloadService;

	@RequestMapping(value = "/api/open-api-definition", method = RequestMethod.GET)
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 406, message = "Data Not Acceptable"),
			@ApiResponse(code = 403, message = "Not Authorized") })
	public @ResponseBody OpenApiDefinitionResponse deploy(@RequestParam("api-url") String url) {
		return downloadService.downloadOpenApiDefinition(url);
	}
}
