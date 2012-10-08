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

import java.util.Arrays;
import java.util.List;

public class Util {
    /**
     * Join the strings in {@code joinees}, separated by {@code joiner}.
     *
     * For example, {@code join("/", list)} where {@code list} contains the strings {@code "a"},
     * {@code "b"}, and {@code "c"} returns "a/b/c";
     *
     * @param joiner String to interpolate between each element of {@code joinees}.
     * @param joinees List of strings to join.
     * @return The strines in {@code joinees} concatenated with a copy of {@code joiner} between each.
     */
    public static String join(String joiner, List<String> joinees) {
        if (joinees.isEmpty()) {
            return "";
        } else {
            StringBuilder joined = new StringBuilder();
            joined.append(joinees.get(0));
            for (String s : joinees.subList(1, joinees.size())) {
                joined.append(joiner);
                joined.append(s);
            }
            return joined.toString();
        }
    }

    /**
     * @see {@link #join(String, List<String>)}
     */
    public static String join(String joiner, String[] joinees) {
        return join(joiner, Arrays.asList(joinees));
    }

    /**
     * Return the substring of {@code template} beginning after {@code toFind}
     * occurs, or {@code defaultTo} if {@code toFind} does not occur. For example,
     * {@code substringAfter("This is a test", "is a", "abcd")} returns {@code " test"},
     * while {@code substringAfter("This is a test", "boris", "abcd")} returns {@code "abcd"}.
     *
     * @param template String to search in.
     * @param toFind String to search for.
     * @param defaultTo String to return if {@code toFind} does not occur in {@code template}.
     * @return Substring of {@code template} beginning after {@code toFind},
     *         or {@code defaultTo} if {@code toFind} is not there.
     */
    public static String substringAfter(String template, String toFind, String defaultTo) {
        int toFindLength = toFind.length();
        int toFindOffset = template.indexOf(toFind);
        int substringOffset = toFindOffset + toFindLength;
        String returnValue;
        if (toFindOffset == -1) { // toFind not found in template
            returnValue = defaultTo;
        } else {
            returnValue = template.substring(substringOffset);
        }
        return returnValue;
    }
}
