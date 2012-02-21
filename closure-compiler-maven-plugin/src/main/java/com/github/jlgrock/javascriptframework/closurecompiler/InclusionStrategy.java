package com.github.jlgrock.javascriptframework.closurecompiler;

import java.util.HashMap;

/**
 * The possible inclusion strategies for this plugin.
 */
public enum InclusionStrategy {
	/**
	 * The possible inclusion strategies.
	 */
    ALL("all"), WHEN_IN_SRCS("when_in_srcs");
    
    /**
     * The type string.
     */
    private String type;
    
    /**
     * The hashmap used for lookups.
     */
    public static final HashMap<String, InclusionStrategy> TYPES;
    
    static {
    	TYPES = new HashMap<String, InclusionStrategy>(); 
    	for (InclusionStrategy t : values()) {
    		TYPES.put(t.type, t);
    	}
    }
    
    /**
     * The private constructor.
     * @param typeIn the type used for lookups
     */
    InclusionStrategy(final String typeIn) {
    	this.type = typeIn;
    }
    
    /**
     * Lookup the inclusion strategy by the type.
     * @param key the type to look for
     * @return the appropriate inclusion strategy
     */
    public static InclusionStrategy getByType(final String key) {
    	return TYPES.get(key.toLowerCase());
    }
  }
