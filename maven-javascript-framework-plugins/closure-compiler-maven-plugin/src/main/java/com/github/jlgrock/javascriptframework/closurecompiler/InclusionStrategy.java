package com.github.jlgrock.javascriptframework.closurecompiler;

import java.util.HashMap;

public enum InclusionStrategy {
    ALL("all"),
    WHEN_IN_SRCS("when_in_srcs");
    
    private String type;
    public static final HashMap<String, InclusionStrategy> types;
    
    static {
    	types = new HashMap<String, InclusionStrategy>(); 
    	for (InclusionStrategy t : values()) {
    		types.put(t.type, t);
    	}
    }
    
    InclusionStrategy(final String typeIn) {
    	this.type = typeIn;
    }
    
    public static InclusionStrategy getByType(final String key) {
    	return types.get(key.toLowerCase());
    }
  }
