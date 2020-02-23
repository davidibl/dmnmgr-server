package de.lv1871.dms.tester.test.function;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class LambdaExtensionTest {

	@Test
	public void testNotNullWithSupplierWhenNull() {
		TestObject testObject = new TestObject(null);

		assertFalse(LambdaExtension.notNull(TestObject::getValue).test(testObject));
	}

	@Test
	public void testNotNullWithSupplierWhenNotNull() {
		TestObject testObject = new TestObject(null);

		assertFalse(LambdaExtension.notNull(TestObject::getValue).test(testObject));
	}

	private static class TestObject {

		public TestObject(String value) {
			this.value = value;
		}

		private String value;

		public String getValue() {
			return value;
		}

	}
}
