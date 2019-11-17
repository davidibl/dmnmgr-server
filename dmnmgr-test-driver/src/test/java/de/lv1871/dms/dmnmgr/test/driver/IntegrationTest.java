package de.lv1871.dms.dmnmgr.test.driver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.file.Paths;

import org.junit.Test;

import junit.framework.TestResult;

public class IntegrationTest {

	// @formatter:off
	private static final String ASSERTION_ERROR_TEST_A = 
			"--------------------------------------------\n" + 
			"Expected Data:\n" + 
			"[{\"res\":\"We\"}]\n" + 
			"\n" + 
			"--------------------------------------------\n" + 
			"Result:\n" + 
			"[{\"res\":\"Welt\"}]";
	// @formatter:on

	@Test
	public void testAssertFailesWhenDataNotEqual() {
		junit.framework.Test testsuite = DmnTestCreator
				.getSuiteParameterized(Paths.get("src", "test", "resources", "a_test.dmnapp.json"));
		TestResult result = new TestResult();
		testsuite.run(result);

		assertEquals(1, result.errorCount());
		assertTrue(result.errors().nextElement().exceptionMessage().contains(ASSERTION_ERROR_TEST_A));
	}
}
