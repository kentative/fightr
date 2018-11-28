package com.bytes.fightr.client.service.logger;

/**
 * Created by Kent on 4/12/2017.
 */

public class LoggerFactory {

    private static Logger logger = new AndroidLogger();

    /**
     * Lazy implementation of a logger matching server codes
     * @param aClass
     * @return
     */
    public static Logger getLogger(Class<?> aClass) {
        return logger;

    }

}
