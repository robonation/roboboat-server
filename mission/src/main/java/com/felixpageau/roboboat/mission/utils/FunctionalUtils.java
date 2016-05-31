package com.felixpageau.roboboat.mission.utils;

import java.util.function.Consumer;

public class FunctionalUtils {
  public static <T> Consumer<T> rethrow(ConsumerCheckException<T> c) {
    return elem -> {
      try {
        c.accept(elem);
      } catch (Exception ex) {
        FunctionalUtils.<RuntimeException> sneakyThrow(ex);
      }
    };
  }

  /**
   * Reinier Zwitserloot who, as far as I know, had the first mention of this
   * technique in 2009 on the java posse mailing list.
   * http://www.mail-archive.com/javaposse@googlegroups.com/msg05984.html
   * 
   * @throws T
   */
  @SuppressWarnings("unchecked")
  public static <T extends Throwable> T sneakyThrow(Throwable t) throws T {
    throw (T) t;
  }
}
