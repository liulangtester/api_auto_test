package com.test.day03;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.restassured.RestAssured.given;

public class GetUserInfoTest {
    List<CaseInfo> caseInfoList = new ArrayList<CaseInfo>();

    @BeforeClass
    //从Excel获取用例数据
    public void setUp() {
        caseInfoList = getCaseDataFromExcel(2);
        //统一参数化
        caseInfoList = paramsReplace(caseInfoList);
    }

    @Test(dataProvider = "getUserInfoData")
    public void getUserInfoTest(CaseInfo caseInfo) throws JsonProcessingException {
//        //参数化替换
//        //1. 接口url地址中的{{member_id}}替换成环境变量中保存的值（登录请求响应获取到的值）
//        String url = regexReplace(caseInfo.getUrl());
//        //2. 响应结果中{{member_id}}给替换成环境变量中保存的值（登录请求响应获取到的值）
//        String expected = regexReplace(caseInfo.getExpected());
//        //3. 请求头中的{{token}}替换成环境变量中保存的值
//        String requestHeaders = regexReplace(caseInfo.getRequestHeader());

        //JSON转Map 需要jackson-databind依赖  比较坑
        ObjectMapper objectMapper = new ObjectMapper();
        //第一个参数：JSON字符串 第二个参数：转成的类型（Map）
        //Map headerMap = objectMapper.readValue(requestHeaders, Map.class);

        ////JSON转Map 需要fastjson依赖 推荐用
        Map headerMap = JSONObject.parseObject(caseInfo.getRequestHeader(), Map.class);


        //登录
        Response res =
                given().
                        headers(headerMap).
                when().
                        get("http://api.lemonban.com/futureloan" + caseInfo.getUrl()).
                then().
                        extract().response();
        //断言
        //把断言数据转换为Map
        ObjectMapper objectMapper2 = new ObjectMapper();
        Map expectedMap = objectMapper2.readValue(caseInfo.getExpected(), Map.class);
        //遍历map集合
        Set<Map.Entry<String, Object>> set = expectedMap.entrySet();
        for (Map.Entry<String, Object> map: set) {
            //实际结果 与 期望结果 比较
            Assert.assertEquals(res.path(map.getKey()),map.getValue());
        }
    }

    @DataProvider
    public Object[] getUserInfoData() {
        Object[] datas = caseInfoList.toArray();
        return datas;
    }

    /**
     * 从Excel读取用例数据
     * @param index
     * @return
     */
    public List<CaseInfo> getCaseDataFromExcel(int index) {
        //使用EasyPOI读取Excel数据
        ImportParams importParams = new ImportParams();
        //设置读取Excel起始sheet值
        importParams.setStartSheetIndex(index);
        File excelFile = new File("src/test/resources/api_testcases_futureloan_v2.xls");
        List<CaseInfo> caseInfoList = ExcelImportUtil.importExcel(excelFile, CaseInfo.class, importParams);
        return caseInfoList;
    }

    /**
     * 根据正则替换字符串中的内容
     * @param sourceStr 需要匹配的原始字符串
     * @return
     */
    public String regexReplace(String sourceStr) {
        //定义正则表达式
        String regex = "\\{\\{(.*?)\\}\\}";
        //通过正则表达式编译出一个匹配器
        Pattern pattern = Pattern.compile(regex);
        //进行匹配：参数是需要匹配的字符串
        Matcher matcher = pattern.matcher(sourceStr);
        //保存匹配到的整个字符串，如{{member_id}}
        String findStr = "";
        //保存匹配到的()里面的内容，如member_id
        String singleStr = "";
        //连续查找、匹配
        while (matcher.find()) {
            //保存匹配到的整个字符串，如{{member_id}}
            findStr = matcher.group(0);
            //保存匹配到的()里面的内容，如member_id
            singleStr = matcher.group(1);
        }
        //获取环境变量里的值
        Object replaceStr = GlobalEnvironment.envData.get(singleStr);
        //替换匹配到的字符串 findStr：被替换字符串  replaceStr：替换进去的字符串
        return sourceStr.replace(findStr, replaceStr+"");
    }

    /**
     * 对获取的excel数据统一参数化
     * @param caseInfoList
     * @return
     */
    public List<CaseInfo> paramsReplace(List<CaseInfo> caseInfoList) {
        //对用例中的四个地方做参数化处理：请求头、接口地址、输入参数、期望响应结果
        for (CaseInfo caseInfo : caseInfoList) {
            //判空处理，避免空指针
            if (caseInfo.getRequestHeader() != null) {
                String requestHeader = regexReplace(caseInfo.getRequestHeader());
                caseInfo.setRequestHeader(requestHeader);
            }

            if (caseInfo.getUrl() != null) {
                String url = regexReplace(caseInfo.getUrl());
                caseInfo.setUrl(url);
            }

            if (caseInfo.getInputParams() != null) {
                String inputParams = regexReplace(caseInfo.getInputParams());
                caseInfo.setInputParams(inputParams);
            }

            if (caseInfo.getExpected() != null) {
                String expected = regexReplace(caseInfo.getExpected());
                caseInfo.setExpected(expected);
            }
        }
        return caseInfoList;
    }
}
