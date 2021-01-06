package com.lemon.base;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lemon.common.Constants;
import com.lemon.common.GlobalEnvironment;
import com.lemon.pojo.CaseInfo;
import com.lemon.util.JDBCUtils;
import io.qameta.allure.Allure;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.testng.Assert;
import io.restassured.response.Response;
import org.testng.annotations.BeforeTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;

/**
 * 所有测试用例类的父类
 */
public class BaseCase {

    @BeforeTest
    public void globalSetup() throws FileNotFoundException {
        //整体性前置配置
        //1.配置BaseUrl(会自动拼接到请求的URL前面)
//        RestAssured.baseURI = "http://api.lemonban.com/futureloan";
        //2.配置接口响应结果如果JSON返回的数据类型是Float或Double类型，使用BigDecimal类型来接收
        RestAssured.config = RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL));
//        //3.配置所有日志都输出到一个指定文件
//        PrintStream fileOutPutStream = new PrintStream(new File("log/test_all.log"));
//        RestAssured.filters(new RequestLoggingFilter(fileOutPutStream),new ResponseLoggingFilter(fileOutPutStream));
    }

    /**
     * 将日志添加到Allure报告中
     * @param logPath 日志文件路径
     */
    public void addLogToAllure(String logPath) {
        try {
            Allure.addAttachment("接口请求响应信息", new FileInputStream(logPath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将日志重定向输出到单独的文件中
     * 通过Constants.IS_DEBUG控制日志输出到控制台或文件
     * @param caseInfo
     */
    public String addLogToFile(CaseInfo caseInfo) {
        String logFilePath = "";

        //IS_DEBUG=true，输出到控制台    IS_DEBUG=false，输出到文件
        if (!Constants.IS_DEBUG) {
            //判断文件夹是否存在及创建  如target/log/register
            String dirPath = "target/log/" + caseInfo.getInterfaceName();
            File dirFile = new File(dirPath);
            //不存在就创建
            if (!dirFile.isDirectory()) {
                //创建目录
                dirFile.mkdirs();
            }
            logFilePath = dirPath + "/" + caseInfo.getInterfaceName() + "_" + caseInfo.getCaseId() + ".log";
            //请求之前配置日志，输出到指定文件(将日志输出路径重定向，不会再输出到控制台)
            PrintStream fileOutPutStream = null;
            try {
                //日志文件如：target/log/register/register_1.log
                fileOutPutStream = new PrintStream(new File(logFilePath));
                RestAssured.config = RestAssured.config().logConfig(LogConfig.logConfig().defaultStream(fileOutPutStream));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return logFilePath;
    }

    /**
     * 用例公共的断言方法 - 断言期望值和实际值
     * @param caseInfo 用例信息
     * @param res 接口响应信息
     */
    public void assertExpetced(CaseInfo caseInfo, Response res) {
        //断言
        //把断言数据转换为Map
        ObjectMapper objectMapper2 = new ObjectMapper();
        Map expectedMap = null;
        try {
            expectedMap = objectMapper2.readValue(caseInfo.getExpected(), Map.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        //遍历map集合
        Set<Map.Entry<String, Object>> set = expectedMap.entrySet();
        for (Map.Entry<String, Object> map: set) {
            //实际结果 与 期望结果 比较

            //Rest-assured的Gpath表达式获取的小数会使用float接收，会丢失精度
            //解决办法：
            //1.让Gpath使用BigDecimal类型接收数据(
            //  即在用例的Response中添加以下代码：让REST-Assured返回json小数的时候，使用BigDecimal类型来存储小数（默认是Float存储的）
            //  config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL))).   )
            //2.让期望值也转成BigDecimall类型

            //期望值
            Object expected = map.getValue();
            //把期望值转成BigDecimal类型（Float/Double类型才要转）
            if (expected instanceof Float || (expected instanceof Double)) {
                BigDecimal bigDecimalData = new BigDecimal(map.getValue().toString());
                Assert.assertEquals(res.path(map.getKey()),bigDecimalData,"接口响应断言失败");
            } else {
                Assert.assertEquals(res.path(map.getKey()),expected,"接口响应断言失败");
            }
        }
    }

    /**
     * 用例公共的断言方法 - 断言数据库
     * @param caseInfo
     */
    public void assertSQL(CaseInfo caseInfo) {
        String checkSQL = caseInfo.getCheckSQL();
        if (checkSQL != null) {
            //Json转Map
            Map checkSQLMap = fromJsonToMap(checkSQL);
            //Map转成entrySet，才能使用getKey()获取key 和 使用getValue()获取value
            Set<Map.Entry<String, Object>> set = checkSQLMap.entrySet();
            for (Map.Entry<String, Object> mapEntry : set) {
                String sql = mapEntry.getKey();
                //查询数据库
                Object actual = JDBCUtils.querySingle(sql);
                System.out.println("转类型前：");
                System.out.println("数据库返回类型：：" + actual.getClass());
                System.out.println("预期结果类型：：" + caseInfo.getExpected().getClass());

                //数据库返回字段是Long类型
                if (actual instanceof Long) {
                    //把expected转成Long
                    Long expected = new Long(mapEntry.getValue().toString());
                    System.out.println("-----数据库返回Long类型");
                    System.out.println("转类型后：");
                    System.out.println("actual：：" + actual.getClass());
                    System.out.println("expected：：" + expected.getClass());
                    //断言需要类型一致才能通过
                    Assert.assertEquals(actual, expected,"数据库断言失败");
                } else if (actual instanceof BigDecimal) {
                    //数据库返回字段是BigDecimal类型
                    //把expected转成BigDecimal
                    BigDecimal expected = new BigDecimal(mapEntry.getValue().toString());
                    System.out.println("-----数据库返回BigDecimal类型");
                    System.out.println("转类型后：");
                    System.out.println("actual：：" + actual.getClass());
                    System.out.println("expected：：" + expected.getClass());
                    Assert.assertEquals(actual, expected,"数据库断言失败");
                }else{
                    //数据库返回字段是String类型
                    System.out.println("字符串类型断言：");
                    Assert.assertEquals(actual, mapEntry.getValue(),"数据库断言失败");
                }
            }
        }
    }

    /**
     * 对所有用例进行参数化替换
     * @param caseInfoList 当前测试类中所有的测试用例数据
     * @return  参数化替换之后的测试用例数据
     */
    public List<CaseInfo> paramsReplace(List<CaseInfo> caseInfoList) {
        //对用例中的四个地方做参数化处理：请求头、接口地址、输入参数、期望响应结果、数据库校验
        for (CaseInfo caseInfo : caseInfoList) {

            String requestHeader = regexReplace(caseInfo.getRequestHeader());
            caseInfo.setRequestHeader(requestHeader);

            String url = regexReplace(caseInfo.getUrl());
            caseInfo.setUrl(url);

            String inputParams = regexReplace(caseInfo.getInputParams());
            caseInfo.setInputParams(inputParams);

            String expected = regexReplace(caseInfo.getExpected());
            caseInfo.setExpected(expected);

            String checkSQL = regexReplace(caseInfo.getCheckSQL());
            caseInfo.setCheckSQL(checkSQL);

        }
        return caseInfoList;
    }

    /**
     * 对一条用例进行参数化替换
     * @param caseInfo
     * @return
     */
    public CaseInfo paramsReplaceCase(CaseInfo caseInfo) {
        //对用例中的四个地方做参数化处理：请求头、接口地址、输入参数、期望响应结果、数据库校验
        String requestHeader = regexReplace(caseInfo.getRequestHeader());
        caseInfo.setRequestHeader(requestHeader);

        String url = regexReplace(caseInfo.getUrl());
        caseInfo.setUrl(url);

        String inputParams = regexReplace(caseInfo.getInputParams());
        caseInfo.setInputParams(inputParams);

        String expected = regexReplace(caseInfo.getExpected());
        caseInfo.setExpected(expected);

        String checkSQL = regexReplace(caseInfo.getCheckSQL());
        caseInfo.setCheckSQL(checkSQL);
        return caseInfo;
    }

    /**
     * 根据正则替换字符串中的内容
     * @param sourceStr 需要匹配的原始字符串
     * @return
     */
    public String regexReplace(String sourceStr) {
        //对excel进行判空处理，如果是空的直接返回
        if (sourceStr == null) {
            return sourceStr;
        }

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

            //获取环境变量里的值
            Object replaceStr = GlobalEnvironment.envData.get(singleStr);
//            System.out.println("singleStr：：" + singleStr);
//            System.out.println("替换进去的值：："+replaceStr);
            //替换匹配到的字符串 findStr：被替换字符串  replaceStr：替换进去的字符串
            sourceStr = sourceStr.replace(findStr, replaceStr+"");
            // 为什么要再给sourceStr赋值？
            // 替换前：{"mobile_phone": "{{mobile_phone}}","pwd":"{{pwd}}"}
            // 第一次替换后：{"mobile_phone": "13433631319","pwd":"{{pwd}}"}
            // 第二次替换后：{"mobile_phone": "13433631319","pwd":"12345678"}
//            System.out.println(sourceStr);

        }
//        System.out.println(sourceStr);
        //如果没有匹配到，返回原来的字符串
        return sourceStr;
    }

    /**
     * 从Excel读取用例数据
     * @param index sheet的索引，从0开始
     * @return
     */
    public List<CaseInfo> getCaseDataFromExcel(int index) {
        //使用EasyPOI读取Excel数据
        ImportParams importParams = new ImportParams();
        //设置读取Excel起始sheet值
        importParams.setStartSheetIndex(index);
        File excelFile = new File(Constants.EXCEL_PATH);
        List<CaseInfo> caseInfoList = ExcelImportUtil.importExcel(excelFile, CaseInfo.class, importParams);
        return caseInfoList;
    }

    /**
     * 把Json字符串转成Map
     * @param jsonStr
     * @return
     */
    public Map fromJsonToMap(String jsonStr) {
        //JSON转Map 需要fastjson依赖 推荐用
        return JSONObject.parseObject(jsonStr, Map.class);
    }
}
