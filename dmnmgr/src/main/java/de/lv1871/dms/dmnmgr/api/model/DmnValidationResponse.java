package de.lv1871.dms.dmnmgr.api.model;

import java.util.List;

public class DmnValidationResponse {

    private List<DmnValidationResult> errors;
    private List<DmnValidationResult> warnings;

    public List<DmnValidationResult> getWarnings() {
        return warnings;
    }

    public List<DmnValidationResult> getErrors() {
        return errors;
    }

    public void setErrors(List<DmnValidationResult> errors) {
        this.errors = errors;
    }

    public void setWarnings(List<DmnValidationResult> warnings) {
        this.warnings = warnings;
    }
    
}