package com.github.jlgrock.javascriptframework.jsdocs;


/**
 * Generates and aggregates javascript docs, from the jsdoc-toolkit (the final
 * version) and stores them into a js archive.
 * 
 * @goal aggregate-jsar
 * 
 */
public class AggregateJsDocsJsarMojo extends JsDocsJsarMojo {

	@Override
	protected final boolean isAggregator() {
		return true;
	}
}
