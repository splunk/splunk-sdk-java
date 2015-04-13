package com.splunk;

public enum SSLSecurityProtocol {
    TLSv1_2 {
        public String toString() { return "TLSv1.2"; }
    },
    TLSv1_1 {
        public String toString() { return "TLSv1.1"; }
    },
    TLSv1 {
        public String toString() { return "TLSv1"; }
    },
    SSLv3 {
        public String toString() { return "SSLv3"; }
    }
}
