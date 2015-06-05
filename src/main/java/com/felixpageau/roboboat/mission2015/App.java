package com.felixpageau.roboboat.mission2015;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
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
import com.felixpageau.roboboat.mission2015.nmea.EnumField;
import com.felixpageau.roboboat.mission2015.nmea.Field;
import com.felixpageau.roboboat.mission2015.nmea.RegexField;
import com.felixpageau.roboboat.mission2015.nmea.SentenceDefinition;
import com.felixpageau.roboboat.mission2015.nmea.SentenceRegistry;
import com.felixpageau.roboboat.mission2015.structures.BuoyColor;
import com.felixpageau.roboboat.mission2015.structures.Challenge;
import com.felixpageau.roboboat.mission2015.structures.Course;
import com.felixpageau.roboboat.mission2015.structures.Symbol;
import com.felixpageau.roboboat.mission2015.structures.SymbolColor;
import com.google.common.collect.ImmutableList;
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
    Mission2015ResourceConfig config = createApp();
    System.out.println(config.toString());

    // NMEA Server
    final NMEAServer nmeaServer = new NMEAServer(config.getCompetition(), 9999, create2015NMEASentenceRegistry());

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

  public static Mission2015ResourceConfig createApp() throws MalformedURLException, URISyntaxException {
    return new Mission2015ResourceConfig();
  }

  public static SentenceRegistry create2015NMEASentenceRegistry() {
    Field course = new EnumField("Course", Arrays.asList(Course.values()).stream().map(c -> c.name()).collect(Collectors.toList()));
    Field teamCode = new RegexField("Teamcode", "[a-zA-Z]{2,5}");
    Field timestamp = new RegexField("Timestamp", "[0-9]{14}");
    Field status = new EnumField("Status", Arrays.asList("true", "false"));
    Field entrance = new EnumField("Entrance", Arrays.asList("1", "2", "3"));
    Field exit = new EnumField("Exit", Arrays.asList("X", "Y", "Z"));
    Field symbol = new EnumField("Symbol", Arrays.asList(Symbol.values()).stream().map(s -> s.name()).collect(Collectors.toList()));
    Field symbolColor = new EnumField("Color", Arrays.asList(SymbolColor.values()).stream().map(s -> s.name()).collect(Collectors.toList()));
    Field buoyColor = new EnumField("BuoyColor", Arrays.asList(BuoyColor.values()).stream().map(s -> s.name()).collect(Collectors.toList()));
    Field shape = new RegexField("Shape", "[0-9a-fA-F]");
    Field imageId = new RegexField("ImageID", "[a-zA-Z0-9\\-]+");
    Field challenge = new EnumField("Challenge", Arrays.asList(Challenge.values()).stream().map(c -> c.toString()).collect(Collectors.toList()));
    Field latitude = new RegexField("Latitude", "\\-?[0-9]{1,2}\\.[0-9]{6,10}");
    Field longitude = new RegexField("Longitude", "\\-?[0-9]{1,3}\\.[0-9]{6,10}");

    List<SentenceDefinition> sentences = ImmutableList.of(SentenceDefinition.create("SV", "OBS", "Request Obstacle Avoidance code",
        ImmutableList.of(course, teamCode), "$SVOBS,courseA,AUVSI*5F"), SentenceDefinition.create("TD", "OBS", "Provide Obstacle Avoidance code",
        ImmutableList.of(timestamp, entrance, exit), "$TDOBS,20150306061030,2,X*0F"), SentenceDefinition.create("SV", "DOC",
        "Request Automated Docking sequence", ImmutableList.of(course, teamCode), "$SVDOC,courseA,AUVSI*49"), SentenceDefinition.create("TD", "DOC",
        "Provide Automated Docking sequence", ImmutableList.of(timestamp, symbol, symbolColor, symbol, symbolColor),
        "$TDDOC,20150306061030,cruciform,red,triangle,blue*68"), SentenceDefinition.create("SV", "PIN", "Report active pinger",
        ImmutableList.of(course, teamCode, buoyColor), "$SVPIN,courseA,AUVSI,red*09"), SentenceDefinition.create("TD", "PIN",
        "Response of active pinger report", ImmutableList.of(timestamp, status), "$TDPIN,20150306061030,true*56"), SentenceDefinition.create("SV", "UAV",
        "Report shape for interop challenge", ImmutableList.of(course, teamCode, shape, imageId),
        "$SVUAV,courseA,AUVSI,8,a4aa8224-07f2-4b57-a03a-c8887c2505c7*7F"), SentenceDefinition.create("TD", "UAV", "Response of interop shape report",
        ImmutableList.of(timestamp, status), "$TDUAV,20150306061030,true*43"), SentenceDefinition.create("SV", "HRT", "Report heartbeat",
        ImmutableList.of(course, teamCode, timestamp, challenge, latitude, longitude), "$SVHRT,courseA,AUVSI,20150306061030,gates,40.689249,-74.044500*0B"),
        SentenceDefinition.create("TD", "HRT", "Response to heartbeat", ImmutableList.of(timestamp, status), "$TDHRT,20150306061030,true*4F"),
        SentenceDefinition.create("SV", "STR", "Request start of run", ImmutableList.of(course, teamCode), "$SVSTR,courseA,AUVSI*54"), SentenceDefinition
            .create("TD", "STR", "Response of start of run", ImmutableList.of(status), "$TDSTR,true*7F"), SentenceDefinition.create("SV", "END",
            "Request of end of run", ImmutableList.of(course, teamCode), "$SVEND,courseA,AUVSI*4E"), SentenceDefinition.create("TD", "END",
            "Response of start of run", ImmutableList.of(status), "$TDEND,true*65"));
    return new SentenceRegistry(sentences);
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
