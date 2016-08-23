package cz.artique.vad.network.vertex;

import java.util.Collection;

import cz.artique.vad.network.Logger;

public interface Behavior<S extends Subject> {
	public S createSubject(VertexInterface si, Collection<String> neighbors);

	public boolean isNetworkStabilized(Logger logger, Collection<S> subjects, int size);

	public String getType();
}
