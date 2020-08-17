package de.lv1871.oss.dmnmgr.domain;

import java.util.Arrays;

import org.camunda.commons.utils.StringUtil;

public class CheckerFunctions {
    
    public static boolean containesUnquotedtQuotationMark(String trimmedText) {
        if (trimmedText == null || trimmedText.length() < 1) {
            return false;
        }
        String textWithoutMarks = trimmedText;
        if (trimmedText.startsWith("\"")) {
            textWithoutMarks = trimmedText.substring(1);
        }
        if (textWithoutMarks.endsWith("\"")) {
            textWithoutMarks = textWithoutMarks.substring(0, textWithoutMarks.length() - 1);
        }

        return Arrays.asList(textWithoutMarks.split("\\\\"))
            .stream()
            .map(value -> value.substring(1))
            .filter(value -> value.indexOf("\"") > -1)
            .count() > 0;
    }
}