package cz.artique.vad.network.message;

public class JoinMessage extends Message {

	private final String to;

	public JoinMessage(String target, String to) {
		super(target);

		this.to = to;
	}

	public String getTo() {
		return to;
	}

	@Override
	public String toString() {
		return getTarget() + "->Join(" + getTo() + ")";
	}

}
