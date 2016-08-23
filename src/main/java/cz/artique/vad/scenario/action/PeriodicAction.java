package cz.artique.vad.scenario.action;

import cz.artique.vad.network.Logger;
import cz.artique.vad.network.Network;

public abstract class PeriodicAction extends Action {
	private double period = 1;
	private int limit = 0;

	private transient int count = 0;

	public double getPeriod() {
		return period;
	}

	public void setPeriod(double period) {
		this.period = period;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	@Override
	public boolean perform(Network<?> network, Logger logger) {
		count++;
		return limit != 0 && count >= limit;
	}

	@Override
	public String toString() {
		return super.toString() + " with period " + getPeriod() + " (" + count + "/" + getLimit() + ")";
	}
}
