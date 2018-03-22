package com.tbb.util;

/**
 * String处理工具类
 * 
 *
 */
public class StringUtil {
  public static String delBlank(String str) {
    return str == null ? null : str.trim();
  }

  public static boolean isEmpty(String str) {
    if (str == null || "".equals(str.trim())) {
      return true;
    }
    return false;
  }

  public static boolean isNotEmpty(String str) {
    return !isEmpty(str);
  }
}
