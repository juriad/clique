package cz.artique.vad.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import cz.artique.vad.network.message.InitMessage;
import cz.artique.vad.network.message.JoinMessage;
import cz.artique.vad.network.message.LeaveMessage;
import cz.artique.vad.network.message.LogMessage;
import cz.artique.vad.network.message.Message;
import cz.artique.vad.network.message.SearchMessage;
import cz.artique.vad.network.message.UnreachableMessage;
import cz.artique.vad.network.vertex.Behavior;
import cz.artique.vad.network.vertex.Subject;
import cz.artique.vad.network.vertex.Vertex;
import cz.artique.vad.network.vertex.VertexInterface;
import cz.artique.vad.network.vertex.VertexListener;
import cz.artique.vad.scenario.graph.Graph;

public class Network<S extends Subject> {
	private static final int NO_MESSGAGE_LOG_TRESHOLD = 100;

	private final Map<String, Graph> graphs = new HashMap<String, Graph>();
	private final Map<String, Vertex> vertices = new ConcurrentHashMap<String, Vertex>();
	private final List<String> allVertices = new ArrayList<String>();
	private final Map<String, List<String>> verticesInGraphs = new HashMap<String, List<String>>();

	private final Collection<S> subjects = new ArrayList<S>();

	private final int timestep;
	private final Behavior<S> behavior;
	private final Logger logger;

	private final Random random = new Random();

	public Network(Logger logger, Behavior<S> behavior, int timestep) {
		this.logger = logger;
		this.behavior = behavior;
		this.timestep = timestep;
	}

	private boolean deliver(Message message, String from) {
		Vertex target = vertices.get(message.getTarget());
		if (target != null) {
			target.deliver(message);
			logger.log(message, from);
			return true;
		} else {
			return false;
		}
	}

	public void registerGraph(Graph graph) {
		graphs.put(graph.getId(), graph);
		List<String> inGraph = new ArrayList<String>();
		verticesInGraphs.put(graph.getId(), inGraph);

		for (final Vertex v : graph.getVertices()) {
			vertices.put(v.getId(), v);
			allVertices.add(v.getId());
			inGraph.add(v.getId());

			v.addListener(new VertexListener() {
				@Override
				public void send(Message message) {
					if (!deliver(message, v.getId())) {
						UnreachableMessage unreachableMessage = new UnreachableMessage(v.getId(), message);
						v.deliver(unreachableMessage);
						logger.log(unreachableMessage, null);
					}
				}

				@Override
				public void log(String logEntry) {
					logger.log(new LogMessage(logEntry), v.getId());
				}

				@Override
				public void left() {
					vertices.remove(v.getId());
					allVertices.remove(v.getId());
					inGraph.remove(v.getId());
				}
			});
		}

		if (vertices.size() > NO_MESSGAGE_LOG_TRESHOLD) {
			logger.setLogMessages(false);
		}
	}

	public void start(String graph) {
		for (Vertex v : graphs.get(graph).getVertices()) {
			subjects.add(v.start(behavior, timestep));

			InitMessage message = new InitMessage(v.getId());
			deliver(message, null);
		}
	}

	public void join(String from, String to) {
		from = findVertex(from);
		to = findVertex(to);
		if (from == null || to == null) {
			logger.log("There is no random vertex to be chosen.");
		}

		JoinMessage message = new JoinMessage(from, to);
		deliver(message, null);
	}

	public void search(String from, String to) {
		from = findVertex(from);
		to = findVertex(to);
		if (from == null || to == null) {
			logger.log("There is no random vertex to be chosen.");
		}

		final String f = from;
		final String t = to;
		final SearchMessage message = new SearchMessage(from, to, new SearchCallback() {
			@Override
			public void onSuccess(VertexInterface vi) {
				vi.log("Success: Vertex " + t + " was found from vertex " + f);
			}

			@Override
			public void onFailure(VertexInterface vi) {
				vi.log("Failure: Vertex " + t + " was not found from vertex " + f);
			}
		});
		deliver(message, null);
	}

	public void leave(String vertex) {
		vertex = findVertex(vertex);
		if (vertex == null) {
			logger.log("There is no random vertex to be chosen.");
		}

		LeaveMessage message = new LeaveMessage(vertex);
		deliver(message, null);
	}

	private String findVertex(String id) {
		int rnd = id.indexOf("random");
		if (rnd == -1) {
			return id;
		}

		List<String> list;
		if (rnd == 0) {
			list = allVertices;
		} else {
			list = verticesInGraphs.get(getGraph(id));
		}

		if (list.size() == 0) {
			return null;
		} else if (list.size() == 1) {
			return list.get(0);
		} else {
			return list.get(random.nextInt(list.size()));
		}
	}

	private String getGraph(String id) {
		int i = id.indexOf("-");
		return id.substring(0, i);
	}

	public boolean isStabilized() {
		return behavior.isNetworkStabilized(logger, subjects, vertices.size());
	}
}
