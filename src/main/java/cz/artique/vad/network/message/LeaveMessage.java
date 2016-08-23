package cz.artique.vad.network.message;

public class LeaveMessage extends Message {

	public LeaveMessage(String target) {
		super(target);
	}

	@Override
	public String toString() {
		return getTarget() + "->Leave()";
	}

}
