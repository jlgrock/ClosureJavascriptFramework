package com.github.jlgrock.javascriptframework.jsdocs;

/**
 * Generates and aggregates javascript docs, from the jsdoc-toolkit (the final
 * version) and stores them into a js archive.
 * 
 * @goal test-aggregate-jsar
 * 
 */
public class TestAggregateJsDocsJsar extends TestJsDocsJsarMojo {
	
	@Override
    protected boolean isAggregator() {
        return true;
    }
}
