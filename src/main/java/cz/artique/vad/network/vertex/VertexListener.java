package cz.artique.vad.network.vertex;

import cz.artique.vad.network.message.Message;

public interface VertexListener {
	public void send(Message message);

	public void log(String logEntry);

	public void left();
}
