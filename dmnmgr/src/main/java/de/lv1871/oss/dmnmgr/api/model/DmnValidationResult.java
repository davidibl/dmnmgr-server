package de.lv1871.oss.dmnmgr.api.model;

public class DmnValidationResult {

    private final String tableId;
    private final String ruleId;
    private final String counterRuleId;
    private final String message;
    private final ErrorSeverity severity;

    private DmnValidationResult(String tableId, String ruleId, String counterRuleId, String message,
            ErrorSeverity severety) {
        this.tableId = tableId;
        this.ruleId = ruleId;
        this.message = message;
        this.severity = severety;
        this.counterRuleId = counterRuleId;
    }

    public String getCounterRuleId() {
        return counterRuleId;
    }

    public String getTableId() {
        return tableId;
    }

    public String getRuleId() {
        return ruleId;
    }

    public String getMessage() {
        return message;
    }

    public ErrorSeverity getSeverity() {
        return severity;
    }
    
    public static class DmnValidationResultBuilder {

        private String tableId;
        private String ruleId;
        private String counterRuleId;
        private String message;
        private ErrorSeverity severity;

        public static DmnValidationResultBuilder create() {
            return new DmnValidationResultBuilder();
        }

        public DmnValidationResultBuilder withTableId(String tableId) {
            this.tableId = tableId;
            return this;
        }

        public DmnValidationResultBuilder withRuleId(String ruleId) {
            this.ruleId = ruleId;
            return this;
        }

        public DmnValidationResultBuilder withCounterRuleId(String counterRuleId) {
            this.counterRuleId = counterRuleId;
            return this;
        }

        public DmnValidationResultBuilder withMessage(String message) {
            this.message = message;
            return this;
        }

        public DmnValidationResultBuilder withSeverity(ErrorSeverity severity) {
            this.severity = severity;
            return this;
        }

        public DmnValidationResult build() {
            return new DmnValidationResult(tableId, ruleId, counterRuleId, message, severity);
        }
    }
    
}