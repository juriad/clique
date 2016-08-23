package cz.artique.vad.scenario.graph;

import cz.artique.vad.network.vertex.Vertex;

public class ManualGraph extends Graph {

	private String desc;

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	@Override
	protected void buildGraph() {
		generateVertices();
		String[] edges = desc.split("\\s+");
		for (String edge : edges) {
			String[] split = edge.split("\\b");
			if (split.length != 3) {
				continue;
			}

			Vertex v1 = vertices.get(Integer.valueOf(split[0], 10));
			String arrow = split[1];
			Vertex v2 = vertices.get(Integer.valueOf(split[2], 10));

			switch (arrow) {
			case "-":
			case "<->":
				connect(v1, v2);
				connect(v2, v1);
				break;
			case "->":
				connect(v1, v2);
				break;
			case "<-":
				connect(v2, v1);
				break;
			}
		}
	}

	public static String getType() {
		return "manual";
	}

	@Override
	public String toString() {
		return getId() + ": " + getType() + " " + getSize() + " (" + desc.trim().replaceAll("\\s+", ", ") + ")";
	}
}
