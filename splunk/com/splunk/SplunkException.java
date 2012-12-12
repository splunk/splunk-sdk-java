/*
 * Copyright 2011 Splunk, Inc.
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

/**
 * Thrown for Splunk responses that return an error status code.
 */
public class SplunkException extends RuntimeException {
    private int code;
    private String text;

    public static final int JOB_NOTREADY = 1;
    public static final int TIMEOUT = 2;
    public static final int AMBIGUOUS = 3;
    public static final int INTERRUPTED = 4;

    SplunkException(int code, String text) {
        super(text);
        this.code = code;
        this.text = text;
    }

    public int getCode() {
        return code;
    }

    public String getText() {
        return text;
    }
}
