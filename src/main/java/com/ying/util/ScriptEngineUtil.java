package com.ying.util;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * 双重校验锁单例模式，获取JavaScript引擎
 * Created by lyz on 2018/8/15.
 */
public class ScriptEngineUtil {

    private volatile static ScriptEngine jse;

    private ScriptEngineUtil() {
    }

    public static ScriptEngine getInstance() {
        if (jse == null) {
            synchronized (ScriptEngineUtil.class) {
                if (jse == null) {
                    jse = new ScriptEngineManager().getEngineByName("JavaScript");
                }
            }
        }
        return jse;
    }
}
