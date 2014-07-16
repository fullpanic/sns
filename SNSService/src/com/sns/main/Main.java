package com.sns.main;

import org.apache.commons.io.IOUtils;
import org.restlet.Component;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sns.resource.ErrorStatus;
import com.sns.utils.ConnectionPool;

/**
 * restlet server
 * @author fullpanic
 *
 */
public class Main {
    
    private static Logger logger = null;
    
    private static final String CONF_PATH = "/Component.xml";
    
    private static final String DB_CONFIG = "/Datasource.properties";
    
    /**
     * init args
     */
    private static void init(String[] args) {
        //system logging
        System.setProperty("org.restlet.engine.loggerFacadeClass", "org.restlet.ext.slf4j.Slf4jLoggerFacade");
        System.setProperty("log4j.configuration", "log4j.properties");
        logger = LoggerFactory.getLogger(Main.class);
        //init db
        ConnectionPool.init(DB_CONFIG);
    }
    
    /**
     * start server
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args)
        throws Exception {
        init(args);
        logger.info("start run reslet server....");
        String text = IOUtils.toString(System.class.getResourceAsStream(CONF_PATH));
        Representation xml = new StringRepresentation(text);
        Component comp = new Component(xml);
        comp.setStatusService(new ErrorStatus());
        comp.start();
    }
}
