package cz.artique.vad.network;

import java.io.Closeable;
import java.io.PrintStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import cz.artique.vad.network.message.Message;

public class Logger implements Closeable, Runnable {
	private static final int TIMESTEP = 100;

	private final PrintStream ps;
	private boolean closed;

	private final long start;
	private final BlockingQueue<String> messageQueue;

	private boolean logMessages = true;

	public Logger(PrintStream ps) {
		this.ps = ps;
		start = System.nanoTime();

		messageQueue = new LinkedBlockingQueue<String>();

		Thread thread = new Thread(this);
		thread.setPriority(8);
		thread.start();
	}

	public void log(String message) {
		double ts = (System.nanoTime() - start) / 1e9;
		String entry = String.format("%.5f %s", ts, message);
		try {
			messageQueue.put(entry);
		} catch (InterruptedException e) {
			System.err.println("Failed to log: " + entry);
		}
	}

	public void log(Message message, String from) {
		if (isLogMessages()) {
			log((from == null ? "SYSTEM" : "From " + from) + ": " + message.toString());
		}
	}

	@Override
	public synchronized void close() {
		closed = true;
	}

	@Override
	public void run() {
		while (!closed || !messageQueue.isEmpty()) {
			String entry = null;
			try {
				entry = messageQueue.poll(TIMESTEP, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				// do nothing
			}

			if (entry != null && ps != null) {
				ps.println(entry);
			}
		}

		if (closed) {
			ps.close();
		}
	}

	public boolean isLogMessages() {
		return logMessages;
	}

	public void setLogMessages(boolean logMessages) {
		this.logMessages = logMessages;
	}
}
