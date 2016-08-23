package cz.artique.vad.network.vertex;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import cz.artique.vad.network.message.Message;

public class Vertex implements Runnable, VertexInterface, Comparable<Vertex> {

	private final String id;
	private VertexMode mode = VertexMode.STAYING;

	private final List<String> neighbors = new ArrayList<String>();
	private final List<VertexListener> listeners = new ArrayList<VertexListener>();

	private Subject subject;
	private Thread thread;
	private final BlockingQueue<Message> messageQueue;

	private int timestep;

	public Vertex(String id) {
		this.id = id;
		messageQueue = new LinkedBlockingQueue<Message>();
	}

	@Override
	public String getId() {
		return id;
	}

	public synchronized void connect(String v) {
		if (thread != null) {
			throw new IllegalStateException("Subject has already been started.");
		}
		neighbors.add(v);
	}

	public synchronized void addListener(VertexListener vertexListener) {
		listeners.add(vertexListener);
	}

	public synchronized void deliver(Message message) {
		try {
			messageQueue.put(message);
		} catch (InterruptedException e) {
			System.err.println("Failed to deliver message.");
			e.printStackTrace();
		}
	}

	@Override
	public void log(String logEntry) {
		for (VertexListener vl : listeners) {
			vl.log(logEntry);
		}
	}

	@Override
	public void send(Message message) {
		for (VertexListener vl : listeners) {
			vl.send(message);
		}
	}

	public <S extends Subject> S start(Behavior<S> behavior, int timestep) {
		if (thread != null) {
			throw new IllegalStateException("Subject has already been started.");
		}

		S subject = behavior.createSubject(this, neighbors);
		this.subject = subject;

		this.timestep = timestep;
		thread = new Thread(this);
		thread.setDaemon(true);
		thread.start();

		return subject;
	}

	@Override
	public void run() {
		Message message = null;
		long lastWakeUpTime = System.currentTimeMillis();
		while (true) {
			long timeSinceLastWakeUp = System.currentTimeMillis() - lastWakeUpTime;
			long waitTime = Math.max(timestep - timeSinceLastWakeUp, 0);

			if (waitTime <= 0) {
				if (mode == VertexMode.STAYING) {
					subject.onTimeout();
				}
				lastWakeUpTime = System.currentTimeMillis();
				waitTime = timestep;
			}

			try {
				message = messageQueue.poll(waitTime, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				// do nothing
			}

			if (message != null) {
				subject.onMessageReceived(message);
			}
		}
	}

	@Override
	public void leave() {
		for (VertexListener vl : listeners) {
			vl.left();
		}
		mode = VertexMode.LEAVING;
	}

	@Override
	public VertexMode getMode() {
		return mode;
	}

	@Override
	public int compareTo(Vertex other) {
		return id.compareTo(other.getId());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Vertex other = (Vertex) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}
}
