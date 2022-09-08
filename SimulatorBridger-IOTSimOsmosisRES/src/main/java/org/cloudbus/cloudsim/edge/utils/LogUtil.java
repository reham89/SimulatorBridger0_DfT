package org.cloudbus.cloudsim.edge.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogUtil {
	public static Logger logger = LogManager.getRootLogger();
	public static void info(String msg) {
		logger.info(msg);
	}
}
