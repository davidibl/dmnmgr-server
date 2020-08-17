package de.lv1871.oss.dmnmgr.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.camunda.bpm.model.dmn.Dmn;
import org.camunda.bpm.model.dmn.DmnModelInstance;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.springframework.stereotype.Service;

import de.lv1871.oss.dmnmgr.api.model.DecisionRequest;
import de.lv1871.oss.dmnmgr.api.model.DmnValidationResponse;
import de.lv1871.oss.dmnmgr.api.model.DmnValidationResult;
import de.lv1871.oss.dmnmgr.api.model.ErrorSeverity;
import de.lv1871.oss.dmnmgr.api.model.DmnValidationResult.DmnValidationResultBuilder;
import de.redsix.dmncheck.result.ValidationResult;
import de.redsix.dmncheck.validators.core.Validator;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;

@Service
public class AdvancedDmnCheckService {
    
    private static final String MESSAGE_NOT_CONNECTED_TABLE = "Element is not connected to requirement graph";
    private static final String ATTRIBUTE_NAME_RULE_ID = "id";
    private static final String SEQUEL_CORRESPONDING_RULE = "by rule ";
    private static final String VALIDATOR_PACKAGE = "de.redsix.dmncheck.validators";
    private static final String VALIDATOR_CORE_PACKAGE = "de.redsix.dmncheck.validators.core";
    
    private @MonotonicNonNull List<Validator> validators;

    private String[] validatorPackages = new String[0];

    private String[] validatorClasses = {
        "de.redsix.dmncheck.validators.ConflictingRuleValidator",
        "de.redsix.dmncheck.validators.ShadowedRuleValidator",
        "de.redsix.dmncheck.validators.AggregationOutputTypeValidator",
        "de.redsix.dmncheck.validators.ConnectedRequirementGraphValidator",
        "de.redsix.dmncheck.validators.DuplicateRuleValidator",
        "de.redsix.dmncheck.validators.InputTypeDeclarationValidator",
        "de.redsix.dmncheck.validators.OutputTypeDeclarationValidator",
        "de.lv1871.oss.dmnmgr.domain.InputExpressionRequiredValidator",
        "de.lv1871.oss.dmnmgr.domain.OutputNameRequiredValidator",
        "de.lv1871.oss.dmnmgr.domain.InputEntryValidator",
        "de.lv1871.oss.dmnmgr.domain.OutputEntryValidator"
    };

    @PostConstruct
    public void init() {
        initValidators();
    }

	public DmnValidationResponse validateDecision(DecisionRequest decisionRequest) {
        var stream = new ByteArrayInputStream(decisionRequest.getXml().getBytes(StandardCharsets.UTF_8));
		return validateDecision(stream);
	}

    public DmnValidationResponse validateDecision(final InputStream file) {
        var response = new DmnValidationResponse();
        response.setErrors(new ArrayList<>());
        response.setWarnings(new ArrayList<>());
        try {
            final var dmnModelInstance = Dmn.readModelFromStream(file);
            final var validationResults = runValidators(dmnModelInstance);

            var results = validationResults
                .stream()
                .filter(result -> !result.getMessage().contains("parse"))
                .map(this::mapModel)
                .collect(Collectors.toList());

            response.setErrors(results
                .stream()
                .filter(result -> result.getSeverity() == ErrorSeverity.ERROR)
                .collect(Collectors.toList()));

            response.setWarnings(results
                .stream()
                .filter(result -> result.getSeverity() == ErrorSeverity.WARNING)
                .collect(Collectors.toList()));

            return response;
        }
        catch (Exception e) {
            var errors = Arrays.asList(
                DmnValidationResultBuilder
                    .create()
                    .withMessage(e.getMessage())
                    .withSeverity(ErrorSeverity.ERROR)
                    .build()
            );
            response.setErrors(errors);
            return response;
        }
    }

    private DmnValidationResult mapModel(ValidationResult result) {
        return DmnValidationResultBuilder
            .create()
            .withTableId(getTableId(result.getElement()))
            .withRuleId(getRuleId(result.getElement()))
            .withCellId(getCellId(result.getElement()))
            .withCounterRuleId(tryFindCounter(result.getMessage()))
            .withMessage(result.getMessage())
            .withSeverity(correctSeverity(result))
            .build();
    }

    public ErrorSeverity correctSeverity(ValidationResult result) {
        if (MESSAGE_NOT_CONNECTED_TABLE.equals(result.getMessage())) {
            return ErrorSeverity.WARNING;
        }
        return ErrorSeverity.ofSeverity(result.getSeverity());
    }

    private String tryFindCounter(String message) {
        if (message.contains(SEQUEL_CORRESPONDING_RULE)) {
            return Optional
                .ofNullable(message.split(SEQUEL_CORRESPONDING_RULE))
                .filter(arr -> arr.length > 1)
                .map(arr -> arr[arr.length - 1])
                .map(value -> value.trim())
                .orElse(null);
        }
        return null;
    }

    private String getTableId(ModelElementInstance instance) {
        if (isDecisionRule(instance)) {
            return Optional.ofNullable(instance)
                .map(ModelElementInstance::getParentElement)
                .map(ModelElementInstance::getParentElement)
                .map(element -> element.getAttributeValue(ATTRIBUTE_NAME_RULE_ID))
                .orElse(null);
        } else if(isLiteralExpression(instance)) {
            return Optional.ofNullable(instance)
                .map(ModelElementInstance::getParentElement)
                .map(ModelElementInstance::getParentElement)
                .map(ModelElementInstance::getParentElement)
                .map(element -> element.getAttributeValue(ATTRIBUTE_NAME_RULE_ID))
                .orElse(null);
        } else if(isOutputClause(instance)) {
            return Optional.ofNullable(instance)
                .map(ModelElementInstance::getParentElement)
                .map(ModelElementInstance::getParentElement)
                .map(element -> element.getAttributeValue(ATTRIBUTE_NAME_RULE_ID))
                .orElse(null);
        } else if(isInputEntry(instance) || isOutputEntry(instance)) {
            return Optional.ofNullable(instance)
                .map(ModelElementInstance::getParentElement)
                .map(ModelElementInstance::getParentElement)
                .map(ModelElementInstance::getParentElement)
                .map(element -> element.getAttributeValue(ATTRIBUTE_NAME_RULE_ID))
                .orElse(null);
        }

        return Optional.ofNullable(instance)
            .map(element -> element.getAttributeValue(ATTRIBUTE_NAME_RULE_ID))
            .orElse(null);
    }

    private boolean isInputEntry(ModelElementInstance instance) {
        return "inputEntry".equals(instance.getElementType().getTypeName());
    }

    private boolean isOutputEntry(ModelElementInstance instance) {
        return "outputEntry".equals(instance.getElementType().getTypeName());
    }

    private String getRuleId(ModelElementInstance instance) {
        if (isDecisionRule(instance)) {
            return instance.getAttributeValue(ATTRIBUTE_NAME_RULE_ID);
        } else if (isInputEntry(instance) || isOutputEntry(instance)) {
            return instance.getParentElement().getAttributeValue(ATTRIBUTE_NAME_RULE_ID);
        }

        return null;
    }

    private String getCellId(ModelElementInstance instance) {
        if (isInputEntry(instance) || isOutputEntry(instance)) {
            return instance.getAttributeValue(ATTRIBUTE_NAME_RULE_ID);
        }
        return null;
    }

    private boolean isDecisionRule(ModelElementInstance instance) {
        return "decisionRule".equals(instance.getElementType().getBaseType().getTypeName());
    }

    private boolean isLiteralExpression(ModelElementInstance instance) {
        return "literalExpression".equals(instance.getElementType().getBaseType().getTypeName());
    }

    private boolean isOutputClause(ModelElementInstance instance) {
        return "outputClause".equals(instance.getElementType().getBaseType().getTypeName());
    }

    private List<ValidationResult> runValidators(final DmnModelInstance dmnModelInstance) {
        return getValidators().stream()
                .flatMap(validator -> validator.apply(dmnModelInstance).stream())
                .collect(Collectors.toList());
    }

    private List<Validator> getValidators() {
        return validators;
    }

    private void initValidators() {

        if (validatorPackages == null) {
            validatorPackages = new String[] {VALIDATOR_PACKAGE, VALIDATOR_PACKAGE + ".core"};
        }

        if (validatorClasses == null) {
            validatorClasses = new String[] { };
        }

        final ScanResult scanResult = new ClassGraph()
                .whitelistClasses(Validator.class.getName())
                .whitelistPackages(VALIDATOR_CORE_PACKAGE)
                .whitelistPackagesNonRecursive(validatorPackages)
                .whitelistClasses(validatorClasses)
                .scan();

        final ClassInfoList allValidatorClasses = scanResult.getClassesImplementing(Validator.class.getName());

        validators = allValidatorClasses.loadClasses(Validator.class).stream()
                .filter(validatorClass -> !Modifier.isAbstract(validatorClass.getModifiers()))
                .filter(validatorClass -> !Modifier.isInterface(validatorClass.getModifiers()))
                .map(this::instantiateValidator)
                .collect(Collectors.toList());
    }

    private Validator instantiateValidator(final Class<? extends Validator> validator) {
        try {
            return validator.getDeclaredConstructor().newInstance();
        }
        catch (IllegalAccessException | InstantiationException |
                NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException("Failed to load validator " + validator, e);
        }
    }

}