package namespaceclosure.minifier.google.calcdeps;

public class NamespaceException extends Exception {

	private static final long serialVersionUID = 4934436570591322552L;
	
	NamespaceException() {
		super();
	}
	NamespaceException(String message) {
		super(message);
	}
	NamespaceException(String message, Throwable exception) {
		super(message, exception);
	}
}
