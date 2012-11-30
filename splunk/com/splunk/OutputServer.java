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

/**
 * The {@code OutputServer} class represents an output server, providing access
 * to data-forwarding configurations.
 */
public class OutputServer extends Entity {

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param path The output server endpoint.
     */
    OutputServer(Service service, String path) {
        super(service, path);
    }

     /**
     * Returns the DNS name of the destination server for this connection.
     *
     * @return The destination host name.
     */
    public String getDestHost() {
        return getString("destHost", null);
    }

    /**
     * Returns the IP address of the destination server for this connection.
     *
     * @return The IP address of the destination server.
     */
    public String getDestIp() {
        return getString("destIp", null);
    }

    /**
     * Return the port on which the destination server is listening.
     *
     * @return The destination port.
     */
    public int getDestPort() {
        return getInteger("destPort", 0);
    }

    /**
     * Returns the data distribution method used when two or more servers exist 
     * in the same forwarder group. Valid values are: "clone", "balance", and 
     * "autobalance". 
     *
     * @return The data distribution method.
     */
    public String getMethod() {
        return getString("method");
    }

    /**
     * Returns the port on the destination server where data is forwarded.
     *
     * @return The source port.
     */
    public int getSourcePort() {
        return getInteger("sourcePort", 0);
    }

    /**
     * Returns the server connection status (usually "connect_done", 
     * "connect_fail", or "connect_try").
     *
     * @return The connection status.
     */
    public String getStatus() {
        return getString("status", null);
    }

    /**
     * Sets the type of data distribution method when two or more servers
     * exist in the same forwarder group. Valid values are: "clone", "balance", 
     * and "autobalance".
     *
     * @param method The data distribution method.
     */
    public void setMethod(String method) {
        setCacheValue("method", method);
    }

    /**
     * Sets the alternate name to match in the remote server's SSL certificate.
     *
     * @param sslAltNameToCheck The alternate name.
     */
    public void setSslAltNameToCheck(String sslAltNameToCheck) {
        setCacheValue("sslAltNameToCheck", sslAltNameToCheck);
    }

    /**
     * Sets the path to the client certificate. If a path is specified, the 
     * connection uses SSL. 
     *
     * @param sslCertPath The path to the client certificate. 
     */
    public void setSslCertPath(String sslCertPath) {
        setCacheValue("sslCertPath", sslCertPath);
    }

    /**
     * Sets the SSL cipher in the form:
     * {@code ALL:!aNULL:!eNULL:!LOW:!EXP:RC4+RSA:+HIGH:+MEDIUM}
     *
     * @param sslCipher The SSL cipher.
     */
    public void setSslCipher(String sslCipher) {
        setCacheValue("sslCipher", sslCipher);
    }

    /**
     * Sets the name against which to check the common name of the server's 
     * certificate. If there is no match, you can assume that Splunk is not 
     * authenticated against this server. You must set a value for this 
     * parameter if {@code SslVerifyServerCert} is {@code true}.
     * @see #setSslVerifyServerCert
     *
     * @param sslCommonNameToCheck The SSL common name.
     */
    public void setSslCommonNameToCheck(String sslCommonNameToCheck) {
        setCacheValue("sslCommonNameToCheck", sslCommonNameToCheck);
    }

    /**
     * Sets the password associated with the PEM file store. 
     *
     * @param sslPassword The password.
     */
    public void setSslPassword(String sslPassword) {
        setCacheValue("sslPassword", sslPassword);
    }
    
    /**
     * Sets the path to the root certificate authority file.
     *
     * @param sslRootCAPath The path to the root certificate authority file.
     */
    public void setSslRootCAPath(String sslRootCAPath) {
        setCacheValue("sslRootCAPath", sslRootCAPath);
    }

    /**
     * Sets whether the server being connected to is authenticated.
     * Both the common name and the alternate name of the server are checked
     * for a match.
     *
     * @param sslVerifyServerCert {@code true} to determine whether the server 
     * is authenticated, {@code false} if not.
     */
    public void setSslVerifyServerCert(boolean sslVerifyServerCert) {
        setCacheValue("sslVerifyServerCert", sslVerifyServerCert);
    }

    /**
     * Returns an object that contains all current connections to the output
     * server.
     *
     * @return The {@code OutputServerAllConnections} object.
     */
    public OutputServerAllConnections allConnections() {
        return new OutputServerAllConnections(
                service, path + "/allconnections");
    }
}
