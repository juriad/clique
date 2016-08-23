package cz.artique.vad.scenario.graph;

import java.util.ArrayList;
import java.util.List;

import cz.artique.vad.network.vertex.Vertex;

public abstract class Graph {
	private String id;
	private int size;
	protected transient List<Vertex> vertices;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	protected void generateVertices() {
		vertices = new ArrayList<Vertex>(getSize());
		for (int i = 0; i < getSize(); i++) {
			vertices.add(new Vertex(getVid(i)));
		}
	}

	public List<Vertex> getVertices() {
		if (vertices == null) {
			buildGraph();
		}

		return vertices;
	}

	protected abstract void buildGraph();

	private String getVid(int i) {
		int l = ((getSize() - 1) + "").length();
		return id + "-" + String.format("%0" + l + "d", i);
	}

	protected void connect(Vertex v1, Vertex v2) {
		v1.connect(v2.getId());
	}
}
