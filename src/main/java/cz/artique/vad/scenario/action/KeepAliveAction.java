package cz.artique.vad.scenario.action;

import cz.artique.vad.network.Logger;
import cz.artique.vad.network.Network;

public class KeepAliveAction extends Action implements TerminatingAction {

	@Override
	public boolean perform(Network<?> network, Logger logger) {
		return true;
	}

	public static String getType() {
		return "keep-alive";
	}

	@Override
	public String toString() {
		return "Action: keeps the system alive until it goes off " + super.toString();
	}

}
