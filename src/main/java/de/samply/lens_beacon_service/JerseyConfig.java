package de.samply.lens_beacon_service;

import de.samply.lens_beacon_service.lens.Api;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

/*
https://stackoverflow.com/questions/72430298/jax-rs-annotation-path-is-not-work-in-java-spring-boot

 */
@Component
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig(){
        register(Api.class);
    }
}
