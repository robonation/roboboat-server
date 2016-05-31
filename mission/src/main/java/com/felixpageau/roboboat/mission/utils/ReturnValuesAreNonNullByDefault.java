package com.felixpageau.roboboat.mission.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifierDefault;

/**
 * Created a new ReturnValuesAreNonNullByDefault since FB decided to deprecate
 * it
 */
@Nonnull
@TypeQualifierDefault(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ReturnValuesAreNonNullByDefault {

}