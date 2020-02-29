package de.lv1871.dms.dmnmgr.service;

import java.io.IOException;
import java.util.HashMap;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Service
public class VariableMapperService {

	private static ObjectMapper mapper;

	@SuppressWarnings("unchecked")
	public HashMap<String, Object> getVariablesFromJsonAsMap(ObjectNode json) {
		String valueAsString = json.toString();
		try {
			return mapper.readValue(valueAsString, HashMap.class);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@PostConstruct
	private static void initMapper() {
		mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
	}
}
