package com.wetrack.client.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.wetrack.model.Chat;

import java.lang.reflect.Type;

public class ChatSerializer implements JsonSerializer<Chat> {
    @Override
    public JsonElement serialize(Chat src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.addProperty("name", src.getName());
        result.add("members", context.serialize(src.getMemberNames()));
        return result;
    }
}
