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

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * The {@code Util} class contains string utilities.
 */
class Util {
    /**
     * Joins the strings in {@code joinees}, separated by {@code joiner}.
     *
     * For example, {@code list} contains the strings {@code "a"}, {@code "b"}, 
     * and {@code "c"}. To combine these strings as "a/b/c", use 
     * {@code join("/", list)}.
     *
     * @param joiner The string to use as the delimiter between each element.
     * @param joinees The list of strings to join.
     * @return The combined strings in {@code joinees} separated  by the 
     * {@code joiner} delimiter.
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
     * Joins the strings in {@code joinees}, separated by {@code joiner}.
     *
     * For example, {@code list} contains the strings {@code "a"}, {@code "b"}, 
     * and {@code "c"}. To combine these strings as "a/b/c", use 
     * {@code join("/", list)}.
     *
     * @param joiner The string to use as the delimiter between each element.
     * @param joinees The array of strings to join.
     * @return The combined strings in {@code joinees} separated  by the 
     * {@code joiner} delimiter.
     */
    public static String join(String joiner, String[] joinees) {
        return join(joiner, Arrays.asList(joinees));
    }

    /**
     * Searches a given string ({@code template}) for a substring 
     * ({@code toFind}) and returns the portion of the string that follows the
     * substring. If the {@code toFind} string is not found in {@code template},
     * an alternative string ({@code defaultTo}) is returned. 
     * <br>
     * For example: 
     * <br>
     * {@code substringAfter("This is a test", "is a ", "abcd")} 
     * returns {@code "test"}. 
     * <br>
     * {@code substringAfter("This is a test", "boris",
     * "abcd")} returns {@code "abcd"}.
     *
     * @param template The string to search in.
     * @param toFind The substring to search for.
     * @param defaultTo The alternative string to return if {@code toFind} is 
     *        not found.
     * @return The substring of {@code template} that follows {@code toFind},
     *         or {@code defaultTo} if {@code toFind} is not found.
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

    /**
     * Throws an IllegalArgumentException if the specified namespace
     * is a wildcarded namespace.
     */
    public static void ensureNamespaceIsExact(Args namespace) {
        String app = (String)namespace.get("app");
        String owner = (String)namespace.get("owner");
        
        boolean wildcardedApp = (app == null || app.equals("-"));
        boolean wildcardedOwner = (owner == null || owner.equals("-"));
        boolean isExact = !wildcardedApp && !wildcardedOwner;
        
        if (!isExact) {
            throw new IllegalArgumentException(
                    "An exact namespace must be provided.");
        }
    }
}
