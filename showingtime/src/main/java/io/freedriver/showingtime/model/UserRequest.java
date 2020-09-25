package io.freedriver.showingtime.model;

import javax.ws.rs.QueryParam;
import java.time.Instant;

public class UserRequest {
    // GetNotifications
    // ?userId=38953069
    // &token=3B0881D65D6861DA88DBF98CFC6FF423D2EAC282
    // &lower=1
    // &upper=25
    // &access_token=_j0rq-WJ9XE8PWE80Oot3jgopcIPlgAZ0pMidl9kwNvpdFnYyvJAo_wZPO05XoRtWG5GVYHl7z4VHirF_S6VoNTOjNE_TKRBGervAW8hgCLZMbJeIkmooXfHhkboJV2FSFs_4BkJIQ0zLT0pkQxSU9B48_czs7zv2dR_LNqB6JoSJujAhKY8Yw7OLXb0WkjeMStCKbzrfNQUDZIp01YYy8-MuuztzbloyDgWLBqyfBTrXdgaK1z05MsotOk5ji5eHS3Qo7DVYS-fSFMHZjGbSF8pXOr1khAUXsOlr7JVqpWtj1QTEi-yFs4NPi6ML1FmzbM_e6x2r1vG9d_tad-YnAv-_iyPSBCwBWQ2Byr1ynGwr-ArGu660SZuSGK14GT-
    // &time=1596843118632

    @QueryParam("userId")
    private Long userId;

    @QueryParam("token")
    private String userToken;

    @QueryParam("lower")
    private Integer lower = 1;

    @QueryParam("upper")
    private Integer upper = 25;

    @QueryParam("ignoreCache")
    private Boolean ignoreCache;

    @QueryParam("access_token")
    private String access_token;

    @QueryParam("time")
    private long time;

    public UserRequest() {
    }

    public UserRequest(TokenResponse token) {
        setUserId(token.getUser_id());
        setUserToken(token.getUser_token());
        setTime(Instant.now().toEpochMilli());
        setAccess_token(token.getAccess_token());
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public Boolean getIgnoreCache() {
        return ignoreCache;
    }

    public void setIgnoreCache(Boolean ignoreCache) {
        this.ignoreCache = ignoreCache;
    }

    public Integer getLower() {
        return lower;
    }

    public void setLower(Integer lower) {
        this.lower = lower;
    }

    public Integer getUpper() {
        return upper;
    }

    public void setUpper(Integer upper) {
        this.upper = upper;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
