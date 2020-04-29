package de.lv1871.oss.dmnmgr.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import org.camunda.bpm.engine.impl.el.ExpressionManager;
import org.camunda.bpm.model.dmn.instance.InputEntry;

import de.redsix.dmncheck.result.Severity;
import de.redsix.dmncheck.result.ValidationResult;
import de.redsix.dmncheck.validators.core.SimpleValidator;
import de.redsix.dmncheck.validators.core.ValidationContext;

public class InputEntryValidator extends SimpleValidator<InputEntry> {

    @Override
    protected boolean isApplicable(InputEntry arg0, ValidationContext arg1) {
        return true;
    }

    @Override
    protected List<ValidationResult> validate(InputEntry element, ValidationContext arg1) {
        String expressionLanguage = Optional.ofNullable(element).map(InputEntry::getExpressionLanguage).orElse("feel")
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

    private List<ValidationResult> checkExpression(InputEntry element,
            ExtendedBiFunction<InputEntry, String, List<ValidationResult>> validator) {
        return Optional.ofNullable(element).map(InputEntry::getTextContent).map(validator.curryWith(element))
                .orElse(Collections.emptyList());
    }

    private List<ValidationResult> checkJuelExpression(InputEntry entry, String text) {
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

    private List<ValidationResult> checkFeelExpression(InputEntry entry, String text) {
        if (text == null) {
            return Collections.emptyList();
        }

        List<ValidationResult> result = new ArrayList<>();
        Scanner scanner = new Scanner(text);
        try {
            scanner.useDelimiter(",");
            while(scanner.hasNext()) {
                String value = scanner.next();
                if (value == null) {
                    continue;
                }
                String valueTrimmed = value.trim();
                if (valueTrimmed.startsWith("\"") && !valueTrimmed.endsWith("\"")) {
                    return Arrays.asList(ValidationResult.init
                            .message(String.format("Error in Feel Expression: (%s) Must end with '\"'", text))
                            .severity(Severity.ERROR).element(entry).build());
                }
                if (!valueTrimmed.startsWith("\"") && valueTrimmed.endsWith("\"")) {
                    return Arrays.asList(ValidationResult.init
                            .message(String.format("Error in Feel Expression: (%s) Must start with '\"'", text))
                            .severity(Severity.ERROR).element(entry).build());
                }
            }
        } catch (Exception exception) {
            result.add(ValidationResult.init.message("Error in Juel Expression: " + exception.getMessage())
                    .severity(Severity.ERROR).element(entry).build());
        } finally {
            scanner.close();
        }
        return result;
    }

    @Override
    public Class<InputEntry> getClassUnderValidation() {
        return InputEntry.class;
    }

    
}