package jsPreparserCompiler.minifier.google;

import jsPreparserCompiler.minifier.MinificationException;
import jsPreparserCompiler.minifier.MinifyFramework;
import jsPreparserCompiler.minifier.google.calcdeps.CalcDeps;
import jsPreparserCompiler.minifier.google.calcdeps.CalcDepsOptions;

public class GoogleMinifier implements MinifyFramework {
	final CalcDepsOptions calcDepsOptions;
	
	public GoogleMinifier (CalcDepsOptions calcDepsOptions) throws MinificationException {
		this.calcDepsOptions = calcDepsOptions;
	}

	public void minify() throws MinificationException {
		try {
			//also should add the directories into the calcDepsOptions class
			CalcDeps calcDeps = new CalcDeps(calcDepsOptions);
			calcDeps.executeCalcDeps();
		} catch(Exception e) {
			throw new MinificationException(e);
		}
	}
}
