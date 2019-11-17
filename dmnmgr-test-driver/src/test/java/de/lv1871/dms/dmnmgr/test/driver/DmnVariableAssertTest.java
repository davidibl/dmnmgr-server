package de.lv1871.dms.dmnmgr.test.driver;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Test;

public class DmnVariableAssertTest {

	@Test
	public void testIntegerCompareCorrectNotEqual() {
		assertFalse(DmnVariableAssert.numbersEqual(1, 2));
	}

	@Test
	public void testIntegerCompareCorrectEqual() {
		assertTrue(DmnVariableAssert.numbersEqual(1, 1));
	}

	@Test
	public void testDoubleCompareCorrectNotEqual() {
		assertFalse(DmnVariableAssert.numbersEqual(1.3, 2.5));
	}

	@Test
	public void testDoubleCompareCorrectEqual() {
		assertTrue(DmnVariableAssert.numbersEqual(1.5, 1.5));
	}

	@Test
	public void testDoubleCompareCorrectToIntegerValueEqual() {
		assertTrue(DmnVariableAssert.numbersEqual(1.0, 1));
	}

	@Test
	public void testDoubleCompareCorrectToIntegerValueNotEqual() {
		assertFalse(DmnVariableAssert.numbersEqual(1.0, 2));
	}

	@Test
	public void testNullValueComparisonBothNull() {
		assertTrue(DmnVariableAssert.equalValue(null, null));
	}

	@Test
	public void testNullValueComparisonLeftNull() {
		assertFalse(DmnVariableAssert.equalValue(null, new Object()));
	}

	@Test
	public void testNullValueComparisonRightNull() {
		assertFalse(DmnVariableAssert.equalValue(new Object(), null));
	}

	@Test
	public void testNumberComparisonPassToNumberComparison() {
		assertTrue(DmnVariableAssert.equalValue(new BigDecimal(12.0), 12L));
	}

	@Test
	public void testStringsEqual() {
		assertTrue(DmnVariableAssert.equalValue(new String("Hallo".getBytes()), new String("Hallo".getBytes())));
	}

	@Test
	public void testObjectComparisonWithEqual() {
		TestObject a = new TestObject(1, 3);
		TestObject b = new TestObject(1, 7);
		assertTrue(DmnVariableAssert.equalValue(a, b));
	}

	private class TestObject {

		private int a;
		private int b;

		public TestObject(int a, int b) {
			this.a = a;
			this.b = b;
		}

		public int getA() {
			return a;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			TestObject other = (TestObject) obj;
			return this.getA() == other.getA();
		}

	}

}
