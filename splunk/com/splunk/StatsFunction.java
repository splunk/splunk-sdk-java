package com.splunk;

/**
 * Created by fross on 2/28/14.
 */
public enum StatsFunction {
    LIST {
        public String toString() { return "list"; }
    },
    DISTINCT_VALUES {
        public String toString() { return "values"; }
    },
    FIRST {
        public String toString() { return "first"; }
    },
    LAST {
        public String toString() { return "last"; }
    },
    COUNT {
        public String toString() { return "count"; }
    },
    DISTINCT_COUNT {
        public String toString() { return "dc"; }
    },
    SUM {
        public String toString() { return "sum"; }
    },
    AVERAGE {
        public String toString() { return "average"; }
    },
    MAX {
        public String toString() { return "max"; }
    },
    MIN {
        public String toString() { return "min"; }
    },
    STDEV {
        public String toString() { return "stdev"; }
    },
    DURATION {
        public String toString() { return "duration"; }
    },
    EARLIEST {
        public String toString() { return "earliest"; }
    },
    LATEST {
        public String toString() { return "latest"; }
    }
}
