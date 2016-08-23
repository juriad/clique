package cz.artique.vad.scenario.action;

import java.util.List;

import cz.artique.vad.network.Logger;
import cz.artique.vad.network.Network;

public class StartAction extends Action {
	private List<String> graphs;

	public List<String> getGraphs() {
		return graphs;
	}

	public void setGraph(List<String> graphs) {
		this.graphs = graphs;
	}

	public static String getType() {
		return "start";
	}

	@Override
	public boolean perform(Network<?> network, Logger logger) {
		for (String g : getGraphs()) {
			network.start(g);
		}
		return true;
	}

	@Override
	public String toString() {
		return "Action: Start graphs " + String.join(", ", getGraphs()) + " " + super.toString();
	}
}
