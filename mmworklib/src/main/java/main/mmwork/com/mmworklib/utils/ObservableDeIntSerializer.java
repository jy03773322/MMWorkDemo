package main.mmwork.com.mmworklib.utils;

import android.databinding.ObservableInt;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class ObservableDeIntSerializer implements JsonDeserializer<ObservableInt> {
    @Override
    public ObservableInt deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return new ObservableInt(json.getAsInt());
    }
}
