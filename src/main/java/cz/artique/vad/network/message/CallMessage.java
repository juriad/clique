package cz.artique.vad.network.message;

public class CallMessage extends Message {
	private final String method;
	private final String[] args;

	public CallMessage(String target, String method, String... args) {
		super(target);

		this.method = method;
		this.args = args;
	}

	public String getMethod() {
		return method;
	}

	public String[] getArgs() {
		return args;
	}

	@Override
	public String toString() {
		return getTarget() + "->" + getMethod() + "(" + String.join(", ", getArgs()) + ")";
	}
}
