package main.mmwork.com.mmworklib.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.util.Date;

public class DateDeSerializer implements JsonDeserializer<Date> {
	@Override
	public Date deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		return new Date(json.getAsJsonPrimitive().getAsLong());
	}
}
