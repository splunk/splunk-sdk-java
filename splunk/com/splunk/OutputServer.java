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
     * Returns the back-off, in seconds, to wait to retry the first time a
     * retry is needed.
     *
     * @return The back-off, in seconds, when the first retry is needed.
     */
    public Integer getBackoffAtStartup() {
        return getInteger("backoffAtStartup", -1);
    }

    /**
     * Returns the back-off, in seconds, to wait to retry after the first
     * rerty, when a retry is needed.
     *
     * @return The back-off, in seconds, when subsequent retries are needed.
     */
    public Integer getInitialBackoff() {
        return getInteger("initialBackoff", -1);
    }

    /**
     * Returns the number of back-offs before reaching the maximum back-off
     * frequency.
     *
     * @return the number of back-offs before reaching the maximum back-off
     * frequency.
     */
    public Integer getMaxBackoff() {
        return getInteger("maxBackoff", -1);
    }

    /**
     * Returns the number of times the system should retry after reaching the
     * highest back-off period, before stopping completely. {@code -1}, the
     * default value, means to try forever.
     *
     * @return the number of times the system should retry after reaching the
     * highest back-off period, before stopping completely.
     */
    public Integer getMaxNumberOfRetriesAtHighestBackoff() {
        return getInteger("maxNumberOfRetriesAtHighestBackoff", -1);
    }

    /**
     * Returns the type of data distribution method when two or more servers
     * exist in the same forwarder group. Valid values are {@code clone},
     * {@code balance} or {@code autobalance}.
     *
     * @return The data distribution method used when two or more servers
     * exist in the same forwarder group.
     */
    public String getMethod() {
        return getString("method", null);
    }

    /**
     * Returns the alternate name to match in the remote server's SSL
     * certificate.
     *
     * @return the alternate name to match in the remote server's SSL
     * certificate.
     */
    public String getSslAltNameToCheck() {
        return getString("sslAltNameToCheck", null);
    }

    /**
     * Returns the Path to the client certificate. If specified, connection
     * uses SSL.
     *
     * @return the Path to the client certificate. If specified, connection
     * uses SSL.
     */
    public String getSslCertPath() {
        return getString("sslCertPath", null);
    }

    /**
     * Returns the SSL Cipher in the form:
     * {@code ALL:!aNULL:!eNULL:!LOW:!EXP:RC4+RSA:+HIGH:+MEDIUM}
     *
     * @return the SSL Cipher.
     */
    public String getSslCipher() {
        return getString("sslCipher", null);
    }

    /**
     * Check the common name of the server's certificate against this name.
     * If there is no match, assume that Splunk is not authenticated against
     * this server. You must specify this setting if sslVerifyServerCert is
     * true.
     *
     * @return the SSL Cipher.
     */
    public String getSslCommonNameToCheck() {
        return getString("sslCommonNameToCheck", null);
    }

    /**
     * Returns the password associated with the CAcert.
     *
     * @return the password associated with the CAcert.
     */
    public String getSslPassword() {
        return getString("sslPassword", null);
    }

    /**
     * Returns the path to the root certificate authority file.
     *
     * @return the path to the root certificate authority file
     */
    public String getsslRootCAPath() {
        return getString("sslRootCAPath", null);
    }

    /**
     * Returns whether or not the server being connected to is authenticated.
     * Both the common name and the alternate name of the server are checked
     * for a match.
     *
     * @return whether or not the server being connected to is authenticated.
     */
    public boolean getSslVerifyServerCert() {
        return getBoolean("sslVerifyServerCert", false);
    }

    /**
     * Sets, in seconds, how long to wait to retry the first time a retry is
     * needed.
     *
     * @param backoffAtStartup how long, in seconds, to wait to retry the first
     * time a retry is needed.
     */
    public void setBackoffAtStartup(int backoffAtStartup) {
        setCacheValue("backoffAtStartup", backoffAtStartup);
    }

    /**
     * Sets how long, in seconds, to wait to retry every time after the first
     * retry.
     *
     * @param initialBackoff how long, in seconds, to wait to retry every time
     * after the first retry.
     */
    public void setInitialBackoff(int initialBackoff) {
        setCacheValue("initialBackoff", initialBackoff);
    }

    /**
     * Sets the number of back-offs before reaching the maximum back-off
     * frequency.
     *
     * @param maxBackoff the number of back-offs before reaching the
     * maximum back-off frequency.
     */
    public void setMaxBackoff(int maxBackoff) {
        setCacheValue("maxBackoff", maxBackoff);
    }

    /**
     * Sets the number of times the system should retry after reaching the
     * highest back-off period, before stopping completely. {@code -1}, the
     * default value, means to try forever.
     *
     * @param maxNumberOfRetriesAtHighestBackoff the number of times the system
     * should retry after reaching the highest back-off period, before stopping
     * completely.
     */
    public void setmaxNumberOfRetriesAtHighestBackoff(
            int maxNumberOfRetriesAtHighestBackoff) {
        setCacheValue("maxNumberOfRetriesAtHighestBackoff",
                       maxNumberOfRetriesAtHighestBackoff);
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
