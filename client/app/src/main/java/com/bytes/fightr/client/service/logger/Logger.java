package com.bytes.fightr.client.service.logger;

/**
 * Created by Kent on 10/14/2016.
 */

public interface Logger {

    void debug(String tag, String message);
    void info(String tag, String message);
    void error(String tag, String message, Throwable t);

    void debug(String message);
    void info(String message);
    void warn(String message);
    void error(String message);
    void error(String message, Throwable t);


}
