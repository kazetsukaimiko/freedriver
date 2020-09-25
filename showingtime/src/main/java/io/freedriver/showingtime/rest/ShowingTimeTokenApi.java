package io.freedriver.showingtime.rest;

import io.freedriver.showingtime.model.Creds;
import io.freedriver.showingtime.model.TokenResponse;
import io.freedriver.showingtime.model.UsernameQuery;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Path("/")
public interface ShowingTimeTokenApi {
    String HOST = "api.showingtime.com";
    String ACCESS_TOKEN = "access_token";
    String TIME = "time";
    String UNREGISTERED_GRANT = "client_id=SellerAppUser&client_secret=53LL3R@pp&grant_type=client_credentials&sub_grant_type=unregistered";

    static TokenResponse loginAsUser(ShowingTimeTokenApi tokenApi, Creds creds) {
        return ShowingTimeTokenApi.loginAsUser(tokenApi, creds.getUsername(), creds.getPassword());
    }

    static TokenResponse loginAsUser(ShowingTimeTokenApi tokenApi, String username, String password) {
        TokenResponse anonymous = getAnonymousGrant(tokenApi);
        Instant now = Instant.now();
        UsernameQuery usernameQuery = tokenApi.usernameQuery(username, anonymous.getAccess_token(), now.toEpochMilli());
        if (usernameQuery.isUsernameExists()) {
            TokenResponse login = tokenApi.tokenRequest(loginAsUserString(username, password));
            if (login.getUser_id() != null) {
                return login;
            } else {
                throw new WebApplicationException("Unauthorized: Invalid credentials (missing userid)", Response.Status.FORBIDDEN);
            }
        } else {
            throw new WebApplicationException("Unauthorized: No such user", Response.Status.FORBIDDEN);
        }
    }

    static TokenResponse getAnonymousGrant(ShowingTimeTokenApi tokenApi) {
        return tokenApi.tokenRequest(UNREGISTERED_GRANT);
    }

    @POST
    @Path("/token")
    @Consumes(MediaType.TEXT_PLAIN)
    TokenResponse tokenRequest(String tokenRequest);

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/api/V1/UnregisteredSeller/UsernameExists")
    UsernameQuery usernameQuery(@FormParam("username") String username,
                                @QueryParam(ACCESS_TOKEN) String access_token,
                                @QueryParam(TIME) long time);

    static String loginAsUserString(String username, String password) {
        return "client_id=SellerAppUser"
                + "&client_secret=53LL3R@pp"
                + "&grant_type=password"
                + "&username="
                + URLEncoder.encode(username, StandardCharsets.UTF_8)
                + "&password="
                + URLEncoder.encode(password, StandardCharsets.UTF_8);
    }



}
