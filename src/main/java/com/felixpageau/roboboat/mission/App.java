package com.felixpageau.roboboat.mission;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.servlet.DispatcherType;
import javax.ws.rs.core.UriBuilder;

import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.LoginService;
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

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.thetransactioncompany.cors.CORSFilter;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Creates the standard roboboat server app
 */
@ParametersAreNonnullByDefault
public class App implements Runnable {
  public static final Charset APP_CHARSET = Charset.defaultCharset();
  public static final Locale APP_LOCALE = Locale.US;
  private final int port;
  private final int securePort;
  private final String srcPath;
  private final LoginService login;

  public App(int port, int securePort, String srcPath, @Nullable LoginService login) {
    Preconditions.checkArgument(port > 0 && port < 65535, String.format("0 < port (%d) < 65535", port));
    Preconditions.checkArgument(securePort > 0 && securePort < 65535, String.format("0 < securePort (%d) < 65535", securePort));
    this.port = port;
    this.securePort = securePort;
    this.srcPath = Preconditions.checkNotNull(srcPath, "srcPath cannot be null");
    this.login = login;
  }

  @Override
  @SuppressFBWarnings(value = "DE_MIGHT_IGNORE")
  public void run() {
    try {
      URI baseUri = UriBuilder.fromUri("https://localhost/").port(securePort).build();

      // JSON/HTTPS endpoint
      SslContextFactory sslContextFactory = new SslContextFactory(srcPath + "main/resources/keystore");
      sslContextFactory.setKeyStorePassword("qwerty123");
      CompetitionResourceConfig config = createApp();
      System.out.println(config.toString());

      // HTTP/JSON server
      JettyHttpContainer container = ContainerFactory.createContainer(JettyHttpContainer.class, config);
      Server server = JettyHttpContainerFactory.createServer(baseUri, sslContextFactory, container, false);
      ServerConnector http = new ServerConnector(server, new HttpConnectionFactory(new HttpConfiguration()));
      http.setPort(port);
      server.addConnector(http);
      WebAppContext wac = new WebAppContext();

      if (login != null) {
        server.addBean(login);
      }
      // wac.getSecurityHandler().setLoginService(myrealm);
      wac.setResourceBase(new File(srcPath + "main/webapp/").getAbsolutePath());
      wac.setDescriptor(new File(srcPath + "main/webapp/WEB-INF/web.xml").getAbsolutePath());
      wac.setContextPath("/");
      wac.setParentLoaderPriority(true);
      wac.addFilter(CORSFilter.class, "/*", EnumSet.of(DispatcherType.INCLUDE, DispatcherType.REQUEST));
      server.setHandler(wac);
      server.addLifeCycleListener(new JettyLifecycleAdapter() {
        @Override
        public void lifeCycleFailure(LifeCycle event, Throwable cause) {
          config.getNMEAServer().stop();
        }

        @Override
        public void lifeCycleStopping(LifeCycle event) {
          config.getNMEAServer().stop();
        }

        @Override
        public void lifeCycleStopped(LifeCycle event) {
          config.getNMEAServer().stop();
        }
      });

      server.start();
      System.out.println("\n\nJSON/HTTP Server started on: "
          + Arrays.stream(server.getConnectors())
              .map(c -> String.format("%s:%d", c.getProtocols().stream().findFirst().orElse(""), c.getServer().getURI().getPort()))
              .collect(Collectors.joining(", ")));
    } catch (Exception e) {

    }
  }

  protected CompetitionResourceConfig createApp() throws URISyntaxException {
    return new MissionResourceConfig();
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("port", port).add("securePort", securePort).add("srcPath", srcPath).add("login", login).toString();
  }

  public static void main(String[] args) {
    String srcPath = "mission2015-war/src/";
    HashLoginService login = new HashLoginService("RoboBoat");
    login.setConfig(srcPath + "main/resources/realm.properties");
    App app = new App(9000, 9443, srcPath, login);
    app.run();
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

    @SuppressFBWarnings(value = "IMC_IMMATURE_CLASS_PRINTSTACKTRACE")
    @Override
    public void lifeCycleFailure(LifeCycle event, Throwable cause) {
      cause.printStackTrace();
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
