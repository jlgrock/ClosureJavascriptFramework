package com.github.jlgrock.javascriptframework.jsdocs;

/**
 * Generates and aggregates javascript docs, from the jsdoc-toolkit (the final
 * version).
 * 
 * @goal test-aggregate
 * 
 */
public class TestAggregateJsDocsMojo extends TestJsDocsMojo {

	@Override
	protected final boolean isAggregator() {
        return true;
    }
}
