package com.ying.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * 汉字转拼音
 * Created by lyz on 2017/6/22.
 */
public class PinYinUtil {

    /**
     * 汉字转换为汉语拼音，英文字符不变，去掉空格(去掉特殊字符)
     *
     * @param chines 汉字
     * @return 拼音
     */
    public static String converterToSpell(String chines) {
        StringBuilder sb = new StringBuilder();
        //转化为字符
        char[] nameChar = chines.toCharArray();
        //汉语拼音格式输出类
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        //输出设置,大小写,音标方式等
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        defaultFormat.setVCharType(HanyuPinyinVCharType.WITH_V);//用v表示ü
        String[] arr;
        for (int i = 0; i < nameChar.length; i++) {
            if (nameChar[i] > 128) {
                //如果是中文
                try {
                    arr = PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat);
                    if (arr.length > 0) {
                        sb.append(arr[0]);
                    } else {
                        sb.append(nameChar[i]);
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else if (
                    (nameChar[i] >= 48 && nameChar[i] <= 57)
                            || (nameChar[i] >= 65 && nameChar[i] <= 90)
                            || (nameChar[i] >= 97 && nameChar[i] <= 122)
                    ) {
                //数字或者大写字母或者小写字母
                sb.append(nameChar[i]);
            } else {
                //其他字符或者空格直接清空
            }
        }
        return sb.toString();
    }

    /**
     * 获取汉字首字母
     *
     * @param chines
     * @return
     */
    public static String converterToInitials(String chines) {
        StringBuilder sb = new StringBuilder();
        //转化为字符
        char[] nameChar = chines.toCharArray();
        //汉语拼音格式输出类
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        //输出设置,大小写,音标方式等
        defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        String[] arr;
        for (int i = 0; i < nameChar.length; i++) {
            //如果是中文
            if (nameChar[i] > 128) {
                try {
                    arr = PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat);
                    if (arr.length > 0) {
                        sb.append(arr[0].substring(0, 1));
                    } else {
                        sb.append(nameChar[i]);
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else if (
                    (nameChar[i] >= 48 && nameChar[i] <= 57)
                            || (nameChar[i] >= 65 && nameChar[i] <= 90)
                            || (nameChar[i] >= 97 && nameChar[i] <= 122)
                    ) {
                //数字或者大写字母或者小写字母
                sb.append(nameChar[i]);
            } else {
                //其他字符或者空格直接清空
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(converterToSpell("请假 !#$%流程_2ksdAS"));
        System.out.println(converterToSpell("重 ()（）点"));
//        System.out.println(converterToInitials("重复"));
    }
}
