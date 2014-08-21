/*
 * Copyright 2014 Splunk, Inc.
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

package com.splunk.examples.endpoint_instantiation;

import com.splunk.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

public class Program {
    public static void main(String[] args) {
    	Command command = Command.splunk("info").parse(args);
        Service service = Service.connect(command.opts);
        
        String mySplunkRESTPath = "apps/local";
        
        EntityCollection myCollection = new EntityCollection(service, mySplunkRESTPath);
        LinkedList<Entity> myEntities = (LinkedList<Entity>) myCollection.values();
        
        System.out.println("Found " + myEntities.size() + " Splunk apps:");
        
        for (Entity myEntity : (LinkedList<Entity>) myCollection.values()) {
        	System.out.println("\t" + myEntity.getName());
        }
    }
}