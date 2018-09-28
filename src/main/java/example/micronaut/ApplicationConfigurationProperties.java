package example.micronaut;

import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties("application")
public class ApplicationConfigurationProperties implements ApplicationConfiguration {

    public static final String DEFAULT_COOKIENAME = "cas";

    private String cookiename = DEFAULT_COOKIENAME;

    @Override
    public String getCookiename() {
        return cookiename;
    }


    public void setCookiename(String cookiename) {
        this.cookiename = cookiename;
    }
}
