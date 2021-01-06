package com.test.day02;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.restassured.RestAssured.given;

public class LoginTest {
    List<CaseInfo> caseInfoList = new ArrayList<CaseInfo>();

    @BeforeTest
    public void setUp() {
        caseInfoList = getCaseDataFromExcel(1);
    }

    @Test(dataProvider = "getLoginData")
    public void loginTest01(CaseInfo caseInfo) throws JsonProcessingException {
//        System.out.println(caseInfo);

        //JSON转Map 需要jackson-databind依赖
        ObjectMapper objectMapper = new ObjectMapper();
        //第一个参数：JSON字符串 第二个参数：转成的类型（Map）
        Map headerMap = objectMapper.readValue(caseInfo.getRequestHeader(), Map.class);
        //登录
        Response res =
                given().
                        headers(headerMap).
                        body(caseInfo.getInputParams()).
                when().
                        post("http://api.lemonban.com/futureloan" + caseInfo.getUrl()).
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
    public Object[] getLoginData() {
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
        importParams.setStartSheetIndex(1);
        File excelFile = new File("src/test/resources/api_testcases_futureloan_v2.xls");
        List<CaseInfo> caseInfoList = ExcelImportUtil.importExcel(excelFile, CaseInfo.class, importParams);
        return caseInfoList;
    }
}
