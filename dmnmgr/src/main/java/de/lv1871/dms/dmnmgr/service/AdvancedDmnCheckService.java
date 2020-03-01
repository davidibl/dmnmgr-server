package de.lv1871.dms.dmnmgr.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.camunda.bpm.model.dmn.Dmn;
import org.camunda.bpm.model.dmn.DmnModelInstance;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.springframework.stereotype.Service;

import de.lv1871.dms.dmnmgr.api.model.DecisionRequest;
import de.lv1871.dms.dmnmgr.api.model.DmnValidationResponse;
import de.lv1871.dms.dmnmgr.api.model.DmnValidationResult;
import de.lv1871.dms.dmnmgr.api.model.ErrorSeverity;
import de.lv1871.dms.dmnmgr.api.model.DmnValidationResult.DmnValidationResultBuilder;
import de.redsix.dmncheck.result.ValidationResult;
import de.redsix.dmncheck.validators.core.Validator;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;

@Service
public class AdvancedDmnCheckService {
    
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
        "de.redsix.dmncheck.validators.OutputTypeDeclarationValidator"
    };

	public DmnValidationResponse validateDecision(DecisionRequest decisionRequest) {
        ByteArrayInputStream stream = new ByteArrayInputStream(decisionRequest.getXml().getBytes(StandardCharsets.UTF_8));
		return validateDecision(stream);
	}

    public DmnValidationResponse validateDecision(final InputStream file) {
        DmnValidationResponse response = new DmnValidationResponse();
        response.setErrors(new ArrayList<>());
        response.setWarnings(new ArrayList<>());
        try {
            final DmnModelInstance dmnModelInstance = Dmn.readModelFromStream(file);
            final List<ValidationResult> validationResults = runValidators(dmnModelInstance);

            List<DmnValidationResult> results = validationResults
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
            List<DmnValidationResult> errors = Arrays.asList(
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
            .withMessage(result.getMessage())
            .withSeverity(ErrorSeverity.ofSeverity(result.getSeverity()))
            .build();
    }

    private String getTableId(ModelElementInstance instance) {
        if (isDecisionRule(instance)) {
            return Optional.ofNullable(instance)
                .map(ModelElementInstance::getParentElement)
                .map(ModelElementInstance::getParentElement)
                .map(element -> element.getAttributeValue("id"))
                .orElse(null);
        }

        return Optional.ofNullable(instance)
            .map(element -> element.getAttributeValue("id"))
            .orElse(null);
    }

    private String getRuleId(ModelElementInstance instance) {
        return Optional.ofNullable(instance)
            .filter(this::isDecisionRule)
            .map(element -> element.getAttributeValue("id"))
            .orElse(null);
    }

    private boolean isDecisionRule(ModelElementInstance instance) {
        return "decisionRule".equals(instance.getElementType().getBaseType().getTypeName());
    }

    private List<ValidationResult> runValidators(final DmnModelInstance dmnModelInstance) {
        return getValidators().stream()
                .flatMap(validator -> validator.apply(dmnModelInstance).stream())
                .collect(Collectors.toList());
    }

    private List<Validator> getValidators() {
        if (validators != null) {
            return validators;
        }

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

        return validators;
    }

    private Validator instantiateValidator(final Class<? extends Validator> validator) {
        try {
            return validator.newInstance();
        }
        catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException("Failed to load validator " + validator, e);
        }
    }

}