package com.wetrack.client.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.joda.time.LocalDate;

import java.lang.reflect.Type;

public class LocalDateTypeAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {

    @Override
    public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        String jsonStr = json.getAsString();
        if (jsonStr == null || jsonStr.trim().isEmpty())
            return null;
        try {
            return LocalDate.parse(jsonStr);
        } catch (IllegalArgumentException ex) {
            throw new JsonParseException("Received illegal date field: `" + jsonStr + "`", ex);
        }
    }

    @Override
    public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString());
    }
}
