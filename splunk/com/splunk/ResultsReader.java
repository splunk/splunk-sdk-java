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

import java.io.*;

/**
 * The {@code ResultsReader} class is a base class for the streaming readers
 * for Splunk search results. It should not be used to get previews from export.
 */
public abstract class ResultsReader<T extends ResultsReader<T>>
        extends StreamIterableBase<Event>
        implements SearchResults {
    InputStreamReader inputStreamReader = null;
    boolean isPreview;
    boolean isExportStream;
    boolean isInMultiReader;

    ResultsReader(InputStream inputStream, boolean isInMultiReader)
            throws IOException {
        inputStreamReader = new InputStreamReader(inputStream, "UTF8");
        isExportStream = inputStream instanceof ExportResultsStream;
        this.isInMultiReader = isInMultiReader;
    }

    /**
     * Closes the reader and returns resources.
     *
     * @throws IOException On IO exception.
     */
    public void close() throws IOException {
        if (inputStreamReader != null)
            inputStreamReader.close();
        inputStreamReader = null;
    }

    /**
     * Returns the cachedElement event in the event stream.
     *
     * @return The map of key-value pairs for an event.
     *         The format of multi-item values is implementation-specific.
     *         We recommend using the methods from the
     *         {@link Event} class to interpret multi-item values.
     * @throws IOException On IO exception.
     */
    final public Event getNextEvent() throws IOException {
        return getNextElement();
    };

    final Event getNextElement() throws IOException {
        Event event = null;
        while ((event = getNextElementRaw()) == null &&
            !isPreview) {
            if (!advanceIteratorToNextSet())
                return null;
            // Concat final results across result sets.
            assert (!isPreview()) :
                "Final result set should never be after preview.";
        }
         return event;
    }

    abstract Event getNextElementRaw() throws IOException;

    final boolean advanceIteratorToNextSet() throws IOException {
        // Throw away any not-null cached element.
        cachedElement = null;
        // If the end of stream is reached, null element in the cache
        // should be used.
        // Otherwise, if getNextElement is called by the iterator
        // the underlying reader may throw which can be confusing.
        nextElementCached = !advanceStreamToNextSet();
        // The advancement happened if and only if the cache is cleared.
        return !nextElementCached;
    }

    boolean advanceStreamToNextSet() throws IOException {
        // Indicate that no more sets are available
        // Subclasses can override this method to support
        // MultiResultsReader.
        return false;
    };

    // This method is used by constructors of result readers to do
    // the following for single reader:
    // 1. Obtain the preview flag and the field list.
    // 2. Skip any previews for export.
    final void finishInitialization() throws IOException {
        if (isInMultiReader)
            return;

        while (true) {
            if (!advanceIteratorToNextSet())
                throw new RuntimeException(
                        "No result set found.");

            if (!isExportStream)
                break;

            if (!isPreview)
                break;
        }
    }
}
