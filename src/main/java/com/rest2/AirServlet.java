package com.rest2;

import org.glassfish.jersey.servlet.ServletContainer;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

/**
 * Created by JackNiu on 2017/7/20.
 */
@WebServlet(
        initParams =@WebInitParam(name="jersey.config.server.provider.packages",value ="com.rest2"),
        urlPatterns = "/webapi/*",
        loadOnStartup = 1
)
public class AirServlet  extends ServletContainer{
}
