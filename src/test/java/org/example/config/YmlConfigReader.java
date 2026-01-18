package org.example.config;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

public class YmlConfigReader {
    private static Map<String, Object> config;

    static {
        Yaml yaml = new Yaml();
        InputStream inputStream = YmlConfigReader.class
                .getClassLoader()
                .getResourceAsStream("config.yml");

        if (inputStream == null){
            throw new RuntimeException("config.yml not found");

        }
        config = yaml.load(inputStream);
    }

    public static String getString(String key){
        return String.valueOf(config.get(key));
    }

    public static boolean getBoolean(String key){
        return Boolean.parseBoolean(getString(key));
    }

    public static int getInt(String key){
        return Integer.parseInt(getString(key));
    }
}
