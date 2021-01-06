package com.test.day01;

import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * X-Lemonban-Media-Type为lemonban.v2表示使用token鉴权
 */
public class FutureloanHomework {

    /**
     * 注册 --> 登录 --> 充值 --> 新增项目 --> 投资
     */
    @Test
    public void homework() {
        // 注册
        Map map = new HashMap();
        map.put("mobile_phone","13433631251");
        map.put("pwd", "12345678");
        map.put("type", 1);

        given().
                // given 设置测试预设（包括请求头、请求参数、请求体、cookies 等等）
                // 参数有中文，必须加charset=utf-8，否则会乱码
                //contentType("application/json;charset=utf-8").
                //contentType也可以放在header中
                        header("Content-Type","application/json;charset=utf-8").
                header("X-Lemonban-Media-Type","lemonban.v2").
                body(map).
                when().
                // 发起请求
                        post("http://api.lemonban.com/futureloan/member/register").
                then().
                // 解析结果
                // 打印所有响应内容
                        log().all();

        // 登录
        Response res =
        given().
                // given 设置测试预设（包括请求头、请求参数、请求体、cookies 等等）
                // 参数有中文，必须加charset=utf-8，否则会乱码
                //contentType("application/json;charset=utf-8").
                //contentType也可以放在header中
                header("Content-Type","application/json;charset=utf-8").
                header("X-Lemonban-Media-Type","lemonban.v2").
                body(map).
        when().
                // 发起请求
                post("http://api.lemonban.com/futureloan/member/login").
        then().
                // 解析结果
                // 获取响应结果
                extract().response();
                //获取响应信息里面的所有内容，响应头+响应体
                System.out.println(res.asString());
                //提取响应状态码
                System.out.println(res.statusCode());
                //获取接口响应时间，单位为ms
                System.out.println(res.time());
                //提取响应头
                System.out.println(res.header("Content-Type"));

                //提取响应体
                //提取响应体中的token
                //path方法 --> 使用Gpath路径表达式语法提取
                String tokenValue = res.path("data.token_info.token");
                System.out.println(tokenValue);
                //提取会员id
                int memberId = res.path("data.id");
                System.out.println(memberId);

        // 充值
        // 把请求数据放到map中
        map.clear();
        map.put("member_id", memberId);
        map.put("amount", 10000);
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
                // 解析结果
                log().all();

        // 新增项目
        map.clear();
        map.put("member_id", memberId);
        map.put("title", "人类移居火星");
        map.put("amount", 2000000);
        map.put("loan_rate", 16);
        map.put("loan_term", 15);
        map.put("loan_date_type", 2);
        map.put("bidding_days", 5);
        Response res2 =
        given().
                // given 设置测试预设（包括请求头、请求参数、请求体、cookies 等等）
                header("Content-Type","application/json;charset=utf-8").
                header("X-Lemonban-Media-Type","lemonban.v2").
                header("Authorization","Bearer " + tokenValue).
                //虽然指定了请求头中的Content-Type=application/json，这里也可以直接放map，因为引入了JSON序列化依赖，会直接转为JSON
                body(map).
        when().
                // 发起请求
                post("http://api.lemonban.com/futureloan/loan/add").
        then().
                // 解析结果
                log().all().
                extract().response();
                int loanId = res2.path("data.id");

        // 投资
        map.clear();
        map.put("member_id", memberId);
        map.put("loan_id", loanId);
        map.put("amount", 2000000);

        given().
                // given 设置测试预设（包括请求头、请求参数、请求体、cookies 等等）
                header("Content-Type","application/json;charset=utf-8").
                header("X-Lemonban-Media-Type","lemonban.v2").
                header("Authorization","Bearer " + tokenValue).
                //虽然指定了请求头中的Content-Type=application/json，这里也可以直接放map，因为引入了JSON序列化依赖，会直接转为JSON
                body(map).
        when().
                // 发起请求
                post("http://api.lemonban.com/futureloan/member/invest").
        then().
                // 解析结果
                log().all();
    }
}
