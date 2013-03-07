/*
 * Copyright 2012 Splunk, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"): you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.splunk;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Helper class for iterator over readers that only support a get operation
 * with null return indicating the end.
 * @param <T>  Type of elements.
 */
abstract class StreamIterableBase<T> implements Iterable<T> {
    private T cachedElement;
    private boolean nextElementCached;

    public Iterator<T> iterator() {

        return new Iterator<T>() {

            public boolean hasNext() {
                cacheNextElement();
                return cachedElement != null;
            }

            public T next()  {
                cacheNextElement();
                // Once reaching the end, don't advance any more.
                // Otherwise underlying reader may throw
                // which can be confusing.
                if (cachedElement == null) {
                    throw new NoSuchElementException();
                }
                else {
                    nextElementCached = false;
                }
                return cachedElement;
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    /**
     * Get the next element.
     * @return null if the end is reached.
     * @throws IOException
     */
    abstract T getNextElement() throws IOException;

    /**
     * Interrupt the iteration by setting the iterator to
     * either the initial state or the end state.
     * @param hasMoreResults Whether or not there are more results.
     */
    void resetIteration(boolean hasMoreResults) {
        // Throw away any not-null cached element.
        cachedElement = null;
        // If there's no more results, i.e., the end is reached,
        // set nextElementCached to true so that
        // the iterator will not try to get the next element.
        // Otherwise, if getNextElement is called by the iterator,
        // the underlying reader may throw which can be confusing.
        nextElementCached = !hasMoreResults;
    }

    private void cacheNextElement() {
        if (!nextElementCached) {
            try {
                cachedElement = getNextElement();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            nextElementCached = true;
        }
    }
}
