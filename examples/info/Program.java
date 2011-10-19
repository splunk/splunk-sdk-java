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

import com.splunk.*;

import java.util.Map;

public class Program extends com.splunk.sdk.Program {
    public static void main(String[] args) {
        Program program = new Program();
        try {
            program.init(args).run();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void run() throws Exception {
        Service service = new Service(this.host, this.port, this.scheme);
        service.login(this.username, this.password);

        Info info =  new Info(service);
        Map<String,String> content = info.get().locate("server-info").content;

        System.out.println("Content Keys:");
        for (String key: content.keySet()) {
            System.out.println("    " + key + ": " + content.get(key));
        }

        Settings settings = new Settings(service);
        content = settings.get().locate("settings").content;

        System.out.println("\nSettings:");
        for (String key: content.keySet()) {
            System.out.println("    " + key + ": " + content.get(key));
        }
    }
}
