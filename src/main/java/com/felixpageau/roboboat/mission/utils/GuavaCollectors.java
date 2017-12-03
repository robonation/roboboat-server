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

import java.util.function.Function;
import java.util.stream.Collector;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
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
  
  /**
   * Collect a stream of elements with derived key into an {@link ImmutableMap}
   * @param keyMapper a {@link Function} that convert the input elements into keys
   * @return An {@link ImmutableMap} of derived keys with the elements streamed.
   */
  public static <T,K> Collector<T, ImmutableMap.Builder<K,T>, ImmutableMap<K,T>> immutableMap(Function<? super T, ? extends K> keyMapper) {
    return immutableMap(keyMapper, Function.identity());
  }
  
  /**
   * Transform elements from the stream and collect them into an {@link ImmutableMap} 
   * @param keyMapper a {@link Function} that converts the element into keys
   * @param valueMapper a {@link Function} that converts the element into values
   * @return An {@link ImmutableMap} containing all elements transformed into key/value pairs.
   */
  public static <T,K,V> Collector<T, ImmutableMap.Builder<K,V>, ImmutableMap<K,V>> immutableMap(Function<? super T, ? extends K> keyMapper,
      Function<? super T, ? extends V> valueMapper) {
    return Collector.of(ImmutableMap.Builder::new, (m,t) -> m.put(keyMapper.apply(t), valueMapper.apply(t)), (l, k) -> l.putAll(k.build()), ImmutableMap.Builder<K,V>::build);
  }

  private GuavaCollectors() {
    throw new AssertionError("No instances.");
  }
}