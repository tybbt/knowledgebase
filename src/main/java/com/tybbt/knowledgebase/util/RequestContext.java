package com.tybbt.knowledgebase.util;

import java.io.Serializable;

public class RequestContext implements Serializable {
    // 本地变量，仅允许当前线程中能进行取值赋值的变量。
    private static ThreadLocal<String> remoteAddr = new ThreadLocal<>();

    public static String getRemoteAddr() {
        return remoteAddr.get();
    }

    public static void setRemoteAddr(String remoteAddr) {
        RequestContext.remoteAddr.set(remoteAddr);
    }
}
