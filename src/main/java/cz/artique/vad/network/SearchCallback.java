package cz.artique.vad.network;

import cz.artique.vad.network.vertex.VertexInterface;

public interface SearchCallback {
	public void onSuccess(VertexInterface vi);

	public void onFailure(VertexInterface vi);
}
