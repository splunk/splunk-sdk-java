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
 * for Splunk search results. It should be used to get previews from export.
 */
public abstract class ResultsReader<T extends ResultsReader<T>>
        extends StreamIterableBase<Event>
        implements SearchResults {
    protected InputStreamReader inputStreamReader = null;
    protected boolean isPreview;
    private boolean isExport;
    private boolean isMultiReader;

    /**
     * Class constructor.
     *
     * @param inputStream The unread return input stream from a Splunk query or
     * export.
     * @throws IOException On IO exception.
     */
    public ResultsReader(InputStream inputStream) throws IOException {
        this(inputStream, false);
    }

    ResultsReader(InputStream inputStream, boolean isMultiReader)
            throws IOException {
        inputStreamReader = new InputStreamReader(inputStream, "UTF8");
        isExport = inputStream instanceof ExportResultsStream;
        this.isMultiReader = isMultiReader;
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
    public Event getNextEvent() throws IOException {
        return getNext();
    };

    abstract Event getNextInner() throws IOException;

    Event getNext() throws IOException {
        Event event = null;
        while ((event = getNextInner()) == null &&
            !isPreview) {
            if (!moveToNextSet()) {
                return null;
            }
            assert (!isPreview()) :
                "Final result set should never be after preview.";
        }
         return event;
    }

    boolean moveToNextSet() throws IOException {
        onNext = false;
        return moveToNextSetStreamPosition();
    }

    boolean moveToNextSetStreamPosition() throws IOException {
        // Indicate that no more sets are available
        // Subclasses can override this method to support
        // MultiResultsReader.
        return false;
    };

    // This method has two purposes:
    // 1. Obtain the preview flag and the field list.
    // 2. Skip any previews for export.
    void readyForIterable() throws IOException {
        if (isMultiReader) {
            return;
        }

        while (true) {
            if (!moveToNextSet()) {
                throw new RuntimeException(
                        "No result set found.");
            }

            if (!isExport){
                break;
            }

            if (!isPreview) {
                break;
            }
        }
    }
}
