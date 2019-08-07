package com.sunred.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MyLogger {

    public static void main(String[] args) {
        Log log = LogFactory.getLog(MyLogger.class);
        log.info("ddd{}");
    }
}
