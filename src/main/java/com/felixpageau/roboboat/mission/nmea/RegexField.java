/**
 * 
 */
package com.felixpageau.roboboat.mission.nmea;

import java.util.regex.Pattern;

import com.google.common.base.Preconditions;

/**
 * @author felixpageau
 *
 */
public class RegexField extends Field {
  private final Pattern pattern;

  public RegexField(String name, Pattern pattern) {
    super(name);
    this.pattern = Preconditions.checkNotNull(pattern);
  }

  public RegexField(String name, String pattern) {
    this(name, Pattern.compile(pattern));
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.felixpageau.roboboat.mission2014.nmea.Field#isValid()
   */
  @Override
  public boolean isValid(String value) {
    if (value == null) return false;
    return pattern.matcher(value).matches();
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.felixpageau.roboboat.mission2014.nmea.Field#validityCondition()
   */
  @Override
  public String validityCondition() {
    return String.format("Regex: [%s]", pattern.pattern());
  }

}
