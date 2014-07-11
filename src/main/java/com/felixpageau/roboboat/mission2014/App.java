package com.felixpageau.roboboat.mission2014;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.ws.rs.core.UriBuilder;

import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.WebAppContext;
import org.glassfish.jersey.jetty.JettyHttpContainer;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.thetransactioncompany.cors.CORSFilter;

public class App {

    public static void main(String[] args) throws Exception {
        URI baseUri = UriBuilder.fromUri("https://localhost/").port(9443).build();
        
        // create JsonProvider to provide custom ObjectMapper
        JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
        ObjectMapper mapper = new ObjectMapper();
        provider.setMapper(mapper);
        
        SslContextFactory sslContextFactory = new SslContextFactory("src/main/resources/keystore");
        sslContextFactory.setKeyStorePassword("qwerty123");
        
        ResourceConfig config = createApp();
        JettyHttpContainer container = ContainerFactory.createContainer(JettyHttpContainer.class, config);
        Server server = JettyHttpContainerFactory.createServer(baseUri, sslContextFactory, container, false);
        
        WebAppContext wac = new WebAppContext();
        wac.setResourceBase(new File("src/main/webapp/").getAbsolutePath());
        wac.setDescriptor(new File("src/main/webapp/WEB-INF/web.xml").getAbsolutePath());
        wac.setContextPath("/");
        wac.setParentLoaderPriority(true);
        wac.addFilter(CORSFilter.class, "/*", EnumSet.of(DispatcherType.INCLUDE,DispatcherType.REQUEST));
        
        
        HashLoginService myrealm = new HashLoginService("RoboBoat");
        myrealm.setConfig("src/main/resources/realm.properties");
        wac.getSecurityHandler().setLoginService(myrealm);
        
        
        server.setHandler(wac);

        server.start();
        System.out.println(config.toString());
        System.out.println("\n\nServer:" + server.dump());
    }

    public static ResourceConfig createApp() throws MalformedURLException {
        return new Mission2014ResourceConfig();
    }
}
