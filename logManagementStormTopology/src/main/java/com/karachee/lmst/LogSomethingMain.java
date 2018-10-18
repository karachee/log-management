package com.karachee.lmst;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Just gives use something to consume
 */
public class LogSomethingMain {
    static Logger logger = LogManager.getLogger(LogSomethingMain.class);

    public static void main(String[] args) {
        //logger.debug("test");

        try {
            Integer.parseInt("Not an integer");
        } catch (Exception e) {
            logger.error("Unable to convert to String to Integer, ", e);
        }

    }
}
