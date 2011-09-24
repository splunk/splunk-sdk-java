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

package com.splunk.http;

import java.io.InputStream;

public class ResponseMessage {
    int status;
    MessageHeader header = null;
    InputStream content;

    ResponseMessage() {}

    ResponseMessage(int status) {
        this.status = status;
    }

    ResponseMessage(int status, InputStream content) {
        this.status = status;
        this.content = content;
    }

    public InputStream getContent() {
        return this.content;
    }

    public MessageHeader getHeader() {
        if (this.header == null)
            this.header = new MessageHeader();
        return this.header;
    }

    public int getStatus() {
        return this.status;
    }
}
