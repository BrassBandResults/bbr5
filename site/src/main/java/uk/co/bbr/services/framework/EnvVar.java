package uk.co.bbr.services.framework;

import lombok.experimental.UtilityClass;

@UtilityClass
public class EnvVar {
    public static String getEnv(String envVar, String defaultValue) {
        String value = System.getenv(envVar);
        if (value != null && value.length() > 0) {
            return value;
        }
        return defaultValue;
    }
}
