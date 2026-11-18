package com.sqat.orangehrm.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;

/**
 * Reads {@code config.properties} from the classpath.
 * A JVM system property with the same key (e.g. {@code -Dheadless=true}) always wins.
 */
public final class ConfigReader {

    private static final String FILE = "config.properties";
    private static final Properties PROPS = load();

    private ConfigReader() {
    }

    private static Properties load() {
        Properties p = new Properties();
        try (InputStream in = ConfigReader.class.getClassLoader().getResourceAsStream(FILE)) {
            if (in == null) {
                throw new IllegalStateException(FILE + " not found on the classpath");
            }
            p.load(in);
            return p;
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read " + FILE, e);
        }
    }

    public static String get(String key) {
        String value = System.getProperty(key, PROPS.getProperty(key));
        if (value == null) {
            throw new IllegalArgumentException("Missing configuration key: " + key);
        }
        return value.trim();
    }

    public static int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }

    public static String baseUrl() {
        return get("base.url");
    }
}
