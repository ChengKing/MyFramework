package com.common.util;

import java.util.Enumeration;

import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;

/**
 * Log4J Util Class. <BR>
 * 
 */
public class Log4JUtil {
    
    @SuppressWarnings("rawtypes")
	public static String getLoggerDetail(Logger logger) {
        StringBuffer sb = new StringBuffer(256);
        sb.append("[Logger]Name:").append(logger.getName());
        sb.append("; Level:").append(logger.getLevel());
        
        for (Enumeration enu = logger.getAllAppenders(); enu.hasMoreElements(); ) {
            sb.append(getAppenderDetail((Appender) enu.nextElement()));
        }

        sb.append("[user.dir]" + System.getProperty("user.dir"));
        return sb.toString();
    }

    public static String getAppenderDetail(Appender appender) {
        StringBuffer sb = new StringBuffer(256);
        sb.append("[Appender]Name:").append(appender.getName());
        sb.append("; Layout:").append(appender.getLayout());
        if (appender instanceof FileAppender) {
            FileAppender fileAppender = (FileAppender) appender;
            sb.append("[File]").append(fileAppender.getFile());
        }
        return sb.toString();
    }
}