package com.felixpageau.roboboat.mission2014;

//import org.glassfish.jersey.jackson.JacksonFeature;
import java.net.MalformedURLException;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.internal.scanning.PackageNamesScanner;

import com.felixpageau.roboboat.mission2014.resources.AutomatedDockingResource;
import com.felixpageau.roboboat.mission2014.resources.LightSequenceResource;
import com.felixpageau.roboboat.mission2014.resources.ManageCompetition;
import com.felixpageau.roboboat.mission2014.resources.ObstacleAvoidanceResource;
import com.felixpageau.roboboat.mission2014.resources.PingerResource;
import com.felixpageau.roboboat.mission2014.server.Competition;

/**
 * Sets the resource configuration for the web-application to use Jackson for marshalling/unmarshalling
 */
public class Mission2014ResourceConfig extends ResourceConfig {
    public Mission2014ResourceConfig() throws MalformedURLException {
        super(JacksonObjectMapperProvider.class/*,
                JacksonFeature.class*/);
        
        Competition competition = new Competition();
        this.registerFinder(new PackageNamesScanner(new String[]{"com.felixpageau.roboboat.mission2014.resources", "com.fasterxml.jackson.jaxrs.base"}, false));
        this.register(new AutomatedDockingResource(competition));
        this.register(new LightSequenceResource(competition));
        this.register(new ManageCompetition(competition));
        this.register(new ObstacleAvoidanceResource(competition));
        this.register(new PingerResource(competition));
    }
    
    @Override
    public String toString() {
        System.out.println("Packages:");
        for (Class c : getConfiguration().getClasses()) {
            System.out.println(c.toString());
        }
        return super.toString();
    }
}
