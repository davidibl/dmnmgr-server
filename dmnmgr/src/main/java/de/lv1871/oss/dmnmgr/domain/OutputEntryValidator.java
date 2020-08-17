package de.lv1871.oss.dmnmgr.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.camunda.bpm.engine.impl.el.ExpressionManager;
import org.camunda.bpm.model.dmn.instance.OutputEntry;

import de.redsix.dmncheck.result.Severity;
import de.redsix.dmncheck.result.ValidationResult;
import de.redsix.dmncheck.validators.core.SimpleValidator;
import de.redsix.dmncheck.validators.core.ValidationContext;

import de.lv1871.oss.tester.test.function.ExtendedBiFunction;
import static de.lv1871.oss.dmnmgr.domain.CheckerFunctions.containesUnquotedtQuotationMark;

public class OutputEntryValidator extends SimpleValidator<OutputEntry> {

    @Override
    protected boolean isApplicable(OutputEntry arg0, ValidationContext arg1) {
        return true;
    }

    @Override
    protected List<ValidationResult> validate(OutputEntry element, ValidationContext arg1) {
        var expressionLanguage = Optional.ofNullable(element).map(OutputEntry::getExpressionLanguage).orElse("feel")
                .toLowerCase();
        switch (expressionLanguage) {
            case "feel":
                return checkExpression(element, this::checkFeelExpression);
            case "juel":
                return checkExpression(element, this::checkJuelExpression);
            default:
                return null;
        }
    }

    private List<ValidationResult> checkExpression(OutputEntry element,
            ExtendedBiFunction<OutputEntry, String, List<ValidationResult>> validator) {
        return Optional.ofNullable(element)
            .map(OutputEntry::getTextContent)
            .map(validator.curryWith(element))
            .orElse(Collections.emptyList());
    }

    private List<ValidationResult> checkJuelExpression(OutputEntry entry, String text) {
        if (text == null) {
            return Collections.emptyList();
        }

        if (text.startsWith("${") && !text.endsWith("}")) {
            return Arrays.asList(ValidationResult.init
                    .message(String.format("Error in Juel Expression: (%s) Must start with '${'", text))
                    .severity(Severity.ERROR).element(entry).build());
        }

        if (!text.startsWith("${") && text.endsWith("}")) {
            return Arrays.asList(ValidationResult.init
                    .message(String.format("Error in Juel Expression: (%s) Must end with '}'", text))
                    .severity(Severity.ERROR).element(entry).build());
        }

        List<ValidationResult> result = new ArrayList<>();
        try {
            if (!text.startsWith("$") && text.length() > 0) {
                text = "${" + text + "}";
            }
            new ExpressionManager().createExpression(text);
        } catch (Exception exception) {
            result.add(ValidationResult.init.message("Error in Juel Expression: " + exception.getMessage())
                    .severity(Severity.ERROR).element(entry).build());
        }
        return result;
    }

    private List<ValidationResult> checkFeelExpression(OutputEntry entry, String text) {
        if (text == null) {
            return Collections.emptyList();
        }

        List<ValidationResult> result = new ArrayList<>();
        String trimmedText = text.trim();
        try {
            if (trimmedText.startsWith("\"") && !trimmedText.endsWith("\"")) {
                return Arrays.asList(ValidationResult.init
                            .message(String.format("Error in Feel Expression: (%s) Must end with '\"'", trimmedText))
                            .severity(Severity.ERROR).element(entry).build());
            }
            if (!trimmedText.startsWith("\"") && trimmedText.endsWith("\"")) {
                return Arrays.asList(ValidationResult.init
                            .message(String.format("Error in Feel Expression: (%s) Must start with '\"'", trimmedText))
                            .severity(Severity.ERROR).element(entry).build());
            }
            
            if (containesUnquotedtQuotationMark(trimmedText)) {
                result.add(ValidationResult.init
                    .message(String.format("Error in Feel Expression: (%s) Must not contain a '\"'", trimmedText))
                    .severity(Severity.ERROR).element(entry).build());
            }
        } catch (Exception exception) {
            result.add(ValidationResult.init.message("Error in Feel Expression: " + exception.getMessage())
                    .severity(Severity.ERROR).element(entry).build());
        }
        return result;
    }

    @Override
    public Class<OutputEntry> getClassUnderValidation() {
        return OutputEntry.class;
    }

    
}