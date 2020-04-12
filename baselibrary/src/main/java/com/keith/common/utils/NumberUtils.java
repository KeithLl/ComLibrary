package com.keith.baselibrary.utils;

/**
 * Created by KeithLee on 17/01/06.
 * 将0~1000以内的数字转换为汉字
 */

public class NumberUtils {

    private static final String[] Unit = new String[]{"百", "十", ""};
    private static final String[] Numbers = new String[]{"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};

    public static String formatNumber(int num) {
        if (num >= 1000 || num < 0) {
            return "请输入0-999之间的数字";
        }
        //转化为三位数组
        int[] numberIndexes = new int[3];
        numberIndexes[0] = num % 1000 / 100;//百位
        numberIndexes[1] = num % 100 / 10;//十位
        numberIndexes[2] = num % 10 / 1;//个位

        //补充最后的
        StringBuffer stringBuffer = new StringBuffer();
        //遍历集合,根据具体情况添加单位
        for (int i = 0; i < numberIndexes.length; i++) {
            switch (numberIndexes[i]) {
                case 0:
                    //表示十位为0
                    if (i == 1) {
                        //百位和个位都不为0,exp:101
                        if (numberIndexes[2] != 0 && numberIndexes[0] != 0) {
                            stringBuffer.append(Numbers[0]);//拼接对应的数字:零
                        }
                    }
                    //表示个位为0
                    if (i == 2) {
                        //百位和十位都为0,exp:1,2
                        if (numberIndexes[0] == 0 && numberIndexes[1] == 0) {
                            stringBuffer.append(Numbers[0]);//拼接对应的数字:零
                        }
                    }
                    break;
                case 1:
                    //十位等于1且百位为0,则不拼接数字
                    if (!(numberIndexes[0] == 0 && i == 1)) {
                        stringBuffer.append(Numbers[numberIndexes[i]]);//拼接对应的数字:一
                    }
                    stringBuffer.append(Unit[i]);//不是个位拼接单位
                    break;
                default:
                    stringBuffer.append(Numbers[numberIndexes[i]]);//拼接对应的数字
                    stringBuffer.append(Unit[i]);//拼接单位
                    break;
            }
        }
        return stringBuffer.toString();
    }
}
