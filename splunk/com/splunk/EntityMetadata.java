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
 * The {@code EntityMetadata} class provides access to the metadata properties
 * of a corresponding entity. Use {@code Entity.getMetadata} to obtain an 
 * instance of this class.
 */
public class EntityMetadata {
    private Entity entity;

    /**
     * Class constructor.
     *
     * @param entity This entity.
     */
    EntityMetadata(Entity entity) {
        this.entity = entity;
    }

    /**
     * Indicates whether this entity's permission can be changed.
     *
     * @return {@code true} if this entity's permission can be changed,
     * {@code false} if not.
     */
    public boolean canChangePermissions() {
        return getEaiAcl().getBoolean("can_change_perms", false);
    }

    /**
     * Indicates whether this entity can be shared via an app.
     *
     * @return {@code true} if this entity can be shared via an app,
     * {@code false} if not.
     */
    public boolean canShareApp() {
        return getEaiAcl().getBoolean("can_share_app", false);
    }

    /**
     * Indicates whether the entity can be shared globally.
     *
     * @return {@code true} if this entity can be shared globally,
     * {@code false} if not.
     */
    public boolean canShareGlobal() {
        return getEaiAcl().getBoolean("can_share_global", false);
    }

    /**
     * Indicates whether the entity can be shared to a specific user.
     *
     * @return {@code true} if this entity can be shared to a specific user,
     * {@code false} if not.
     */
    public boolean canShareUser() {
        return getEaiAcl().getBoolean("can_share_user", false);
    }

    /**
     * Indicates whether this entity can be modified.
     *
     * @return {@code true} if this entity can be modified,
     * {@code false} if not.
     */
    public boolean canWrite() {
        return getEaiAcl().getBoolean("can_write", false);
    }

    /**
     * Returns the app context of this entity.
     *
     * @return The app context of this entity.
     */
    public String getApp() {
        return getEaiAcl().getString("app", "system");
    }

    /**
     * Returns a record containing all of the metadata information 
     * for this entity.
     *
     * @return The record containing the metadata information.
     */
    Record getEaiAcl() {
        return (Record)entity.validate().get("eai:acl");
    }

    /**
     * Returns the username of the entity owner.
     *
     * @return The entity owner's username.
     */
    public String getOwner() {
        return getEaiAcl().getString("owner");
    }

    /**
     * Returns this entity's permissions, which represent an
     * allowable inclusive action:list-of-roles map.
     *
     * @return This entity's permissions map.
     */
    public Record getPermissions() {
        return getEaiAcl().getValue("perms", null);
    }

    /**
     * Returns how this entity is shared (app, global, and/or user).
     *
     * @return Values that indicate how this entity is shared.
     */
    public String getSharing() {
        return getEaiAcl().getString("sharing");
    }

    /**
     * Indicates whether this entity can be modified.
     *
     * @return {@code true} if this entity can be modified,
     * {@code false} if not.
     */
    public boolean isModifiable() {
        return getEaiAcl().getBoolean("modifiable", false);
    }
}
