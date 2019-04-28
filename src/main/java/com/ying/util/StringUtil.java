package com.ying.util;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by lyz on 2017/7/5.
 */
public class StringUtil {

    /**
     * 不为空，且不为null字符串
     *
     * @param value
     * @return
     */
    public static boolean isNotEmptyStr(String value) {
        return !StringUtils.isEmpty(value) && !value.equals("null");
    }

    /**
     * list转化为逗号间隔的String
     *
     * @param lists
     * @return
     */
    public static String list2Str(List<String> lists) {
        if (lists != null && lists.size() > 0) {
            StringBuffer sb = new StringBuffer();
            for (String string : lists) {
                if (string != null && !"".equals(string)) {
                    sb.append(",");
                    sb.append(string);
                }
            }
            if (sb != null && sb.length() > 0) {
                return sb.deleteCharAt(0).toString();
            }
        }
        return null;
    }

    /**
     * long数组转化为字符串
     *
     * @param userIds
     * @return
     */
    public static String array2Str(Long[] userIds) {
        if (userIds != null && userIds.length > 0) {
            StringBuffer sb = new StringBuffer();
            for (Long l : userIds) {
                if (l != null) {
                    sb.append(",");
                    sb.append(String.valueOf(l));
                }
            }
            if (sb != null && sb.length() > 0) {
                return sb.deleteCharAt(0).toString();
            }
        }
        return null;
    }

    /**
     * 逗号间隔的string转化为list
     *
     * @param str
     * @return
     */
    public static List<String> str2List(String str) {
        if (!Strings.isNullOrEmpty(str)) {
            return Arrays.asList(str.split(","));
        } else {
            return null;
        }
    }

    /**
     * list去重
     *
     * @param list1
     * @return
     */
    public static List<String> listDistinct(List<String> list1) {
        if (null != list1 && list1.size() > 0) {
            return list1.stream().distinct().collect(Collectors.toList());
        } else {
            return null;
        }
    }

    /**
     * 逗号间隔str去重
     *
     * @param str
     * @return
     */
    public static String strDistinct(String str) {
        List<String> assList = StringUtil.listDistinct(StringUtil.str2List(str));
        return StringUtil.list2Str(assList);
    }

    /**
     * list转set
     *
     * @param list
     * @return
     */
    public static Set<String> list2Set(List<String> list) {
        if (null != list && list.size() > 0) {
            Set<String> set = Sets.newHashSet();
            set.addAll(list);
            return set;
        } else {
            return null;
        }
    }

    /**
     * 逗号间隔的string转化为list，忽略大小写
     *
     * @param str
     * @return
     */
    public static List<String> str2ListIgnore(String str) {
        if (!Strings.isNullOrEmpty(str)) {
            return Arrays.asList(str.toLowerCase().split(","));
        } else {
            return null;
        }
    }

    /**
     * list比较，返回后者比前者多的列表
     *
     * @param beforeList
     * @param afterList
     * @return
     */
    public static List<String> listCompare(List<String> beforeList, List<String> afterList) {
        if (beforeList == null || beforeList.size() == 0) {
            return afterList;
        }
        if (!beforeList.containsAll(afterList) && afterList.containsAll(beforeList)) {
            List<String> result = Lists.newArrayList();
            for (String param : afterList) {
                if (!beforeList.contains(param)) {
                    result.add(param);
                }
            }
            return result;
        }
        return null;
    }

    /**
     * 截取%{}%中含字符串
     *
     * @param context
     * @return
     */
    public static String getParamByContext(String context) {
        if (!Strings.isNullOrEmpty(context)) {
            if (context.indexOf("%{") > -1 && context.indexOf("}%") > context.indexOf("%{")) {
                return context.substring(context.indexOf("%{") + 2, context.indexOf("}%"));
            }
        }
        return null;
    }

    /**
     * 后者过滤掉前者中已存在的部分
     *
     * @param ass
     * @param users
     * @return
     */
    public static String strCompare(String ass, String users) {
        if (Strings.isNullOrEmpty(users)) {
            return null;
        }
        List<String> userList = str2List(users);
        List<String> assList = str2List(ass);
        if (assList == null || assList.size() == 0) {
            return list2Str(userList);
        }
        List<String> resultList = Lists.newArrayList();
        for (String u : userList) {
            if (!assList.contains(u)) {
                resultList.add(u);
            }
        }
        return list2Str(resultList);
    }

    /**
     * 字符串是否为日期格式
     *
     * @param strDate
     * @return
     */
    public static boolean isDate(String strDate) {
        if (strDate == null) {
            return false;
        }
        //        yyyy-MM-dd HH:mm:ss
        //        yyyy-MM-dd HH:mm
        //        yyyy-MM-dd
        //        yyyy-MM
        //        HH:mm:ss
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat dateFormat3 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateFormat4 = new SimpleDateFormat("yyyy-MM");
        SimpleDateFormat dateFormat5 = new SimpleDateFormat("HH:mm:ss");
        try {
            // 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2004/02/29会被接受，并转换成2004/03/01
            dateFormat1.setLenient(false);
            dateFormat1.parse(strDate);
            return true;
        } catch (Exception e1) {
            // 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
            try {
                dateFormat2.setLenient(false);
                dateFormat2.parse(strDate);
                return true;
            } catch (ParseException e2) {
                try {
                    dateFormat3.setLenient(false);
                    dateFormat3.parse(strDate);
                    return true;
                } catch (ParseException e3) {
                    try {
                        dateFormat4.setLenient(false);
                        dateFormat4.parse(strDate);
                        return true;
                    } catch (ParseException e4) {
                        try {
                            dateFormat5.setLenient(false);
                            dateFormat5.parse(strDate);
                            return true;
                        } catch (ParseException e5) {
                            return false;
                        }
                    }
                }
            }
        }
    }

    /**
     * 字符串转化为日期格式
     *
     * @param strDate
     * @return
     */
    public static Date toDate(String strDate) {
        //        yyyy-MM-dd HH:mm:ss
        //        yyyy-MM-dd HH:mm
        //        yyyy-MM-dd
        //        yyyy-MM
        //        HH:mm:ss
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat dateFormat3 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateFormat4 = new SimpleDateFormat("yyyy-MM");
        SimpleDateFormat dateFormat5 = new SimpleDateFormat("HH:mm:ss");
        try {
            // 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2004/02/29会被接受，并转换成2004/03/01
            dateFormat1.setLenient(false);
            return dateFormat1.parse(strDate);
        } catch (Exception e1) {
            // 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
            try {
                dateFormat2.setLenient(false);
                return dateFormat2.parse(strDate);
            } catch (ParseException e2) {
                try {
                    dateFormat3.setLenient(false);
                    return dateFormat3.parse(strDate);
                } catch (ParseException e3) {
                    try {
                        dateFormat4.setLenient(false);
                        return dateFormat4.parse(strDate);
                    } catch (ParseException e4) {
                        try {
                            dateFormat5.setLenient(false);
                            return dateFormat5.parse(strDate);
                        } catch (ParseException e5) {
                            return null;
                        }
                    }
                }
            }
        }
    }

    /**
     * 字符串是否为数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        Pattern pattern = Pattern.compile("^((-)?(([0-9]+.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*.[0-9]+)|([0-9]*[1-9][0-9]*)))$");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    /**
     * 根据数据库字段类型获取字段长度
     *
     * @param type
     * @return
     */
    public static int getColumnLen(String type) {
        if (StringUtil.isNotEmptyStr(type) && type.indexOf("(") > -1 && type.indexOf(")") > -1 && type.indexOf(")") > type.indexOf("(")) {
            try {
                String len = type.substring(type.indexOf("(") + 1, type.indexOf(")"));
                return Integer.parseInt(len);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    public static void main(String[] args) {
//        List l = Lists.newArrayList();
//        l.add("1");
//        l.add("1");
//        l.add("2");
//        l.add("3");
//        System.out.println(listDistinct(l));
//        System.out.println(list2Set(l));
//        System.out.println(str2ListIgnore(null));
//        System.out.println(isDate("2018-08-08"));
//        System.out.println(isDate("2018-08"));
//        System.out.println(isDate("2018-08-08 12:12"));
//        System.out.println(isDate("2018-08-08 12:12:09"));
//        System.out.println(isDate("2018-08-08 12"));
//        System.out.println(isDate("2018"));

//        System.out.println(isNumeric("2"));
//        System.out.println(isNumeric("-2"));
//        System.out.println(isNumeric("2.233"));
//        System.out.println(isNumeric("-2.22"));
//        System.out.println(isNumeric("1s"));
//        System.out.println(getColumnLen("varchar(20)"));
        System.out.println(toDate("14:59:32"));
        System.out.println(toDate("16:51:51"));
        System.out.println(toDate("15:23:28").after(toDate("15:15:15")));
    }
}
