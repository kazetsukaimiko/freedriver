package io.freedriver.showingtime.rest;


import io.freedriver.showingtime.model.ShowingNotifications;
import io.freedriver.showingtime.model.UserRequest;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface ShowingTimeApi {

    String HOST = "api.showingtime.com";

    // GetBadgeCounts
    // ?userId=38953069
    // &token=3B0881D65D6861DA88DBF98CFC6FF423D2EAC282
    // &access_token=Un2hL6s7kg8gVmZYNxkvoZHCnMFCE6N1UmdoUIY3UwgNFf241UU6F3ytrB2IliDPmfIi5AocrrRCmxfT5TiiOH-A1AigUxtCNARNN4JvA-oaKfjoWYKH9A-PqN4waV1P6narfDw93wnqWWPPRW-UM8KB_q7v9G3qiMy2eez0803zmASFOZuxnq8LAULjvRxtRRu0Yn_i1PINOkDxcOmld3ZlLnrjIMqhBuMtPopEWDSLFUMhAWrvW-hc8JiQ57oUzaHA7B9ijd5t9qwvumJ_ObsJEwuyMfgt2_oVTMKzQeeUBGFO-gH92ShOyhwZFbbMJT1l4l4nTE2wI0IHnwoZekr775VJeaq1O5Xw5iIVd_aFjCvvY2yYtCdz0D5q6d7m
    // &time=1596839293805

    // "GetNotifications
    // ?userId=38953069&
    // token=3B0881D65D6861DA88DBF98CFC6FF423D2EAC282
    // &lower=1
    // &upper=25
    // &access_token=Un2hL6s7kg8gVmZYNxkvoZHCnMFCE6N1UmdoUIY3UwgNFf241UU6F3ytrB2IliDPmfIi5AocrrRCmxfT5TiiOH-A1AigUxtCNARNN4JvA-oaKfjoWYKH9A-PqN4waV1P6narfDw93wnqWWPPRW-UM8KB_q7v9G3qiMy2eez0803zmASFOZuxnq8LAULjvRxtRRu0Yn_i1PINOkDxcOmld3ZlLnrjIMqhBuMtPopEWDSLFUMhAWrvW-hc8JiQ57oUzaHA7B9ijd5t9qwvumJ_ObsJEwuyMfgt2_oVTMKzQeeUBGFO-gH92ShOyhwZFbbMJT1l4l4nTE2wI0IHnwoZekr775VJeaq1O5Xw5iIVd_aFjCvvY2yYtCdz0D5q6d7m
    // &time=1596839294003
    @GET
    @Path("/api/V1/SellerApp/GetNotifications")
    ShowingNotifications getNotifications(@BeanParam UserRequest request);


    // GetListings
    // ?userId=38953069
    // &token=3B0881D65D6861DA88DBF98CFC6FF423D2EAC282
    // &ignoreCache=false
    // &access_token=Un2hL6s7kg8gVmZYNxkvoZHCnMFCE6N1UmdoUIY3UwgNFf241UU6F3ytrB2IliDPmfIi5AocrrRCmxfT5TiiOH-A1AigUxtCNARNN4JvA-oaKfjoWYKH9A-PqN4waV1P6narfDw93wnqWWPPRW-UM8KB_q7v9G3qiMy2eez0803zmASFOZuxnq8LAULjvRxtRRu0Yn_i1PINOkDxcOmld3ZlLnrjIMqhBuMtPopEWDSLFUMhAWrvW-hc8JiQ57oUzaHA7B9ijd5t9qwvumJ_ObsJEwuyMfgt2_oVTMKzQeeUBGFO-gH92ShOyhwZFbbMJT1l4l4nTE2wI0IHnwoZekr775VJeaq1O5Xw5iIVd_aFjCvvY2yYtCdz0D5q6d7m
    // &time=1596839293822



}
