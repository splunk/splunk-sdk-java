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

// Using <T extends ResultsReader> is to allow specialization of T in
// subclasses of MultiResultReader. For example, MultiResultsReaderXml
// references ResultsReaderXml.
public class MultiResultsReader<T extends ResultsReader>
        extends StreamIterableBase<SearchResults>
        implements Iterable<SearchResults> {
    private T resultsReader;

    MultiResultsReader(T resultsReader) throws IOException {
        this.resultsReader = resultsReader;
    }

    /**
     * Returns an iterator over the sets of results from this reader.
     * @return an Iterator
     */
    @Override
    public final Iterator<SearchResults> iterator() {
        return super.iterator();
    }

    /**
     * Close the reader and release resources.
     * @throws IOException
     */
    public final void close() throws IOException {
        resultsReader.close();
    }

    protected final T getNextElement() {
        try {
            if (!resultsReader.resetIteratorToNextSet()) {
                return null;
            }
            return resultsReader;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}