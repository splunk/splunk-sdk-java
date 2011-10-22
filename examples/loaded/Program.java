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

import com.splunk.atom.*;
import com.splunk.http.ResponseMessage;

import java.io.IOException;
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

    public Service connect() throws IOException {
        Service service = new Service(this.host, this.port, this.scheme);
        service.login(this.username, this.password);
        return service;
    }

    public void printActions(Map<String, String> actions) {
        for (Map.Entry entry : actions.entrySet()) {
            System.out.format("* %s => %s\n", 
                entry.getKey(), entry.getValue());
        }
    }

    public void printEntity(Entity entity) {
        System.out.format("## %s\n", entity.getPath());
        System.out.format("title = %s\n", entity.getTitle());
        printActions(entity.getActions());
        Map<String, Object> content = entity.getContent();
        if (content != null) {
            for (Map.Entry entry : content.entrySet()) {
                System.out.format("%s = %s\n",
                    entry.getKey(), entry.getValue().toString());
            }
        }
        System.out.println("");
    }

    public void printEntities(EntityCollection entities) {
        System.out.format("# %s\n", entities.getPath());
        printActions(entities.getActions());
        for (Entity entity : entities) 
            printEntity(entity);
    }

    public void run() throws Exception {
        Service service = connect();

        EntityCollection entities;
        
        entities = service.getApplications();
        printEntities(entities);

        entities = service.getEventTypes();
        printEntities(entities);

        entities = service.getIndexes();
        printEntities(entities);

        entities = service.getJobs();
        printEntities(entities);

        entities = service.getLoggers();
        printEntities(entities);

        entities = service.getRoles();
        printEntities(entities);

        entities = service.getSearches();
        printEntities(entities);

        entities = service.getUsers();
        printEntities(entities);
    }
}

