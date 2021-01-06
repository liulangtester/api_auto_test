package com.test.day02;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import java.util.HashMap;
import java.util.Map;
import static io.restassured.RestAssured.given;

/**
 * token鉴权
 * X-Lemonban-Media-Type为lemonban.v2表示使用token鉴权
 */
public class TokenTest {

    /**
     * 先登录获取token鉴权信息，再充值
     */
    @Test
    public void loginAndRecharge() {
        //登录请求
        String jsonStr = "{\n" +
                "\"mobile_phone\":\"13433631234\",\n" +
                "\"pwd\":\"12345678\",\n" +
                "\"type\":\"1\"\n" +
                "}";
        Response res =
        given().
                // given 设置测试预设（包括请求头、请求参数、请求体、cookies 等等）
                // 参数有中文，必须加charset=utf-8，否则会乱码
                //contentType("application/json;charset=utf-8").
                //contentType也可以放在header中
                header("Content-Type","application/json;charset=utf-8").
                header("X-Lemonban-Media-Type","lemonban.v2").
                body(jsonStr).
        when().
                // 发起请求
                post("http://api.lemonban.com/futureloan/member/login").
        then().
                // 解析结果、断言
                // 获取响应结果
                extract().response();
                //提取响应体中的token
                String tokenValue = res.path("data.token_info.token");
                //提取会员id
                int memberId = res.path("data.id");


        //充值请求--使用token鉴权
        //把请求数据放到map中
        Map<String, String> map = new HashMap<String, String>();
        map.put("member_id", memberId + "");
        map.put("amount", "10000");
        given().
                // given 设置测试预设（包括请求头、请求参数、请求体、cookies 等等）
                header("Content-Type","application/json;charset=utf-8").
                header("X-Lemonban-Media-Type","lemonban.v2").
                header("Authorization","Bearer " + tokenValue).
                //虽然指定了请求头中的Content-Type=application/json，这里也可以直接放map，因为引入了JSON序列化依赖，会直接转为JSON
                body(map).
        when().
                // 发起请求
                post("http://api.lemonban.com/futureloan/member/recharge").
        then().
                // 解析结果、断言
                log().all();
    }
}
