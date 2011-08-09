package namespaceclosure.minifier;

public class MinificationException extends Exception {
	private static final long serialVersionUID = -1949031695912114181L;

	public MinificationException() {
		super();
	}
	
	public MinificationException(Exception e) {
		super(e);
	}
	
	public MinificationException(String s) {
		super(s);
	}
	
	public MinificationException (String s, Exception e) {
		super(s,e);
	}
}
