/**
 * 
 */
package com.felixpageau.roboboat.mission;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author felixpageau
 *
 */
public class EchoProxy {
  /**
   * @param args
   * @throws IOException
   * @throws UnknownHostException
   */
  public EchoProxy(int proxyPort, int servicePort) throws UnknownHostException, IOException {
    try (Socket s = new Socket("127.0.0.1", servicePort);
        BufferedReader serviceOut = new BufferedReader(new InputStreamReader(s.getInputStream()));
        BufferedWriter serviceIn = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
        ServerSocket ss = new ServerSocket(proxyPort);
        BufferedReader proxyOut = new BufferedReader(new InputStreamReader(s.getInputStream()));
        BufferedWriter proxyIn = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));) {

    }
  }

}
