package cz.artique.vad.scenario.action;

import cz.artique.vad.network.Logger;
import cz.artique.vad.network.Network;

public class TestStabilizeAction extends PeriodicAction implements TerminatingAction {

	private transient boolean stabilized;

	@Override
	public boolean perform(Network<?> network, Logger logger) {
		boolean ret = false;
		if (network.isStabilized()) {
			logger.log("System is stabilized.");
			if (!stabilized) {
				stabilized = true;
				ret = true;
			}
		} else {
			logger.log("System has not stabilized yet.");
		}

		super.perform(network, logger);
		return ret;
	}

	public static String getType() {
		return "test-stabilize";
	}

	@Override
	public String toString() {
		return "Action: tests whether the system has stabilized " + super.toString();
	}

}
