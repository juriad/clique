package cz.artique.vad.scenario.graph;

import java.util.Collections;

import cz.artique.vad.network.vertex.Vertex;

public abstract class RandomGraph extends Graph {

	private double connectivity;

	public double getConnectivity() {
		return connectivity;
	}

	public void setConnectivity(double connectivity) {
		this.connectivity = connectivity;
	}

	@Override
	protected void buildGraph() {
		generateVertices();
		Collections.shuffle(vertices);
		buildGraph2();
		Collections.sort(vertices);
	}

	protected abstract void buildGraph2();

	@Override
	protected void connect(Vertex v1, Vertex v2) {
		if (Math.random() < 0.5) {
			v1.connect(v2.getId());
			if (Math.random() < getConnectivity()) {
				v2.connect(v1.getId());
			}
		} else {
			v2.connect(v1.getId());
			if (Math.random() < getConnectivity()) {
				v1.connect(v2.getId());
			}
		}
	}
}
