package com.felixpageau.roboboat.mission2015;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.stream.Collectors;

import javax.servlet.DispatcherType;
import javax.ws.rs.core.UriBuilder;

import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.WebAppContext;
import org.glassfish.jersey.jetty.JettyHttpContainer;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ContainerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.thetransactioncompany.cors.CORSFilter;

public class App {

  public static void main(String[] args) throws Exception {
    URI baseUri = UriBuilder.fromUri("https://localhost/").port(9443).build();

    // JSON/HTTP endpoint
    JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
    ObjectMapper mapper = new ObjectMapper();
    provider.setMapper(mapper);
    SslContextFactory sslContextFactory = new SslContextFactory("src/main/resources/keystore");
    sslContextFactory.setKeyStorePassword("qwerty123");
    CompetitionResourceConfig config = createApp();
    System.out.println(config.toString());

    // NMEA Server
    final NMEAServer nmeaServer = new NMEAServer(config.getCompetition(), 9999, SentenceRegistryFactory.create2015NMEASentenceRegistry());

    // HTTP/JSON server
    JettyHttpContainer container = ContainerFactory.createContainer(JettyHttpContainer.class, config);
    Server server = JettyHttpContainerFactory.createServer(baseUri, sslContextFactory, container, false);
    ServerConnector http = new ServerConnector(server, new HttpConnectionFactory(new HttpConfiguration()));
    http.setPort(9000);
    server.addConnector(http);
    WebAppContext wac = new WebAppContext();
    HashLoginService myrealm = new HashLoginService("RoboBoat");
    myrealm.setConfig("src/main/resources/realm.properties");
    wac.getSecurityHandler().setLoginService(myrealm);
    wac.setResourceBase(new File("src/main/webapp/").getAbsolutePath());
    wac.setDescriptor(new File("src/main/webapp/WEB-INF/web.xml").getAbsolutePath());
    wac.setContextPath("/");
    wac.setParentLoaderPriority(true);
    wac.addFilter(CORSFilter.class, "/*", EnumSet.of(DispatcherType.INCLUDE, DispatcherType.REQUEST));
    server.setHandler(wac);
    server.addLifeCycleListener(new JettyLifecycleAdapter() {
      @Override
      public void lifeCycleFailure(LifeCycle event, Throwable cause) {
        nmeaServer.stop();
      }

      @Override
      public void lifeCycleStopping(LifeCycle event) {
        nmeaServer.stop();
      }

      @Override
      public void lifeCycleStopped(LifeCycle event) {
        nmeaServer.stop();
      }
    });

    // starting
    nmeaServer.start();
    server.start();
    System.out.println("\n\nJSON/HTTP Server started on: "
        + Arrays.stream(server.getConnectors())
            .map(c -> String.format("%s:%d", c.getProtocols().stream().findFirst().orElse(""), c.getServer().getURI().getPort()))
            .collect(Collectors.joining(", ")));
  }

  public static CompetitionResourceConfig createApp() throws MalformedURLException, URISyntaxException {
    return new Mission2015ResourceConfig();
  }

  private static class JettyLifecycleAdapter implements LifeCycle.Listener {
    @Override
    public void lifeCycleStarting(LifeCycle event) {
      // EMPTY
    }

    @Override
    public void lifeCycleStarted(LifeCycle event) {
      // EMPTY
    }

    @Override
    public void lifeCycleFailure(LifeCycle event, Throwable cause) {
      // EMPTY
    }

    @Override
    public void lifeCycleStopping(LifeCycle event) {
      // EMPTY
    }

    @Override
    public void lifeCycleStopped(LifeCycle event) {
      // EMPTY
    }
  }
}
