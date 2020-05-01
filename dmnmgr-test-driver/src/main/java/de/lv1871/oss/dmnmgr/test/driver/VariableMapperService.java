package de.lv1871.oss.dmnmgr.test.driver;

import java.io.IOException;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class VariableMapperService {

	private static ObjectMapper MAPPER;

	public VariableMapperService() {
		this.initMapper();
	}

	@SuppressWarnings("unchecked")
	public HashMap<String, Object> getVariablesFromJsonAsMap(ObjectNode json) {
		var valueAsString = json.toString();
		try {
			return MAPPER.readValue(valueAsString, HashMap.class);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public String writeJson(Object value) {
		try {
			return MAPPER.writeValueAsString(value);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	private void initMapper() {
		MAPPER = new ObjectMapper();
		MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
	}
}
