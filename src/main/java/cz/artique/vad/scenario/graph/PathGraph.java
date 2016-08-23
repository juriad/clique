package cz.artique.vad.scenario.graph;

import cz.artique.vad.network.vertex.Vertex;

public class PathGraph extends RandomGraph {

	private int degree;

	@Override
	protected void buildGraph2() {
		for (int i = 0; i < getSize(); i++) {
			Vertex v1 = vertices.get(i);
			for (int j = i - 1; j >= 0 && j >= i - getDegree(); j--) {
				Vertex v2 = vertices.get(i - 1);
				connect(v1, v2);
			}
		}
	}

	public int getDegree() {
		return degree;
	}

	public void setDegree(int degree) {
		this.degree = degree;
	}

	public static String getType() {
		return "path";
	}

	@Override
	public String toString() {
		return getId() + ": " + getType() + " " + getSize() + " (" + getConnectivity() + ", " + getDegree() + ")";
	}
}
