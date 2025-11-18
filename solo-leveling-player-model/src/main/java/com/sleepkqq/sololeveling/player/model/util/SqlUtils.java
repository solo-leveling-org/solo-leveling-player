package com.sleepkqq.sololeveling.player.model.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SqlUtils {

  public static String loadSqlFile(String path) {
    try (var is = SqlUtils.class.getClassLoader().getResourceAsStream(path)) {

      if (is == null) {
        throw new RuntimeException("SQL file not found: " + path);
      }

      return new String(is.readAllBytes(), StandardCharsets.UTF_8).trim();

    } catch (IOException e) {
      throw new RuntimeException("Failed to load SQL file: " + path, e);
    }
  }
}
