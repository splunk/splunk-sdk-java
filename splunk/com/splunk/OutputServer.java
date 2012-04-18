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
     * Returns the destination host name for this connection.
     *
     * @return The destination host name.
     */
    public String getDestHost() {
        return getString("destHost", null);
    }

    /**
     * Returns the IP address of the destination host for this connection.
     *
     * @return The IP address of the destination host.
     */
    public String getDestIp() {
        return getString("destIp", null);
    }

    /**
     * Return the destination port for this connection.
     *
     * @return The destination port.
     */
    public int getDestPort() {
        return getInteger("destPort", 0);
    }

    /**
     * Return the destination port for this connection.
     *
     * @return The destination port.
     */
    public String getMethod() {
        return getString("method");
    }

    /**
     * Returns the source port for this connection.
     *
     * @return The source port.
     */
    public int getSourcePort() {
        return getInteger("sourcePort", 0);
    }

    /**
     * Returns the connection status. This is normally {@code connect_done}, or
     * {@code connect_fail} or {@code connect_try}.
     *
     * @return The connection status.
     */
    public String getStatus() {
        return getString("status", null);
    }

    /**
     * Sets the type of data distribution method when two or more servers
     * exist in the same forwarder group. Valid values are {@code clone},
     * {@code balance} or {@code autobalance}.
     *
     * @param method the distribution method.
     */
    public void setMethod(String method) {
        setCacheValue("method", method);
    }

    /**
     * Sets the alternate name to match in the remote server's SSL
     * certificate.
     *
     * @param sslAltNameToCheck the alternate name to match in the remote
     * server's SSL certificate.
     */
    public void setSslAltNameToCheck(String sslAltNameToCheck) {
        setCacheValue("sslAltNameToCheck", sslAltNameToCheck);
    }

    /**
     * Sets the Path to the client certificate. If specified, connection
     * uses SSL.
     *
     * @param sslCertPath the Path to the client certificate. If specified,
     * connection uses SSL.
     */
    public void setSslCertPath(String sslCertPath) {
        setCacheValue("sslCertPath", sslCertPath);
    }

    /**
     * Sets the SSL Cipher in the form:
     * {@code ALL:!aNULL:!eNULL:!LOW:!EXP:RC4+RSA:+HIGH:+MEDIUM}
     *
     * @param sslCipher the SSL Cipher.
     */
    public void setSslCipher(String sslCipher) {
        setCacheValue("sslCipher", sslCipher);
    }

    /**
     * Check the common name of the server's certificate against this name.
     * If there is no match, assume that Splunk is not authenticated against
     * this server. You must specify this setting if sslVerifyServerCert is
     * true.
     *
     * @param sslCommonNameToCheck the SSL Cipher.
     */
    public void setSslCommonNameToCheck(String sslCommonNameToCheck) {
        setCacheValue("sslCommonNameToCheck", sslCommonNameToCheck);
    }

    /**
     * Stets the password associated with the CAcert.
     *
     * @param sslPassword the password associated with the CAcert.
     */
    public void setSslPassword(String sslPassword) {
        setCacheValue("sslPassword", sslPassword);
    }

    /**
     * Sets the path to the root certificate authority file.
     *
     * @param sslRootCAPath the path to the root certificate authority file
     */
    public void setsslRootCAPath(String sslRootCAPath) {
        setCacheValue("sslRootCAPath", sslRootCAPath);
    }

    /**
     * Sets whether or not the server being connected to is authenticated.
     * Both the common name and the alternate name of the server are checked
     * for a match.
     *
     * @param sslVerifyServerCert whether or not the server being connected to
     * is authenticated.
     */
    public void setSslVerifyServerCert(String sslVerifyServerCert) {
        setCacheValue("sslVerifyServerCert", sslVerifyServerCert);
    }

    /**
     * Returns an object that contains all current connections to the output server.
     *
     * @return The all-connections object.
     */
    public OutputServerAllConnections allConnections() {
        return new OutputServerAllConnections(
                service, path + "/allconnections");
    }
}
