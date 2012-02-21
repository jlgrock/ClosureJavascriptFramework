package com.github.jlgrock.javascriptframework.jsdocs;


/**
 * Generates and aggregates javascript docs, from the jsdoc-toolkit (the final
 * version).
 * 
 * @goal aggregate
 * 
 */
public class AggregateJsDocsMojo extends JsDocsMojo {
	
	@Override
	protected final boolean isAggregator() {
        return true;
    }
	
}
