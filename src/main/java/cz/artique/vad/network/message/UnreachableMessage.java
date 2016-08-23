package cz.artique.vad.network.message;

public class UnreachableMessage extends Message {

	private final Message original;

	public UnreachableMessage(String target, Message original) {
		super(target);
		this.original = original;
	}

	public Message getOriginal() {
		return original;
	}

	@Override
	public String toString() {
		return getTarget() + "->Unreachable: " + getOriginal();
	}
}
