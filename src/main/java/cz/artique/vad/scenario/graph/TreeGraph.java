package cz.artique.vad.scenario.graph;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import cz.artique.vad.network.vertex.Vertex;

public class TreeGraph extends RandomGraph {

	private int degree;

	public int getDegree() {
		return degree;
	}

	public void setDegree(int degree) {
		this.degree = degree;
	}

	@Override
	protected void buildGraph2() {
		Random random = new java.util.Random();

		for (int i = 0; i < getSize(); i++) {
			Vertex v1 = vertices.get(i);
			Set<Integer> s = new HashSet<Integer>();

			for (int d = 0; d < Math.min(getDegree(), i); d++) {
				int j;
				do {
					j = random.nextInt(i);
				} while (s.contains(j));
				Vertex v2 = vertices.get(j);
				s.add(j);
				connect(v1, v2);
			}
		}
	}

	public static String getType() {
		return "tree";
	}

	@Override
	public String toString() {
		return getId() + ": " + getType() + " " + getSize() + " (" + getConnectivity() + ", " + getDegree() + ")";
	}
}
