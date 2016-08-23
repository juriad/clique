package cz.artique.vad.scenario.action;

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

public class ActionAdapter implements JsonSerializer<Action>, JsonDeserializer<Action> {
	Map<Class<? extends Action>, String> types = new HashMap<Class<? extends Action>, String>();
	Map<String, Class<? extends Action>> actions = new HashMap<String, Class<? extends Action>>();
	{
		types.put(StartAction.class, StartAction.getType());
		actions.put(StartAction.getType(), StartAction.class);

		types.put(JoinAction.class, JoinAction.getType());
		actions.put(JoinAction.getType(), JoinAction.class);

		types.put(SearchAction.class, SearchAction.getType());
		actions.put(SearchAction.getType(), SearchAction.class);

		types.put(LeaveAction.class, LeaveAction.getType());
		actions.put(LeaveAction.getType(), LeaveAction.class);

		types.put(KeepAliveAction.class, KeepAliveAction.getType());
		actions.put(KeepAliveAction.getType(), KeepAliveAction.class);

		types.put(TestStabilizeAction.class, TestStabilizeAction.getType());
		actions.put(TestStabilizeAction.getType(), TestStabilizeAction.class);
	}

	@Override
	public Action deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		return context.deserialize(json, actions.get(json.getAsJsonObject().get("type").getAsString()));
	}

	@Override
	public JsonElement serialize(Action src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject elem = context.serialize(src).getAsJsonObject();
		elem.addProperty("type", types.get(src.getClass()));
		return elem;
	}
}
