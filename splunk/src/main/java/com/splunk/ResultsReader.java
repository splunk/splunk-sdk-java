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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

/**
 * The {@code ResultsReader} class is a base class for the streaming readers
 * for Splunk search results. This class should not be used to retrieve preview
 * results for an export search.
 */
public abstract class ResultsReader
        extends StreamIterableBase<Event>
        implements SearchResults {
    protected final InputStream inputStream;
    // Default should be false which will result in no result set skipping.
    boolean isPreview;
    boolean isExportStream;
    private boolean isInMultiReader;

    ResultsReader(InputStream inputStream, boolean isInMultiReader)
            throws IOException {
        this.inputStream = inputStream;
        isExportStream = inputStream instanceof ExportResultsStream;
        this.isInMultiReader = isInMultiReader;
    }

    /**
     * Closes the reader and returns resources.
     *
     * @throws IOException On IO exception.
     */
    public void close() throws IOException {
        inputStream.close();
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
        return getNextElement();
    }

    /**
     * Returns an iterator over the events from this reader.
     * @return an Iterator.
     */
    @Override
    public final Iterator<Event> iterator() {
        return super.iterator();
    }

    /**
     * Returns the next event while moving to the next set
     * automatically when needed, such as concatenating final results
     * across multiple sets.
     * 
     * @return  null {@code null} if the end is reached.
     * @throws IOException On IO exception.
     */
    final Event getNextElement() throws IOException {
        Event event;
        while (true) {
            event = getNextEventInCurrentSet();

            // If we actually managed to get an event, then we break and return it
            if (event != null)
                break;

            // We don't concatenate across previews across sets, since each set
            // might be a snapshot at a given time or a summary result with
            // partial data from a reporting search
            // (for example "count by host"). So if this is a preview,
            // break. Null return indicating the end of the set.
            if (isPreview)
                break;

            // If we did not advance to next set, i.e. the end of stream is
            // reached, break. Null return indicating the end of the set.
            if (!advanceStreamToNextSet())
                break;

            // We have advanced to the next set. isPreview is for that set.
            // It should not be a preview. Splunk should never return a preview
            // after final results which we might have concatenated together
            // across sets.
            assert (!isPreview) :
                "Preview result set should never be after a final set.";
        }
        return event;
    }

    /*
     * Get the next event in the current result set. Return null
     * if the end is reached.
     */
    abstract Event getNextEventInCurrentSet() throws IOException;

    /*
     * Return false if the end is reached.
     */
    final boolean resetIteratorToNextSet() throws IOException {

        // Get to the beginning of the next set in the stream
        // skipping remaining event(s) if any in the current set.
        boolean hasMoreResults = advanceStreamToNextSet();

        // Reset the iterator so that it would either fetch a new
        // element for the next iteration or stop.
        resetIteration(hasMoreResults);

        return hasMoreResults;
    }

    /*
     * Return false if the end is reached.
     */
    boolean advanceStreamToNextSet() throws IOException {
        // Indicate that no more sets are available
        // Subclasses can override this method to support
        // MultiResultsReader.
        return false;
    }

    /*
     * This method is used by constructors of result readers to do
     * the following for single reader:
     * 1. Obtain the preview flag and the field list.
     * 2. Skip any previews for export.
     */
    final void finishInitialization() throws IOException {
        if (isInMultiReader)
            return;

        while (true) {
            // Stop if no more set is available
            if (!advanceStreamToNextSet()) {
                // Terminating the iteration.
                // This avoids future callings into the underlying reader
                // to get events, which may result in exceptions.
                resetIteration(false);
                break;
            }

            // No skipping of result sets if the stream
            // is not from an export endpoint.
            if (!isExportStream)
                break;

            // Skipping ends at any file results.
            if (!isPreview)
                break;
        }
    }
}
