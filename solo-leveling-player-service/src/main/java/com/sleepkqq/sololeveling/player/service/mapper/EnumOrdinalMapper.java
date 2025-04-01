package com.sleepkqq.sololeveling.player.service.mapper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.IOException;

public class EnumOrdinalMapper {

  public static <E extends Enum<E>> ObjectMapper createMapper(Class<E> enumType) {
    var mapper = new ObjectMapper();
    var module = new SimpleModule();

    module.addSerializer(enumType, new JsonSerializer<Enum<?>>() {
      @Override
      public void serialize(Enum<?> value, JsonGenerator gen, SerializerProvider provider)
          throws IOException {
        gen.writeNumber(value.ordinal());
      }
    });

    module.addDeserializer(enumType, new JsonDeserializer<>() {
      @Override
      public E deserialize(JsonParser p, DeserializationContext ctx)
          throws IOException {
        var ordinal = p.getIntValue();
        return enumType.getEnumConstants()[ordinal];
      }
    });

    mapper.registerModule(module);
    return mapper;
  }
}