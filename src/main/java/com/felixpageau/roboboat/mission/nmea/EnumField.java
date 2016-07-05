/**
 * 
 */
package com.felixpageau.roboboat.mission.nmea;

import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

/**
 * @author felixpageau
 *
 */
public class EnumField extends Field {
  private final Set<String> enumeration;

  public EnumField(String name, Set<String> enumeration) {
    super(name);
    this.enumeration = Preconditions.checkNotNull(enumeration, "enumeration cannot be null");
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.felixpageau.roboboat.mission2014.nmea.Field#isValid()
   */
  @Override
  public boolean isValid(String value) {
    if (value == null) return false;
    return enumeration.contains(value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.felixpageau.roboboat.mission2014.nmea.Field#validityCondition()
   */
  @Override
  public String validityCondition() {
    return String.format("Valid values: [%s]", enumeration.stream().collect(Collectors.joining(", ")));
  }

}
