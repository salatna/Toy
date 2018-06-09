package ru.antalas.ssl;

import com.typesafe.config.Config;

import javax.net.ssl.SSLContext;
import java.security.NoSuchAlgorithmException;

import static java.lang.System.setProperty;

public class SSL {
    private static final String KEYSTORE_PASS = "javax.net.ssl.keyStorePassword";
    private static final String KEYSTORE = "javax.net.ssl.keyStore";
    private static final String TRUSTSTORE = "javax.net.ssl.trustStore";
    private static final String TRUSTSTORE_PASS = "javax.net.ssl.trustStorePassword";

    public static SSLContext initSSL(Config config) throws NoSuchAlgorithmException {
        setProperty(KEYSTORE, config.getString(KEYSTORE));
        setProperty(KEYSTORE_PASS, config.getString(KEYSTORE_PASS));

        setProperty(TRUSTSTORE, getSafeString(config, TRUSTSTORE));
        setProperty(TRUSTSTORE_PASS, getSafeString(config, TRUSTSTORE_PASS));

        return SSLContext.getDefault();
    }

    private static String getSafeString(Config config, String property) {
        return config.hasPath(property) ? config.getString(property) : "";
    }
}
