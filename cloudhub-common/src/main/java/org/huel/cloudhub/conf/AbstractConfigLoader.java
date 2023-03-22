package org.huel.cloudhub.conf;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author RollW
 */
public class AbstractConfigLoader {
    private final Properties properties;

    public AbstractConfigLoader(InputStream inputStream) throws IOException {
        properties = new Properties();
        properties.load(inputStream);
    }

    public String get(String key) {
        return properties.getProperty(key, null);
    }

    public String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    protected int getInt(String key, String defaultValue) {
        return Integer.parseInt(properties.getProperty(key, defaultValue));
    }

    protected int getInt(String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value != null) {
            return Integer.parseInt(value);
        }
        return defaultValue;
    }

    protected static InputStream openConfigInput(Class<?> appClz) throws IOException {
        File confFile = tryFile();
        if (!confFile.exists()) {
            return appClz.getResourceAsStream("/cloudhub.conf");
        }
        return new FileInputStream(confFile);
    }

    private static File tryFile() {
        File confFile = new File("conf/cloudhub.conf");
        if (confFile.exists()) {
            return confFile;
        }
        return new File("cloudhub.conf");
    }
}
