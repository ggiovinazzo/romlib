package com.github.jakz.romlib.json;

import java.lang.reflect.Type;

import com.github.jakz.romlib.data.game.attributes.Attribute;
import com.github.jakz.romlib.data.game.attributes.GameAttribute;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class GameSavedAttributeAdapter implements JsonSerializer<GameSavedAttribute>, JsonDeserializer<GameSavedAttribute> {
  @Override
  public JsonElement serialize(GameSavedAttribute src, Type type, JsonSerializationContext context)
  {
    JsonObject object = new JsonObject();
    object.add("key", context.serialize(src.key));
    object.add("value", context.serialize(src.value));
    return object;
  }
  
  @Override
  public GameSavedAttribute deserialize(JsonElement json, Type type, JsonDeserializationContext context)
  {
    Attribute attribute = context.deserialize(json.getAsJsonObject().get("key"), GameAttribute.class);
    
    if (attribute != null && attribute.getType() != null)
    {
      Object value = context.deserialize(json.getAsJsonObject().get("value"), attribute.getType());
      return new GameSavedAttribute(attribute, value);
    }
    
    return null;
  }
}