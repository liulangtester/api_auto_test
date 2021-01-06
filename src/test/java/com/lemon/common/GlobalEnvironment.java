package com.lemon.common;

import java.util.HashMap;
import java.util.Map;

public class GlobalEnvironment {
    //public static Integer memberId = 0;

    //优化设计--使用Map保存环境变量
    public static Map<String, Object> envData = new HashMap<String, Object>();


}
