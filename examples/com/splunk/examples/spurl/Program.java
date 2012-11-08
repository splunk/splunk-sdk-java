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

package com.splunk.examples.spurl;

import com.splunk.Service;
import com.splunk.ResponseMessage;
import com.splunk.Command;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class Program {
    public static void main(String[] args) {
        try {
            run(args);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    static void run(String[] args) throws IOException {
        Command command = Command.splunk("test").parse(args);
        Service service = Service.connect(command.opts);

        String path = command.args.length > 0 ? command.args[0] : "/";
        ResponseMessage response = service.get(path);

        int status = response.getStatus();
        System.out.println(String.format("=> %d", status));
        if (status != 200) return;

        BufferedReader reader = new BufferedReader(
            new InputStreamReader(response.getContent(), "UTF8"));
        while (true) {
            String line = reader.readLine();
            if (line == null) break;
            System.out.println(line);
        }
    }
}
