package example.micronaut;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.cookie.Cookie;
import io.micronaut.security.Secured;
import io.micronaut.views.View;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Secured("isAnonymous()") // <1>
@Controller("/login")  // <2>
public class LoginAuthController {
    private final ApplicationConfiguration applicationConfiguration;

    public LoginAuthController(ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;

    }
    @Get("/auth") // <3>
    public HttpResponse auth() {
        final String path = "/login";
        final String cookieName = applicationConfiguration.getCookiename();
        return HttpResponse.status(HttpStatus.FOUND).headers((headers) ->
                headers.location(URI.create(path))
        ).cookie(Cookie.of(cookieName, "sherlock").path(path));
    }

    @Get("/authFailed") // <5>
    @View("auth")// <4>
    public Map<String, Object> authFailed() {
        return Collections.singletonMap("errors", true);
    }
}
