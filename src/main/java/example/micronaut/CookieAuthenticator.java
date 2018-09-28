package example.micronaut;

import io.micronaut.security.authentication.AuthenticationFailed;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.authentication.UserDetails;
import io.reactivex.Flowable;
import org.reactivestreams.Publisher;

import javax.inject.Singleton;
import java.util.Collections;

@Singleton
public class CookieAuthenticator {

    public Publisher<AuthenticationResponse> authenticate(String cookieValue) {
        boolean isvalid = isValid(cookieValue);
        String username = cookieValue; // TODO infer the username showhow
        return Flowable.just(isvalid ? new UserDetails(username, Collections.emptyList()) : new AuthenticationFailed());
    }

    boolean isValid(String cookieValue) {
        // TODO validate this somehoew
        return true;
    }
}
