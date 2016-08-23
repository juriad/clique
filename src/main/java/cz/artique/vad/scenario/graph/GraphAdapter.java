package cz.artique.vad.scenario.graph;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class GraphAdapter implements JsonSerializer<Graph>, JsonDeserializer<Graph> {
	Map<Class<? extends Graph>, String> types = new HashMap<Class<? extends Graph>, String>();
	Map<String, Class<? extends Graph>> graphs = new HashMap<String, Class<? extends Graph>>();
	{
		types.put(PathGraph.class, PathGraph.getType());
		graphs.put(PathGraph.getType(), PathGraph.class);

		types.put(TreeGraph.class, TreeGraph.getType());
		graphs.put(TreeGraph.getType(), TreeGraph.class);

		types.put(CliqueGraph.class, CliqueGraph.getType());
		graphs.put(CliqueGraph.getType(), CliqueGraph.class);

		types.put(ManualGraph.class, ManualGraph.getType());
		graphs.put(ManualGraph.getType(), ManualGraph.class);
	}

	@Override
	public Graph deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		return context.deserialize(json, graphs.get(json.getAsJsonObject().get("type").getAsString()));
	}

	@Override
	public JsonElement serialize(Graph src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject elem = context.serialize(src).getAsJsonObject();
		elem.addProperty("type", types.get(src.getClass()));
		return elem;
	}
}
