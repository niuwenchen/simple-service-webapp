package com.rest3;

import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

/**
 * Created by JackNiu on 2017/7/20.
 */
@ApplicationPath("/webapi/*")
public class AirResourceConfig extends ResourceConfig {
    public AirResourceConfig(){
        packages("com.rest3");
    }
}
