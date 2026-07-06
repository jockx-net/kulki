package net.jockx.kulki.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Properties;

public final class Messages {
    private static final Properties props = new Properties();
    private static Locale currentLocale = Locale.getDefault();
    private static boolean loaded;

    private Messages() {}

    public static Locale getCurrentLocale() {
        return currentLocale;
    }

    public static String get(String key) {
        if (!loaded) {
            load();
        }
        String value = props.getProperty(key);
        return value != null ? value : "!" + key + "!";
    }

    public static String get(String key, Object... args) {
        return MessageFormat.format(get(key), args);
    }

    public static void setLocale(Locale locale) {
        currentLocale = locale;
        loaded = false;
        props.clear();
    }

    private static synchronized void load() {
        if (loaded) return;
        loaded = true;

        loadFile("i18n/Messages.properties");

        String lang = currentLocale.getLanguage();
        if (!lang.isEmpty() && !"en".equals(lang)) {
            String country = currentLocale.getCountry();
            if (!country.isEmpty()) {
                loadFile("i18n/Messages_" + lang + "_" + country + ".properties");
            }
            loadFile("i18n/Messages_" + lang + ".properties");
        }
    }

    private static void loadFile(String name) {
        try (var is = openResource(name)) {
            if (is != null) {
                props.load(new InputStreamReader(is, StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            // ignore
        }
    }

    private static InputStream openResource(String name) {
        InputStream is = Messages.class.getResourceAsStream("/" + name);
        if (is == null) {
            is = Messages.class.getClassLoader().getResourceAsStream(name);
        }
        if (is == null) {
            try {
                is = Messages.class.getModule().getResourceAsStream(name);
            } catch (IOException e) {
                // ignore
            }
        }
        return is;
    }
}
