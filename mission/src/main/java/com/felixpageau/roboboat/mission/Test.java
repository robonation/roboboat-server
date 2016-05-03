package com.felixpageau.roboboat.mission;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Test {
  public static void main(String[] args) throws IOException {
    System.out.println(Files.probeContentType(new File("roboboat2015-images/upload/21c3abe2-9fc3-48ce-87a0-fab66c603484").toPath()));
  }
}
