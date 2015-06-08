package com.felixpageau.roboboat.mission2015.structures;

public enum Challenge {
  gates, 
  obstacles, 
  docking, 
  pinger, 
  interop,
  return_to_dock;
  
  public String toString() {
    return name().replaceFirst("_.*", "");
  };
}
