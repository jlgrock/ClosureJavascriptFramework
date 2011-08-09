package namespaceclosure.minifier.google.calcdeps;

import java.io.PrintStream;

import com.google.javascript.jscomp.CheckLevel;
import com.google.javascript.jscomp.CommandLineRunner;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.DiagnosticGroups;
import com.google.javascript.jscomp.PropertyRenamingPolicy;
import com.google.javascript.jscomp.VariableRenamingPolicy;

public class AssertCommandLineRunner extends CommandLineRunner {

	public AssertCommandLineRunner(final String[] args) {
		super(args);
	}

	/**
	 * @param args
	 * @param out
	 * @param err
	 */
	public AssertCommandLineRunner(final String[] args, final PrintStream out,
			final PrintStream err) {
		super(args, out, err);
	}

	@Override
	protected final CompilerOptions createOptions() {
		CompilerOptions options = super.createOptions();
		applyCustomCompilationOptions(options);
		return options;
	}

	private static void applyCustomCompilationOptions(final CompilerOptions options) {
	    // All the safe optimizations.
	    options.closurePass = true;
	    options.foldConstants = true;
	    options.coalesceVariableNames = true;
	    options.deadAssignmentElimination = true;
	    options.extractPrototypeMemberDeclarations = true;
	    options.collapseVariableDeclarations = true;
	    options.convertToDottedProperties = true;
	    options.rewriteFunctionExpressions = true;
	    options.labelRenaming = true;
	    options.removeDeadCode = true;
	    options.optimizeArgumentsArray = true;
	    options.collapseObjectLiterals = true;

	    // All the advance optimizations.
		options.setRemoveClosureAsserts(true);

	    options.inlineConstantVars = true;

	    // Turn off default ADVANCED_OPTIMIZATIONS
	    options.aliasKeywords = false;
	    options.reserveRawExports = false;
	    options.variableRenaming = VariableRenamingPolicy.OFF;
	    options.setShadowVariables(false);
	    options.propertyRenaming = PropertyRenamingPolicy.OFF;
	    options.removeUnusedPrototypeProperties = false;
	    options.removeUnusedPrototypePropertiesInExterns = false;
	    options.collapseAnonymousFunctions = false;
	    options.collapseProperties = false;

	    options.rewriteFunctionExpressions = false;
	    options.smartNameRemoval = false;
	    options.inlineFunctions = false;

	    options.inlineLocalFunctions = false;
	    options.inlineGetters = false;
	    options.inlineVariables = false;
	    options.flowSensitiveInlineVariables = false;
	    options.computeFunctionSideEffects = false;

	    // Remove unused vars also removes unused functions.
	    options.removeUnusedVars = false;
	    options.removeUnusedLocalVars = false;

	    // Move code around based on the defined modules.
	    options.crossModuleCodeMotion = false;
	    options.crossModuleMethodMotion = false;

	    // Call optimizations
	    options.devirtualizePrototypeMethods = false;
	    options.optimizeParameters = false;
	    options.optimizeReturns = false;
	    options.optimizeCalls = false;

		options.checkGlobalThisLevel = CheckLevel.WARNING;

		// Kindly tell the user that they have JsDocs that we don't understand.
		options.setWarningLevel(DiagnosticGroups.NON_STANDARD_JSDOC,
				CheckLevel.WARNING);
	}

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
	    CommandLineRunner runner = new AssertCommandLineRunner(args);
	    if (runner.shouldRunCompiler()) {
	      runner.run();
	    } else {
	      System.exit(-1);
	    }
	}

}
