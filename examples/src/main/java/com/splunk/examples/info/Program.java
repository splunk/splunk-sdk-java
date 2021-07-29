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

package com.splunk.examples.info;

import com.splunk.*;

public class Program {
    public static void main(String[] args) {
        Command command = Command.splunk("info").parse(args);
        Service service = Service.connect(command.opts);

        ServiceInfo info = service.getInfo();
        System.out.println("Info:");
        for (String key : info.keySet())
            System.out.println("    " + key + ": " + info.get(key));

        Entity settings = service.getSettings();
        System.out.println("\nSettings:");
        for (String key : settings.keySet())
            System.out.println("    " + key + ": " + settings.get(key));
    }
}
