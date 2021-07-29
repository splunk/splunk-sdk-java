/*
 * Copyright 2014 Splunk, Inc.
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
 * Possible bin sizes for timestamp valued fields in pivots.
 */
public enum TimestampBinning {
    AUTO {
        public String toString() { return "auto"; }
    },
    YEAR {
        public String toString() { return "year"; }
    },
    MONTH {
        public String toString() { return "month"; }
    },
    DAY {
        public String toString() { return "day"; }
    },
    HOUR {
        public String toString() { return "hour"; }
    },
    MINUTE {
        public String toString() { return "minute"; }
    },
    SECOND {
        public String toString() { return "second"; }
    }
};
