package com.test.day01;

import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class FutureloanTest {
    @Test
    public void register() {
        // post请求-JSON参数
        String jsonStr = "{\n" +
                "\"mobile_phone\":\"13433631250\",\n" +
                "\"pwd\":\"12345678\",\n" +
                "\"type\":\"1\"\n" +
                "}";
        given().
                // given 设置测试预设（包括请求头、请求参数、请求体、cookies 等等）
                // 参数有中文，必须加charset=utf-8，否则会乱码
                //contentType("application/json;charset=utf-8").
                //contentType也可以放在header中
                header("Content-Type","application/json;charset=utf-8").
                header("X-Lemonban-Media-Type","lemonban.v1").
                body(jsonStr).
        when().
                // 发起请求
                post("http://api.lemonban.com/futureloan/member/register").
        then().
                // 解析结果、断言
                // 打印所有响应内容
                log().all();
    }

    @Test
    public void login() {
        // post请求-JSON参数
        String jsonStr = "{\n" +
                "\"mobile_phone\":\"13433631234\",\n" +
                "\"pwd\":\"12345678\",\n" +
                "\"type\":\"1\"\n" +
                "}";
        given().
                // given 设置测试预设（包括请求头、请求参数、请求体、cookies 等等）
                // 参数有中文，必须加charset=utf-8，否则会乱码
                //contentType("application/json;charset=utf-8").
                //contentType也可以放在header中
                header("Content-Type","application/json;charset=utf-8").
                header("X-Lemonban-Media-Type","lemonban.v1").
                body(jsonStr).
        when().
                // 发起请求
                post("http://api.lemonban.com/futureloan/member/login").
        then().
                // 解析结果、断言
                // 打印所有响应内容
                log().all();
    }
}
