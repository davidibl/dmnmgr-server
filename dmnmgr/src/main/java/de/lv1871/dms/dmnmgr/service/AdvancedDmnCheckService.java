package de.lv1871.dms.dmnmgr.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
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

	public List<DmnValidationResult> validateDecision(DecisionRequest decisionRequest) {
        ByteArrayInputStream stream = new ByteArrayInputStream(decisionRequest.getXml().getBytes(StandardCharsets.UTF_8));
		return validateDecision(stream);
	}

    public List<DmnValidationResult> validateDecision(final InputStream file) {
        try {
            final DmnModelInstance dmnModelInstance = Dmn.readModelFromStream(file);
            final List<ValidationResult> validationResults = runValidators(dmnModelInstance);

            return validationResults
                .stream()
                .filter(result -> !result.getMessage().contains("parse"))
                .map(this::mapModel)
                .collect(Collectors.toList());
        }
        catch (Exception e) {
            return Arrays.asList(
                DmnValidationResultBuilder
                    .create()
                    .withMessage(e.getMessage())
                    .withSeverity(ErrorSeverity.ERROR)
                    .build()
            );
        }
    }

    private DmnValidationResult mapModel(ValidationResult result) {
        return DmnValidationResultBuilder
            .create()
            .withTableId(Optional
                .ofNullable(result.getElement())
                .map(ModelElementInstance::getParentElement)
                .map(ModelElementInstance::getParentElement)
                .map(element -> element.getAttributeValue("id"))
                .orElse(null))
            .withRuleId(result.getElement().getAttributeValue("id"))
            .withMessage(result.getMessage())
            .withSeverity(ErrorSeverity.ofSeverity(result.getSeverity()))
            .build();
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