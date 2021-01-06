package com.lemon.util;

import java.util.Random;


public class PhoneRandom {
    public static void main(String[] args) {
        System.out.println(getPhone());
    }

    /**
     * 随机生成一个手机号码
     * @return
     */
    public static String getRandomPhone() {
        //定义手机号的号段
        String phone = "134";
        //循环8次，把每一次随机生成的0~9的整数拼接上
        for (int i = 0; i < 8; i++) {
            //随机生成一个0~9的整数
            Random random = new Random();
            int num = random.nextInt(9);
            phone += num;
        }
        return phone;
    }

    /**
     * 获取一个未注册过的手机号码
     * @return
     */
    public static String getPhone() {
        while (true) {
            String phone = getRandomPhone();
            Object result = JDBCUtils.querySingle("select count(*) from member where mobile_phone=" + phone);
            if ((Long) result != 0) {
                System.out.println("手机号码被注册过");
            } else {
                return phone;
            }
        }
    }
}
