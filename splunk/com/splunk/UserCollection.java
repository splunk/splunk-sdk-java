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

import java.util.Map;

/**
 * The {@code UserCollection} class represents a collection of Splunk users who
 * are registered on the current Splunk server.
 */
public class UserCollection extends EntityCollection<User> {

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     */
    UserCollection(Service service) {
        super(service, "authentication/users", User.class);
    }

    /**
     * Constructs an instance of {@code UserCollection}.
     *
     * @param service The connected {@code Service} instance.
     * @param args Arguments to use when you instantiate the entity, such as 
     * "count" and "offset".
     */
    UserCollection(Service service, Args args) {
        super(service, "authentication/users", User.class, args);
    }

    /**
     * Creates a new user entity from a username, password, and role.
     * Usernames must be unique on the system, and are used by the user to log
     * in to Splunk.
     *
     * @param name The username for the new user entity.
     * @param password The password for the new user entity.
     * @param role The role to assign to the new entity.
     * @return The new user entity.
     */
    public User create(String name, String password, String role) {
        return create(name, password, role, null);
    }

    /**
     * Creates a new user entity from a username, password, and array of
     * roles.
     * Usernames must be unique on the system, and are used by the user to log
     * in to Splunk.
     *
     * @param name The username for the new user entity.
     * @param password The password for the new user entity.
     * @param roles Array of roles to assign to the new user entity.
     * @return The new user entity.
     */
    public User create(String name, String password, String[] roles) {
        return create(name, password, roles, null);
    }

    /**
     * Creates a new user entity from a username, password, role, and
     * additional arguments.
     * Usernames must be unique on the system, and are used by the user to log
     * in to Splunk.
     *
     * @param name The username for the new user entity.
     * @param password The password for the new user entity.
     * @param role The role to assign to the new user entity.
     * @param args A map of additional arguments for the new user entity.
     * @return The new user entity.
     */
    public User create(String name, String password, String role, Map args) {
        args = Args.create(args);
        args.put("password", password);
        args.put("roles", role);
        return create(name.toLowerCase(), args);
    }

    /**
     * Creates a new user entity from a username, password, array of
     * roles, and additional arguments.
     * Usernames must be unique on the system, and are used by the user to log
     * in to Splunk.
     *
     * @param name The username for the new user entity.
     * @param password The password for the new user entity.
     * @param roles Array of roles to assign to the new user entity.
     * @param args A map of additional arguments for the new user entity.
     * @return The new user entity.
     */
    public User 
    create(String name, String password, String[] roles, Map args) {
        args = Args.create(args);
        args.put("password", password);
        args.put("roles", roles);
        return create(name.toLowerCase(), args);
    }
}
