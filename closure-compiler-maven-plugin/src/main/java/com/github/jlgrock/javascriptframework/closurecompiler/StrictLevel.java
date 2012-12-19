package com.github.jlgrock.javascriptframework.closurecompiler;

import com.google.javascript.jscomp.CheckLevel;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.DiagnosticGroups;

/**
 * Convert the warnings level to an Options object.
 * 
 */
public enum StrictLevel {
	/**
	 * The predefined option that is verbose.
	 */
	VERBOSE;

	/**
	 * Set one of the predefined Warning levels.
	 * 
	 * @param options
	 *            the predefined option
	 */
	public void setOptionsForWarningLevel(final CompilerOptions options) {
		switch (this) {
		case VERBOSE:
			addVerboseWarnings(options);
			break;
		default:
			throw new RuntimeException("Unknown warning level.");
		}
	}

	/**
	 * Add all the check pass that are possibly relevant to a non googler.
	 * 
	 * @param options
	 *            The CompilerOptions object to set the options on.
	 */
	private static void addVerboseWarnings(final CompilerOptions options) {
		options.checkTypes = true;
		options.checkSuspiciousCode = true;
		options.checkUnreachableCode = CheckLevel.ERROR;
		options.checkControlStructures = true;
		options.checkSuspiciousCode = true;
		options.checkSymbols = true;

		options.setAggressiveVarCheck(CheckLevel.ERROR);
		options.setBrokenClosureRequiresLevel(CheckLevel.ERROR);
		options.setCheckGlobalNamesLevel(CheckLevel.ERROR);
		options.setCheckGlobalThisLevel(CheckLevel.ERROR);
		// options.setCheckMissingGetCssNameLevel(CheckLevel.ERROR);
		options.setCheckMissingReturn(CheckLevel.ERROR);
		options.setCheckProvides(CheckLevel.ERROR);
		options.setCheckRequires(CheckLevel.ERROR);
		options.setCheckUnreachableCode(CheckLevel.ERROR);

		/**
		 * All other Warning Level Settings
		 */
		// Warnings when deprecated, private, or protected are violated.
		options.setWarningLevel(DiagnosticGroups.ACCESS_CONTROLS,
				CheckLevel.ERROR);
		options.setWarningLevel(DiagnosticGroups.AMBIGUOUS_FUNCTION_DECL,
				CheckLevel.ERROR);
		options.setWarningLevel(DiagnosticGroups.CAST,
				CheckLevel.ERROR);
		options.setWarningLevel(DiagnosticGroups.CHECK_PROVIDES,
				CheckLevel.ERROR);
		options.setWarningLevel(DiagnosticGroups.CHECK_REGEXP, CheckLevel.ERROR);
		options.setWarningLevel(DiagnosticGroups.CHECK_TYPES, CheckLevel.ERROR);
		options.setWarningLevel(DiagnosticGroups.CHECK_USELESS_CODE,
				CheckLevel.ERROR);
		options.setWarningLevel(DiagnosticGroups.CHECK_VARIABLES,
				CheckLevel.ERROR);
		options.setWarningLevel(DiagnosticGroups.CONST, CheckLevel.ERROR);
		options.setWarningLevel(DiagnosticGroups.CONSTANT_PROPERTY,
				CheckLevel.ERROR);
		options.setWarningLevel(DiagnosticGroups.DEBUGGER_STATEMENT_PRESENT,
				CheckLevel.ERROR);
		// Warnings when non-deprecated code accesses code that's marked
		// deprecated
		options.setWarningLevel(DiagnosticGroups.DEPRECATED, CheckLevel.ERROR);
		options.setWarningLevel(DiagnosticGroups.DUPLICATE_MESSAGE,
				CheckLevel.ERROR);
		options.setWarningLevel(DiagnosticGroups.DUPLICATE_VARS,
				CheckLevel.ERROR);
		options.setWarningLevel(DiagnosticGroups.ES5_STRICT, CheckLevel.ERROR);
		options.setWarningLevel(DiagnosticGroups.EXTERNS_VALIDATION,
				CheckLevel.ERROR);
		options.setWarningLevel(DiagnosticGroups.FILEOVERVIEW_JSDOC,
				CheckLevel.ERROR);
		options.setWarningLevel(DiagnosticGroups.GLOBAL_THIS, CheckLevel.ERROR);
		options.setWarningLevel(DiagnosticGroups.INTERNET_EXPLORER_CHECKS,
				CheckLevel.ERROR);
		options.setWarningLevel(DiagnosticGroups.INVALID_CASTS,
				CheckLevel.ERROR);
		options.setWarningLevel(DiagnosticGroups.MISSING_PROPERTIES,
				CheckLevel.ERROR);
		options.setWarningLevel(DiagnosticGroups.MISPLACED_TYPE_ANNOTATION,
				CheckLevel.ERROR);
		options.setWarningLevel(DiagnosticGroups.NON_STANDARD_JSDOC,
				CheckLevel.ERROR);
		options.setWarningLevel(DiagnosticGroups.STRICT_MODULE_DEP_CHECK,
				CheckLevel.ERROR);
		options.setWarningLevel(DiagnosticGroups.SUSPICIOUS_CODE,
				CheckLevel.ERROR);
		options.setWarningLevel(DiagnosticGroups.TWEAKS, CheckLevel.ERROR);
		options.setWarningLevel(DiagnosticGroups.TYPE_INVALIDATION,
				CheckLevel.ERROR);
		options.setWarningLevel(DiagnosticGroups.UNDEFINED_NAMES,
				CheckLevel.ERROR);
		options.setWarningLevel(DiagnosticGroups.UNDEFINED_VARIABLES,
				CheckLevel.ERROR);
		options.setWarningLevel(DiagnosticGroups.UNKNOWN_DEFINES,
				CheckLevel.ERROR);
		options.setWarningLevel(DiagnosticGroups.VIOLATED_MODULE_DEP,
				CheckLevel.ERROR);
		// Warnings when private and protected are violated (seems to work the
		// same as access controls).
		options.setWarningLevel(DiagnosticGroups.VISIBILITY, CheckLevel.ERROR);
	}
}
