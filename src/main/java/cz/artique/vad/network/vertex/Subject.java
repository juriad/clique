package cz.artique.vad.network.vertex;

import cz.artique.vad.network.message.Message;

public interface Subject {

	public void onMessageReceived(Message message);

	public void onTimeout();
}
