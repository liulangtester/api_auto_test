package com.test.day01;

import org.testng.annotations.Test;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;

public class RestApiTestGet {

    @Test
    public void testGet01() {
        //1.get请求不带参数
        given().
                // given 设置测试预设（包括请求头、请求参数、请求体、cookies 等等）
                when().
                // 发起请求
                get("http://httpbin.org/get").
                then().
                // 解析结果、断言
                //打印响应体
                log().body();
                //打印所有响应内容
                //log().all();
    }

    @Test
    public void testGet02() {
        //2.get请求带参数（1）
        given().
                // given 设置测试预设（包括请求头、请求参数、请求体、cookies 等等）
        when().
                // 发起请求
                get("http://httpbin.org/get?name=zhangsan&age=80").
        then().
                // 解析结果、断言
                //打印响应体
                log().body();
                //打印所有响应内容
                //log().all();
    }

    @Test
    public void testGet03() {
        //3.get请求带参数（2）
        given().
                queryParam("name","lisi").queryParam("age",70).
                // given 设置测试预设（包括请求头、请求参数、请求体、cookies 等等）
        when().
                // 发起请求
                get("http://httpbin.org/get").
        then().
                // 解析结果、断言
                //打印响应体
                //log().body();
                //打印所有响应内容
                log().all();
    }

    @Test
    public void testGet04() {
        //4.get请求带参数（3）
        Map<String, String> map = new HashMap<String, String>();
        map.put("name","王五");
        map.put("age","90");
        map.put("address","深圳");
        map.put("sex","男");

        given().
                queryParams(map).
                // given 设置测试预设（包括请求头、请求参数、请求体、cookies 等等）
        when().
                // 发起请求
                get("http://httpbin.org/get").
        then().
                // 解析结果、断言
                //打印响应体
                //log().body();
                //打印所有响应内容
                log().all();
    }
}
