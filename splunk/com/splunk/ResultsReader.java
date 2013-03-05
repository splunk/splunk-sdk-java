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

/*
 * Summary of class relationships and control flow
 *
 * All result readers support both the Iterator interface and
 * getNextEvent method. They share the same underlying implementation
 * of pureGet(). The iterator interface is supported through
 * the base class, StreamIterableBase (which is also used by
 * multi result readers).
 *
 * Some result readers support multiple result sets in the input stream.
 * A result set can be skipped, or combined with the
 * previous result set with newer events returned through the same
 * iterator used for the older events even through they are in different result
 * sets.
 *
 * Such a result reader is also used by a multi result reader which
 * returns an iterator over the result sets, with one result set returned
 * in one iteration, as SearchResults. SearchResults is an interface consisting
 * of getters of preview flag, field name list, and an iterator over events.
 * Unlike ResultReader, SearchResults does not have a close method. Only the
 * containing multi reader needs to be closed by an application.
 */

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
     * Returns the next event in the event stream.
     *
     * @return The map of key-value pairs for an event.
     *         The format of multi-item values is implementation-specific.
     *         We recommend using the methods from the
     *         {@link Event} class to interpret multi-item values.
     * @throws IOException On IO exception.
     */
    final public Event getNextEvent() throws IOException {
        return pureGet();
    }

    /**
     * Return the next event while moving onto the the next set
     * automatically when needed, i.e., concatenating final results
     * across multiple sets.
     * @return  null if the end is reached
     * @throws IOException On IO exception.
     */
    final Event pureGet() throws IOException {
        Event event = null;
        while (true) {
            event = pureGetFromSingleSet();
            if (event != null)
                break;
            if (isPreview)
                break;
            if (!advanceStreamToNextSet())
                break;
            assert (!isPreview()) :
                "Preview result set should never be after a final set.";
        }
        return event;
    }

    /*
     * Get the next event in the current result set.
     */
    abstract Event pureGetFromSingleSet() throws IOException;

    /*
     * Return false if the end is reached.
     */
    final boolean resetIteratorToNextSet() throws IOException {
        // Throw away any not-null cached element.
        cachedElement = null;
        // If the end of stream is reached, null element in the cache
        // should be used. Setting nextElementCached to true in that case,
        // so the iteration will return null in the cache without trying to
        // read next element from the stream.
        // Otherwise, if pureGet is called by the iterator
        // the underlying reader may throw which can be confusing.
        nextElementCached = !advanceStreamToNextSet();
        return !nextElementCached;
    }

    /*
    * Return false if the end is reached.
    */
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
            if (!advanceStreamToNextSet())
                throw new RuntimeException(
                        "No result set found.");

            if (!isExportStream)
                break;

            if (!isPreview)
                break;
        }
    }
}
