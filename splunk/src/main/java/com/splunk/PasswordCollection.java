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
 * The {@code PasswordCollection} class represents a collection of credentials.
 */
public class PasswordCollection extends EntityCollection<Password> {

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     */
    PasswordCollection(Service service) {
        super(service, service.passwordEndPoint, Password.class);
    }

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param args Collection arguments that specify the number of entities to 
     * return and how to sort them. See {@link CollectionArgs}.
     */
    PasswordCollection(Service service, Args args) {
        super(service, service.passwordEndPoint, Password.class, args);
    }

    /**
     * Creates a credential with a username and password.
     *
     * @param name The username.
     * @param password The password.
     *
     * @return The new credential.
     */
    public Password create(String name, String password) {
        if(checkForWildcards()){
            throw new IllegalArgumentException("While creating StoragePasswords, namespace cannot have wildcards.");
        }
        Args args = new Args("password", password);
        return create(name, args);
    }

    /**
     * Creates a credential with a username, password, and realm.
     *
     * @param name The username.
     * @param password The password.
     * @param realm The credential realm.
     * @return The new credential.
     */
    public Password create(String name, String password, String realm) {
        if(checkForWildcards()){
            throw new IllegalArgumentException("While creating StoragePasswords, namespace cannot have wildcards.");
        }
        Args args = new Args();
        args.put("password", password);
        args.put("realm", realm);
        return create(name, args);
    }

    /**
     * Get a credential with realm and name.
     *
     * @param realm The credential realm.
     * @param name The username.
     * @return The credential, or null if not found.
     */
    public Password get(String realm, String name) {
        return super.get(String.format("%s:%s:", realm, name));
    }

    @Override
    public Password get(Object key) {
        // Make it compatible with the old way (low-efficient)
        if (key instanceof String keyInst && !keyInst.contains(":")) {
            return getByUsername(keyInst);
        }
        return super.get(key);
    }

    /**
     * Remove a credential with realm and name.
     *
     * @param realm The credential realm.
     * @param name The username.
     * @return The removed credential, or null if not found.
     */
    public Password remove(String realm, String name) {
        if(checkForWildcards()){
            throw new IllegalArgumentException("app context must be specified when removing a password.");
        }
        return super.remove(String.format("%s:%s:", realm, name));
    }

    @Override
    public Password remove(String key) {
        if(checkForWildcards()){
            throw new IllegalArgumentException("app context must be specified when removing a password.");
        }
        // Make it compatible with the old way (low-efficient)
        if (!key.contains(":")) {
            Password password = getByUsername(key);
            validate();
            if (password == null) return null;
            password.remove();
            // by invalidating any access to items will get refreshed
            invalidate();
            return password;
        }
        return super.remove(key);
    }

    @Override
    public boolean containsKey(Object key) {
        if (key instanceof String keyInst && !keyInst.contains(":")) {
            return getByUsername(keyInst) != null;
        }
        return super.containsKey(key);
    }

    private Password getByUsername(String name) {
        for (Password password : this.values()) {
            if (password.getUsername().equals(name)) return password;
        }
        return null;
    }

    private boolean checkForWildcards(){
        boolean isWildCard = false;
        if(("-").equals(service.getOwner()) || ("-").equals(service.getApp())){
            isWildCard = true;
        }
        return isWildCard;
    }
}
