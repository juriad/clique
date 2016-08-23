package cz.artique.vad.scenario.action;

import cz.artique.vad.network.Logger;
import cz.artique.vad.network.Network;

public class SearchAction extends PeriodicAction {
	private String from;
	private String to;

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public static String getType() {
		return "search";
	}

	@Override
	public boolean perform(Network<?> network, Logger logger) {
		network.search(from, to);
		return super.perform(network, logger);
	}

	@Override
	public String toString() {
		return "Action: " + getFrom() + " searches for " + getTo() + " " + super.toString();
	}
}
