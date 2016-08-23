package cz.artique.vad.network.message;

public class LogMessage extends Message {
	private final String logEntry;

	public LogMessage(String logEntry) {
		super(null);

		this.logEntry = logEntry;
	}

	public String getLogEntry() {
		return logEntry;
	}

	@Override
	public String toString() {
		return getLogEntry();
	}
}
