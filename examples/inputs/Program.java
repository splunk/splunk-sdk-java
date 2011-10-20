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

        Inputs allInputs = new Inputs(service);
        Element all = allInputs.get();

        for (Entry entry: all.entry) {
            Input baseInput = new Input(service, entry.title);
            Element element = baseInput.get();

            System.out.println(entry.title + ":");
            for (Entry base: element.entry) {
                System.out.print("  " + base.title + " --> " +
                        element.locateComplete(base.title).content);

                System.out.println();
                try {
                    Input subInput = new Input(service,
                                                entry.title,
                                                base.title);
                    Element subele = subInput.get();
                    for (Entry sub: subele.entry) {
                        System.out.println("    :"
                                + sub.title + " --> " + sub.content);
                    }
                } catch (Exception e) {
                    // ignore
                }
            }
        }
    }
}
