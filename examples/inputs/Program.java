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

import java.util.Date;
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

    public void printActions(Map<String, String> actions) {
        for (Map.Entry entry : actions.entrySet()) {
            System.out.format("action %s => %s\n",
                entry.getKey(), entry.getValue());
        }
    }

    public void printEntity(Entity entity) {
        System.out.println("");
        if (entity == null) {
            System.out.println("null");
            return;
        }
        System.out.format("## %s\n", entity.getName());
        System.out.format("title = %s\n", entity.getTitle());
        System.out.format("path = %s\n", entity.getPath());
        printActions(entity.getActions());
        Map<String, Object> content = entity.getContent();
        if (content != null) {
            for (Map.Entry entry : content.entrySet()) {
                System.out.format("%s = %s\n",
                    entry.getKey(), entry.getValue().toString());
            }
        }
    }

    public void run() throws Exception {
        Service service = new Service(this.host, this.port, this.scheme);
        service.login(this.username, this.password);

        /*

        // UNDONE -- need Inputs class
        EntityCollection inputs = service.getInputs();
        for (Entity entity: ((EntityCollection<Entity>)inputs).values()) {
            System.out.println(entity.getTitle());
        }
        */
    }
}
