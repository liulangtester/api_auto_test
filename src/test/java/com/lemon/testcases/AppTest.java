package com.lemon.testcases;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lemon.base.BaseCase;
import com.lemon.pojo.CaseInfo;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static io.restassured.RestAssured.given;


public class AppTest extends BaseCase {
    List<CaseInfo> caseInfoList = new ArrayList<CaseInfo>();

    @BeforeClass
    //从Excel获取用例数据
    public void setUp() {
        caseInfoList = getCaseDataFromExcel(0);
        //参数化替换
        caseInfoList = paramsReplace(caseInfoList);
    }

    @Test(dataProvider = "getData")
    public void getTest(CaseInfo caseInfo) {

        //JSON转Map
        Map headerMap = fromJsonToMap(caseInfo.getRequestHeader());

        //重定向日志输出到单独的文件中
        String logPath = addLogToFile(caseInfo);

        if (caseInfo.getMethod().equals("GET")) {
            Response res =
                    given().
                            log().all().
                            headers(headerMap).
                    when().
                            get(caseInfo.getUrl()).
                    then().
                            log().all().
                            extract().response();

            //将日志添加到Allure报告中
            addLogToAllure(logPath);
            //断言
            assertExpetced(caseInfo, res);
        } else if (caseInfo.getMethod().equals("POST")) {
            //新增项目
            Response res =
                    given().
                            log().all().
                            headers(headerMap).
                            body(caseInfo.getInputParams()).

                     when().
                            post(caseInfo.getUrl()).

                    then().
                            log().all().
                            extract().response();

            //将日志添加到Allure报告中
            addLogToAllure(logPath);
            //断言
            assertExpetced(caseInfo, res);
        }

    }

    @DataProvider
    public Object[] getData() {
        Object[] datas = caseInfoList.toArray();
        return datas;
    }



}
