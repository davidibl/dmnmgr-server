package de.lv1871.dms.dmnmgr.service;

import java.io.InputStreamReader;
import java.net.URL;

import org.springframework.stereotype.Service;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;

import de.lv1871.dms.dmnmgr.api.model.OpenApiDefinitionResponse;

@Service
public class DownloadService {

	public OpenApiDefinitionResponse downloadOpenApiDefinition(String urlString) {
		try {
			URL url = new URL(urlString);

			String readUrlContents;
			try (InputStreamReader reader = new InputStreamReader(url.openStream(), Charsets.UTF_8)) {
				readUrlContents = CharStreams.toString(reader);
			}

			OpenApiDefinitionResponse response = new OpenApiDefinitionResponse();
			response.setApiUrl(urlString);
			response.setSwaggerDefinition(readUrlContents);

			return response;
		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

}
