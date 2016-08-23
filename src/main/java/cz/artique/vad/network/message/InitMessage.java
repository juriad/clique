package cz.artique.vad.network.message;

public class InitMessage extends Message {
	public InitMessage(String target) {
		super(target);
	}

	@Override
	public String toString() {
		return getTarget() + "->Init()";
	}
}
