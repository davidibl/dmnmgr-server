package de.lv1871.dms.dmnmgr.service;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.stream.Collectors;

import org.camunda.bpm.model.dmn.Dmn;
import org.camunda.bpm.model.dmn.DmnModelInstance;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.springframework.stereotype.Service;

import de.redsix.dmncheck.result.Severity;
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

    private String[] validatorPackages;

    private String[] validatorClasses;

    public boolean testFile(final File file) {
        boolean encounteredError = false;

        try {
            final DmnModelInstance dmnModelInstance = Dmn.readModelFromFile(file);
            final List<ValidationResult> validationResults = runValidators(dmnModelInstance);

            if (!validationResults.isEmpty()) {
                // PrettyPrintValidationResults.logPrettified(file, validationResults, getLog());
                encounteredError = validationResults.stream()
                        .anyMatch(result -> Severity.ERROR.equals(result.getSeverity()));
            }
        }
        catch (Exception e) {
            encounteredError = true;
        }

        return encounteredError;
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