package com.test.day02;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import java.util.HashMap;
import java.util.Map;
import static io.restassured.RestAssured.given;

public class CookieTest {
    Map<String,String> cookieMap = new HashMap<String, String>();
    /**
     * cookie+session鉴权
     * 登录接口拿cookie信息
     */
    @Test
    public void authenticationWithSessionTest(){
        //登录接口请求
        Response res=
                given().
                        header("Content-Type","application/x-www-form-urlencoded; charset=UTF-8").
                        header("X-Lemonban-Media-Type","lemonban.v2").
                        formParam("loginame","admin").formParam("password","e10adc3949ba59abbe56e057f20f883e").
                when().
                        post("http://erp.lemfix.com/user/login").
                then().
                        log().all().extract().response();
                        //拿到cookie信息，存放到Map
                        //System.out.println(res.header("Set-Cookie"));
                        //System.out.println("cookie::"+res.getCookies()); //推荐
                        cookieMap = res.getCookies();
    }

    /**
     *  使用cookie
     *  getUserSession接口请求 必须要携带cookie里面保存的sessionid
     */
    @Test
    public void useCookieTest(){
        //getUserSession接口请求 必须要携带cookie里面保存的sessionid
        given().
                //因为cookie是存放在Map中的，所以必须使用cookies，而不是cookie
                cookies(cookieMap).
        when().
                get("http://erp.lemfix.com/user/getUserSession").
        then().
                log().all();
    }
}
