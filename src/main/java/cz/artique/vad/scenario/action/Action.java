package cz.artique.vad.scenario.action;

import cz.artique.vad.network.Logger;
import cz.artique.vad.network.Network;
import cz.artique.vad.network.vertex.Vertex;
import cz.artique.vad.scenario.Scenario;
import cz.artique.vad.scenario.graph.Graph;

public abstract class Action {
	private double delay = 0;

	public double getDelay() {
		return delay;
	}

	public void setDelay(double delay) {
		this.delay = delay;
	}

	protected Graph lookupGraph(Scenario scenario, String graphId) {
		for (Graph g : scenario.getGraphs()) {
			if (g.getId().equals(graphId)) {
				return g;
			}
		}
		return null;
	}

	protected Vertex lookupVertex(Scenario scenario, String vertexId) {
		String[] split = vertexId.split("-");
		Graph graph = lookupGraph(scenario, split[0]);
		return graph.getVertices().get(Integer.valueOf(split[1]));
	}

	public abstract boolean perform(Network<?> network, Logger logger);

	@Override
	public String toString() {
		return "at " + getDelay();
	}
}
