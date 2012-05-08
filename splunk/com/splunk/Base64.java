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

import java.io.UnsupportedEncodingException;

/*
 * This module is used to turn binary date in to base64 (radix-64) encoding.
 * Its primary use is a utility function to encode the StormService
 * authorization token.
 */
public class Base64 {

    private static final String base64code =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

    public static byte[] zeroPad(int length, byte[] bytes) {
        byte[] padded = new byte[length];
        System.arraycopy(bytes, 0, padded, 0, bytes.length);
        return padded;
    }

    public static String encode(String string) {
        String encoded = "";
        byte[] byteArray = {};
        try {
            byteArray = string.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) { assert false; }

        int paddingCount = (3 - (byteArray.length % 3)) % 3;
        byteArray = zeroPad(byteArray.length + paddingCount, byteArray);
        for (int i = 0; i < byteArray.length; i += 3) {
            int j = ((byteArray[i] & 0xff) << 16) +
                    ((byteArray[i + 1] & 0xff) << 8) +
                    (byteArray[i + 2] & 0xff);
            encoded = encoded +
                    base64code.charAt((j >> 18) & 0x3f) +
                    base64code.charAt((j >> 12) & 0x3f) +
                    base64code.charAt((j >> 6) & 0x3f) +
                    base64code.charAt(j & 0x3f);
        }
        return encoded;
    }
}