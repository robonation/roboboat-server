package com.felixpageau.roboboat.mission.utils;

@FunctionalInterface
public interface ConsumerCheckException<T> {
  void accept(T elem) throws Exception;
}
