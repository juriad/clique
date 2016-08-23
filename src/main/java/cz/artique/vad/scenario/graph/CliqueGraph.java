package cz.artique.vad.scenario.graph;

import java.util.Random;

import cz.artique.vad.network.vertex.Vertex;

public class CliqueGraph extends RandomGraph {

	private double probability;

	public double getProbability() {
		return probability;
	}

	public void setProbability(double probability) {
		this.probability = probability;
	}

	@Override
	protected void buildGraph2() {
		Random random = new Random();

		for (int i = 0; i < getSize(); i++) {
			Vertex v1 = vertices.get(i);
			int k = i > 0 ? random.nextInt(i) : 0;
			for (int j = 0; j < i; j++) {
				Vertex v2 = vertices.get(j);
				if (j == k || random.nextDouble() < getProbability()) {
					connect(v1, v2);
				}
			}
		}
	}

	public static String getType() {
		return "clique";
	}

	@Override
	public String toString() {
		return getId() + ": " + getType() + " " + getSize() + " (" + getConnectivity() + ", " + getProbability() + ")";
	}
}
