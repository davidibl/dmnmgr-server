
package de.lv1871.dms.dmnmgr.domain;

import de.redsix.dmncheck.result.Severity;
import de.redsix.dmncheck.result.ValidationResult;
import de.redsix.dmncheck.validators.core.SimpleValidator;
import de.redsix.dmncheck.validators.core.ValidationContext;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.camunda.bpm.model.dmn.instance.InputExpression;
import org.camunda.bpm.model.dmn.instance.Text;
import org.springframework.util.StringUtils;

public class InputExpressionRequiredValidator extends SimpleValidator<InputExpression> {

    @Override
    protected boolean isApplicable(InputExpression arg0, ValidationContext arg1) {
        return true;
    }

    @Override
    protected List<ValidationResult> validate(InputExpression expression, ValidationContext arg1) {
        try {
            final String expressionText = Optional.ofNullable(expression).map(InputExpression::getText)
                    .map(Text::getTextContent).orElse(null);

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