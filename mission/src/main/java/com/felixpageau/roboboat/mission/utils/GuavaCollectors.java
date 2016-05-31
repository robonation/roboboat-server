package com.felixpageau.roboboat.mission.utils;

/*
 * Copyright (C) 2014 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import static java.util.stream.Collector.Characteristics.UNORDERED;

import java.util.stream.Collector;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/** Stream {@link Collector collectors} for Guava types. */
public final class GuavaCollectors {
  /** Collect a stream of elements into an {@link ImmutableList}. */
  public static <T> Collector<T, ImmutableList.Builder<T>, ImmutableList<T>> immutableList() {
    return Collector.of(ImmutableList.Builder::new, ImmutableList.Builder::add, (l, r) -> l.addAll(r.build()), ImmutableList.Builder<T>::build);
  }

  /** Collect a stream of elements into an {@link ImmutableSet}. */
  public static <T> Collector<T, ImmutableSet.Builder<T>, ImmutableSet<T>> immutableSet() {
    return Collector.of(ImmutableSet.Builder::new, ImmutableSet.Builder::add, (l, r) -> l.addAll(r.build()), ImmutableSet.Builder<T>::build, UNORDERED);
  }

  private GuavaCollectors() {
    throw new AssertionError("No instances.");
  }
}