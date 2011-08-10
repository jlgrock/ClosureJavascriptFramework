package jsPreparserCompiler.minifier.google;

import jsPreparserCompiler.minifier.MinificationException;
import jsPreparserCompiler.minifier.MinificationType;


public class GoogleEnumConverter {
	private GoogleEnumConverter() {}
	
	public static GoogleMinificationType convert(MinificationType type) throws MinificationException {
		GoogleMinificationType returnType;
		switch(type) {
		case GOOGLE_WHITESPACE:
			returnType = GoogleMinificationType.GOOGLE_WHITESPACE;
			break;
		case GOOGLE_SIMPLE:
			returnType = GoogleMinificationType.GOOGLE_SIMPLE;
			break;
		case GOOGLE_ADVANCED:
			returnType = GoogleMinificationType.GOOGLE_ADVANCED;
			break;
	    default:
	    	throw new MinificationException("The minification type is not valid.");
		}
		return returnType;
	}
}
