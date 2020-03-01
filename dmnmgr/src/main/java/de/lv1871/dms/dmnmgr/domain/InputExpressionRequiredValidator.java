
package de.lv1871.dms.dmnmgr.domain;

import de.redsix.dmncheck.result.Severity;
import de.redsix.dmncheck.result.ValidationResult;
import de.redsix.dmncheck.validators.core.SimpleValidator;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.camunda.bpm.model.dmn.instance.InputExpression;
import org.camunda.bpm.model.dmn.instance.Text;
import org.springframework.util.StringUtils;

public class InputExpressionRequiredValidator extends SimpleValidator<InputExpression> {

    @Override
    public boolean isApplicable(InputExpression expression) {
        return true;
    }

    @Override
    public List<ValidationResult> validate(InputExpression expression) {
        try {
            final String expressionText = Optional.ofNullable(expression)
                .map(InputExpression::getText)
                .map(Text::getTextContent)
                .orElse(null);
    
            if (StringUtils.isEmpty(expressionText)) {
                return Collections.singletonList(ValidationResult.init
                        .message(getClassUnderValidation().getSimpleName() + " has no expressiontext")
                        .severity(Severity.ERROR).element(expression).build());
            }
            return Collections.emptyList();
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public Class<InputExpression> getClassUnderValidation() {
        return InputExpression.class;
    }

}