package com.felixpageau.roboboat.mission;

import java.net.URISyntaxException;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.eclipse.jetty.security.LoginService;

/**
 * Creates the Admin version of the RoboBoat server app (includes admin
 * resources)
 */
@ParametersAreNonnullByDefault
public class AdminApp extends App {
  public AdminApp(int port, int securePort, String srcPath, @Nullable LoginService login) {
    super(port, securePort, srcPath, login);
  }

  public static void main(String[] args) {
    App app = new AdminApp(9000, 9443, "mission2016-admin-war/src/", null);
    app.run();
  }

  @Override
  protected CompetitionResourceConfig createApp() throws URISyntaxException {
    return new AdminResourceConfig();
  }
}
