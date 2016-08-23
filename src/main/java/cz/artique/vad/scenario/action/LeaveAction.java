package cz.artique.vad.scenario.action;

import cz.artique.vad.network.Logger;
import cz.artique.vad.network.Network;

public class LeaveAction extends PeriodicAction {
	private String vertex;

	public String getVertex() {
		return vertex;
	}

	public void setVertex(String vertex) {
		this.vertex = vertex;
	}

	public static String getType() {
		return "leave";
	}

	@Override
	public boolean perform(Network<?> network, Logger logger) {
		network.leave(vertex);
		return super.perform(network, logger);
	}

	@Override
	public String toString() {
		return "Action: " + getVertex() + " leaves " + super.toString();
	}
}
