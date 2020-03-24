package de.lv1871.oss.dmnmgr.test.driver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.junit.Test;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class VariableMapperServiceTest {

	private VariableMapperService cut = new VariableMapperService();

	@Test
	public void testDeserializesObjectNodeToMapCorrect() {
		ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
		node.put("testDatum", "2019-12-20T12:12:00Z");
		node.put("Hallo", "Welt");
		node.put("zahl", 1);
		node.put("bool", true);

		HashMap<String, Object> jsonAsMap = cut.getVariablesFromJsonAsMap(node);

		assertEquals("Welt", jsonAsMap.get("Hallo"));
		assertEquals("2019-12-20T12:12:00Z", jsonAsMap.get("testDatum"));
		assertEquals(1, jsonAsMap.get("zahl"));
		assertTrue((boolean) jsonAsMap.get("bool"));
	}

	@Test
	public void testSerializesDatesAsJavascriptDates() {
		TestObject testObject = new TestObject(createDateFromString("2012-12-20:12"), BigDecimal.valueOf(12.12));

		String jsonString = cut.writeJson(testObject);

		assertTrue("Big Decimal fehlerhaft " + jsonString, jsonString.contains("\"zahl\":12.12"));
		assertTrue("Datum fehlerhaft " + jsonString, jsonString.contains("\"datum\":\"2012-12-20T11:00:00.000+0000\""));
	}

	private Date createDateFromString(String dateString) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd:HH");
		try {
			return format.parse(dateString);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public static class TestObject {

		private Date datum;
		private BigDecimal zahl;

		public TestObject(Date datum, BigDecimal zahl) {
			this.setDatum(datum);
			this.setZahl(zahl);
		}

		public Date getDatum() {
			return datum;
		}

		public void setDatum(Date datum) {
			this.datum = datum;
		}

		public BigDecimal getZahl() {
			return zahl;
		}

		public void setZahl(BigDecimal zahl) {
			this.zahl = zahl;
		}
	}
}
