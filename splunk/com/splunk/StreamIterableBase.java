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

abstract class StreamIterableBase<T> implements Iterable<T> {
    /** {@inheritDoc} */
    private T next;
    protected boolean onNext;

    public Iterator<T> iterator(){

        return new Iterator<T>(){

            public boolean hasNext() {
                getOnNext();
                return next != null;
            }

            public T next()  {
                getOnNext();
                if (next == null) {
                    throw new NoSuchElementException();
                }
                onNext = false;
                return next;
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    abstract T getNext() throws IOException;

    private void getOnNext() {
        if (!onNext)
        {
            try {
                next = getNext();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            onNext = true;
        }
    }
}
