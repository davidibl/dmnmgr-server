package de.lv1871.dms.dmnmgr.test.driver;

import java.math.BigDecimal;
import java.util.Map.Entry;

public class DmnVariableAssert {

	public static boolean equal(Entry<String, Object> resultEntry, Entry<String, Object> expectedEntry) {
		if (!resultEntry.getKey().equals(expectedEntry.getKey())) {
			return false;
		}
		return equalValue(resultEntry.getValue(), expectedEntry.getValue());
	}

	public static boolean equalValue(Object result, Object expected) {
		if (result == null && expected == null) {
			return true;
		}
		if (result == null || expected == null) {
			return false;
		}
		if (result instanceof Number && expected instanceof Number) {
			return numbersEqual((Number) result, (Number) expected);
		}
		return result.equals(expected);
	}

	public static boolean numbersEqual(Number n1, Number n2) {
		BigDecimal b1 = new BigDecimal(n1.doubleValue());
		BigDecimal b2 = new BigDecimal(n2.doubleValue());
		return b1.compareTo(b2) == 0;
	}
}
