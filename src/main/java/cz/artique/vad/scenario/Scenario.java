package cz.artique.vad.scenario;

import java.io.Reader;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import cz.artique.vad.scenario.action.Action;
import cz.artique.vad.scenario.action.ActionAdapter;
import cz.artique.vad.scenario.graph.Graph;
import cz.artique.vad.scenario.graph.GraphAdapter;

public class Scenario {
	private String behavior;
	private List<Graph> graphs;
	private List<Action> actions;

	public String getBehavior() {
		return behavior;
	}

	public void setBehavior(String behavior) {
		this.behavior = behavior;
	}

	public List<Graph> getGraphs() {
		return graphs;
	}

	public void setGraphs(List<Graph> graphs) {
		this.graphs = graphs;
	}

	public List<Action> getActions() {
		return actions;
	}

	public void setActions(List<Action> actions) {
		this.actions = actions;
	}

	private static Gson gson = null;
	static {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Graph.class, new GraphAdapter());
		builder.registerTypeAdapter(Action.class, new ActionAdapter());
		gson = builder.create();
	}

	public static Scenario fromFile(Reader r) {
		Scenario scenario = gson.fromJson(r, Scenario.class);
		return scenario;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Scenario:");
		sb.append("Behavior: " + getBehavior());
		for (Graph g : getGraphs()) {
			sb.append("\n");
			sb.append(g.toString());
		}
		for (Action a : getActions()) {
			sb.append("\n");
			sb.append(a.toString());
		}
		return sb.toString();
	}
}
