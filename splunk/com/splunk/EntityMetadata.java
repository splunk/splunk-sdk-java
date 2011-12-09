/*
 * Copyright 2011 Splunk, Inc.
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

// A class that provides access to an entities metadata. Instances of this
// class are obtained via Entity.getMetadata.

/**
 * Representation of Entity and its subclasses meta data fields.
 */
public class EntityMetadata {
    private Entity entity;

    /**
     * Clas constructor.
     *
     * @param entity This entity.
     */
    EntityMetadata(Entity entity) {
        this.entity = entity;
    }

    /**
     * Returns whether or not this entity's permission can be changed.
     *
     * @return Whether or not this entity's permission can be changed.
     */
    public boolean canChangePermissions() {
        return getEaiAcl().getBoolean("can_change_perms", false);
    }

    /**
     * Returns whether or not the resource can be shared via an app.
     *
     * @return Whether or not the resource can be shared via an app.
     */
    public boolean canShareApp() {
        return getEaiAcl().getBoolean("can_share_app", false);
    }

    /**
     * Returns whether or not the resource can be shared globally.
     *
     * @return Whether or not the resource can be shared globally.
     */
    public boolean canShareGlobal() {
        return getEaiAcl().getBoolean("can_share_global", false);
    }

    /**
     * Returns whether or not the resource can be shared to a specific user.
     *
     * @return Whether ot nor the resource can be shared to a specific user.
     */
    public boolean canShareUser() {
        return getEaiAcl().getBoolean("can_share_user", false);
    }

    /**
     * Returns whether or not this entity can be modified.
     *
     * @return Whether or not this entity can be modified.
     */
    public boolean canWrite() {
        return getEaiAcl().getBoolean("can_write", false);
    }

    /**
     * Returns the app context of this resource.
     *
     * @return The app context of this resource.
     */
    public String getApp() {
        return getEaiAcl().getString("app", "system");
    }

    /**
     * Returns the record containing all the metadata information.
     *
     * @return The record containing all the metadata information.
     */
    Record getEaiAcl() {
        return (Record)entity.validate().get("eai:acl");
    }

    /**
     * Returns the resource owner's username.
     *
     * @return The resource owner's username.
     */
    public String getOwner() {
        return getEaiAcl().getString("owner");
    }

    // Returns the entity's permissions. The permissions are represented as
    // a Map that maps action to a list of roles that can perform the action.

    /**
     * Returns this entity's permissions. These permissions represent an
     * allowable inclusive action:list-of-roles map.
     *
     * @return This entity's permissions map.
     */
    public Record getPermissions() {
        return getEaiAcl().<Record>getValue("perms", null);
    }

    /**
     * Returns how this resource is shared. Sharing values are from the list
     * app, global and user.
     *
     * @return How this resource is shared.
     */
    public String getSharing() {
        return getEaiAcl().getString("sharing");
    }

    /**
     * Returns whether or not this entity can be modified.
     *
     * @return Whether or not this entity can be modified.
     */
    public boolean isModifiable() {
        return getEaiAcl().getBoolean("modifiable", false);
    }
}
