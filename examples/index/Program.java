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

import com.splunk.Service;
import com.splunk.Indexes;
import com.splunk.Index;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import java.util.Date;
import java.text.SimpleDateFormat;


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

        Indexes indexes =  new Indexes(service);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

        for (String index: indexes.get().list()) {
            Index idx = new Index(service, index);
            List<String> item = new ArrayList<String>();
            item.add("totalEventCount");
            Map<String,String> data = idx.read(item);
            System.out.println(index+" ("+data.get("totalEventCount")+")");
        }

        Index idx = new Index(service, "sdk-tests");
        String date = sdf.format(new Date());

        // stream method
        idx.attach();
        idx.stream(date + " ONE");
        idx.stream(date + " TWO");
        idx.stream(date + " THREE");
        idx.detach();
    }
}
