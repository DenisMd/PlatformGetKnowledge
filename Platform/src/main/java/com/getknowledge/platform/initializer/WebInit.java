package com.getknowledge.platform.initializer;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.web.WebApplicationInitializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.File;

public class WebInit implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        String log4jLocation = servletContext.getInitParameter("log4j-properties-location");
        if (log4jLocation == null) {
            System.err.println("*** No log4j-properties-location init param, so initializing log4j with BasicConfigurator");
            BasicConfigurator.configure();
        } else {
            String webAppPath = servletContext.getRealPath("/");
            String log4jProp = webAppPath + log4jLocation;
            File file = new File(log4jProp);
            if (file.exists()) {
                System.out.println("Initializing log4j with: " + log4jProp);
                PropertyConfigurator.configure(log4jProp);
            } else {
                System.err.println("*** " + log4jProp + " file not found, so initializing log4j with BasicConfigurator");
                BasicConfigurator.configure();
            }
        }
    }
}
