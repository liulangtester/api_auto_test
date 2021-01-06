package com.test.day01;
import org.testng.annotations.Test;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
public class RestApiTestPost {
    @Test
    public void testPost01() {
        //1.post请求-form表单参数
        given().
                // given 设置测试预设（包括请求头、请求参数、请求体、cookies 等等）
                formParam("name1","王五").
                formParam("name2","刘八").
                //参数有中文，必须加charset=utf-8，否则会乱码
                //contentType("application/x-www-form-urlencoded;charset=utf-8").
                //contentType也可以放在header中
                header("Content-Type","application/x-www-form-urlencoded;charset=utf-8").
        when().
                // 发起请求
                post("http://httpbin.org/post").
        then().
                // 解析结果、断言
                //打印所有响应内容
                log().all();
                //打印响应体
                //log().body();
    }

    @Test
    public void testPost02() {
        //2.post请求-Map参数
        Map<String, String> map = new HashMap<String, String>();
        map.put("name","王五");
        map.put("age","90");
        map.put("address","深圳");
        map.put("sex","男");

        given().
                formParams(map).
                // given 设置测试预设（包括请求头、请求参数、请求体、cookies 等等）
                //参数有中文，必须加charset=utf-8，否则会乱码
                //contentType("application/x-www-form-urlencoded;charset=utf-8").
                //contentType也可以放在header中
                header("Content-Type","application/x-www-form-urlencoded;charset=utf-8").
        when().
                // 发起请求
                post("http://httpbin.org/post").
        then().
                // 解析结果、断言
                //打印响应体
                //log().body();
                //打印所有响应内容
                log().all();
    }

    @Test
    public void testPost03() {
        //3.post请求-JSON参数
        String jsonStr = "{\"mobile_phone\":\"abcdeabcdea\",\"pwd\":\"12345678\"}";
        given().
                // given 设置测试预设（包括请求头、请求参数、请求体、cookies 等等）
                //参数有中文，必须加charset=utf-8，否则会乱码
                //contentType("application/json;charset=utf-8").
                //contentType也可以放在header中
                header("Content-Type","application/json;charset=utf-8").
                body(jsonStr).
        when().
                // 发起请求
                post("http://httpbin.org/post").
        then().
                // 解析结果、断言
                //打印响应体
                //log().body();
                //打印所有响应内容
                log().all();
    }

    @Test
    public void testPost04() {
        //4.post请求-xml参数
        String xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\n" +
                "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
                "</project>";
        given().
                // given 设置测试预设（包括请求头、请求参数、请求体、cookies 等等）
                //参数有中文，必须加charset=utf-8，否则会乱码
                //contentType("text/xml;charset=utf-8").
                //contentType也可以放在header中
                header("Content-Type","text/xml;charset=utf-8").
                body(xmlStr).
        when().
                // 发起请求
                post("http://httpbin.org/post").
        then().
                // 解析结果、断言
                //打印响应体
                //log().body();
                //打印所有响应内容
                log().all();
    }

    @Test
    public void testPost05() {
        //5.post请求-多参数表单参数 上传文件(多个文件，即多参数，链式拼接即可)
        given().
                // given 设置测试预设（包括请求头、请求参数、请求体、cookies 等等）
                //参数有中文，必须加charset=utf-8，否则会乱码
                //contentType("multipart/form-data;charset=utf-8").
                //contentType也可以放在header中
                header("Content-Type","multipart/form-data;charset=utf-8").
                multiPart(new File("E:\\app.png")).
        when().
                // 发起请求
                post("http://httpbin.org/post").
        then().
                // 解析结果、断言
                //打印响应体
                //log().body();
                //打印所有响应内容
                log().all();
    }
}
