package cz.artique.vad.network.message;

import cz.artique.vad.network.SearchCallback;

public class SearchMessage extends Message {
	private final String id;
	private final SearchCallback callback;

	public SearchMessage(String target, String id, SearchCallback callback) {
		super(target);

		this.id = id;
		this.callback = callback;
	}

	public String getId() {
		return id;
	}

	public SearchCallback getCallback() {
		return callback;
	}

	@Override
	public String toString() {
		return getTarget() + "->Search(" + getId() + ")";
	}
}
