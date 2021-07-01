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
     * @param args Collection arguments that specify the number of entities to 
     * return and how to sort them (see {@link CollectionArgs}).
     */
    UserCollection(Service service, Args args) {
        super(service, "authentication/users", User.class, args);
    }

    /**
     * Creates a new Splunk user from a username, password, and role.
     * Usernames must be unique on the system, and are used by the user to log
     * in to Splunk.
     *
     * @param name The username for the new user.
     * @param password The password for the new user.
     * @param role The role to assign to the new user.
     * @return The new user.
     */
    public User create(String name, String password, String role) {
        return create(name, password, role, null);
    }

    /**
     * Creates a new Splunk user from a username, password, and array of
     * roles.
     * Usernames must be unique on the system, and are used by the user to log
     * in to Splunk.
     *
     * @param name The username for the new user.
     * @param password The password for the new user.
     * @param roles Array of roles to assign to the new user.
     * @return The new user.
     */
    public User create(String name, String password, String[] roles) {
        return create(name, password, roles, null);
    }

    /**
     * Creates a new Splunk user from a username, password, role, and
     * additional arguments.
     * Usernames must be unique on the system, and are used by the user to log
     * in to Splunk.
     *
     * @param name The username for the new user.
     * @param password The password for the new user.
     * @param role The role to assign to the new user.
     * @param args A map of additional arguments. For a list of available 
     * parameters, see 
     * <a href="http://dev.splunk.com/view/SP-CAAAEJ7#userauthparams" 
     * target="_blank">User authentication parameters</a> on 
     * <a href="http://dev.splunk.com/view/SP-CAAAEJ7" 
     * target="_blank">dev.splunk.com</a>.
     * @return The new user.
     */
    public User create(String name, String password, String role, Map args) {
        args = Args.create(args);
        args.put("password", password);
        args.put("roles", role);
        return create(name.toLowerCase(), args);
    }

    /**
     * Creates a new Splunk user from a username, password, array of
     * roles, and additional arguments.
     * Usernames must be unique on the system, and are used by the user to log
     * in to Splunk.
     *
     * @param name The username for the new user.
     * @param password The password for the new user.
     * @param roles Array of roles to assign to the new user.
     * @param args A map of additional arguments. For a list of available 
     * parameters, see 
     * <a href="http://dev.splunk.com/view/SP-CAAAEJ7#userauthparams" 
     * target="_blank">User authentication parameters</a> on 
     * <a href="http://dev.splunk.com/view/SP-CAAAEJ7" 
     * target="_blank">dev.splunk.com</a> .
     * @return The new user.
     */
    public User 
    create(String name, String password, String[] roles, Map args) {
        args = Args.create(args);
        args.put("password", password);
        args.put("roles", roles);
        return create(name.toLowerCase(), args);
    }
}
