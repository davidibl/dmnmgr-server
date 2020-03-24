package de.lv1871.oss.dmnmgr.api.model;

import de.redsix.dmncheck.result.Severity;

public enum ErrorSeverity {
    ERROR, WARNING;

	public static ErrorSeverity ofSeverity(Severity severity) {
		switch(severity) {
            case ERROR:
                return ErrorSeverity.ERROR;
            case WARNING:
                return ErrorSeverity.WARNING;
            default:
                return ErrorSeverity.ERROR;

        }
	}

}
