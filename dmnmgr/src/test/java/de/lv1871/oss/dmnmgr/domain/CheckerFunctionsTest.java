package de.lv1871.oss.dmnmgr.domain;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CheckerFunctionsTest {
    
    @Test
    public void testFindsUnescapedQuotationMark() {
        String value = "Hallo\" Welt";
        Boolean result = CheckerFunctions.containesUnquotedtQuotationMark(value);
        assertTrue(result);
    }

    @Test
    public void testFindsUnescapedQuotationMarkWithOneCorrect() {
        String value = "Hal\\\"lo\" Welt";
        Boolean result = CheckerFunctions.containesUnquotedtQuotationMark(value);
        assertTrue(result);
    }

    @Test
    public void testFindsEscapedQuotationMark() {
        String value = "Hallo\\\" Welt";
        Boolean result = CheckerFunctions.containesUnquotedtQuotationMark(value);
        assertFalse(result);
    }
}