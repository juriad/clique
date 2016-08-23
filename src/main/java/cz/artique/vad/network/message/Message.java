package cz.artique.vad.network.message;

public abstract class Message {
	private final String target;

	public Message(String target) {
		this.target = target;
	}

	public String getTarget() {
		return target;
	}
}
