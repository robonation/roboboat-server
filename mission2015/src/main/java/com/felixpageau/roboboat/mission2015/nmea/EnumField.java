/**
 * 
 */
package com.felixpageau.roboboat.mission2015.nmea;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

/**
 * @author felixpageau
 *
 */
public class EnumField extends Field {
  private final List<String> enumeration;

  public EnumField(String name, List<String> enumeration) {
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
