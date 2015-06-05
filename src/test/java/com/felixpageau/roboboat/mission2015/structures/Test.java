package com.felixpageau.roboboat.mission2015.structures;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Test {
  public Test(int port) throws IOException {
    System.out.println(String.format("Listening on port: %d", port));
    try (ServerSocket ss = new ServerSocket(port)) {
      while (true) {
        try (Socket s = ss.accept(); BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()))) {
          String str = br.readLine();
          while (str != null) {
            System.out.println(String.format("%d > %s", port, str));
            str = br.readLine();
          }
          System.out.println(String.format("%d > %s", port, str));
          System.out.println(">");
          s.getOutputStream().write("200 OK\nDate: Sun, 09 Mar 2014 02:03:22 GMT\nContent-Length: 0\n\n\n".getBytes());
          s.getOutputStream().close();
        }
      }
    }
  }

  public static void main(String[] args) throws IOException {
    new Thread() {
      @Override
      public void run() {
        try {
          new Test(4000);
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      };
    }.start();

    new Thread() {
      @Override
      public void run() {
        try {
          new Test(5000);
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      };
    }.start();

    // new Thread(){
    // public void run() {
    // try {
    // new Test(9000);
    // } catch (IOException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // };
    // }.start();
  }
}
