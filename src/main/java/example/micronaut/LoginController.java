package example.micronaut;

import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.cookie.Cookie;
import io.micronaut.security.Secured;
import io.micronaut.security.authentication.AuthenticationFailed;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.authentication.UserDetails;
import io.micronaut.security.event.LoginFailedEvent;
import io.micronaut.security.event.LoginSuccessfulEvent;
import io.micronaut.security.handlers.LoginHandler;
import io.reactivex.Flowable;
import io.reactivex.Single;

@Secured("isAnonymous()")
@Controller("/login")
public class LoginController {

    private final ApplicationConfiguration applicationConfiguration;
    private final LoginHandler loginHandler;
    private final ApplicationEventPublisher eventPublisher;
    private final CookieAuthenticator cookieAuthenticator;

    public LoginController(ApplicationConfiguration applicationConfiguration,
                           LoginHandler loginHandler,
                           ApplicationEventPublisher eventPublisher,
                           CookieAuthenticator cookieAuthenticator) {
        this.applicationConfiguration = applicationConfiguration;
        this.loginHandler = loginHandler;
        this.eventPublisher = eventPublisher;
        this.cookieAuthenticator = cookieAuthenticator;
    }

    @Get
    public Single<HttpResponse> login(HttpRequest request) {

        Cookie cookie = request.getCookies().get(applicationConfiguration.getCookiename());
        if (cookie==null) {
            return Single.just(HttpResponse.status(HttpStatus.UNAUTHORIZED));
        }
        String cookieValue = cookie.getValue();

        Flowable<AuthenticationResponse> authenticationResponseFlowable = Flowable.fromPublisher(cookieAuthenticator.authenticate(cookieValue));

        return authenticationResponseFlowable.map(authenticationResponse -> {
            if (authenticationResponse.isAuthenticated()) {
                UserDetails userDetails = (UserDetails) authenticationResponse;
                eventPublisher.publishEvent(new LoginSuccessfulEvent(userDetails));
                return loginHandler.loginSuccess(userDetails, request);
            } else {
                AuthenticationFailed authenticationFailed = (AuthenticationFailed) authenticationResponse;
                eventPublisher.publishEvent(new LoginFailedEvent(authenticationFailed));
                return loginHandler.loginFailed(authenticationFailed);
            }
        }).first(HttpResponse.status(HttpStatus.UNAUTHORIZED));

    }
}
