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

import com.splunk.EntityCollection;
import com.splunk.Service;
import com.splunk.Index;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.Socket;

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

        EntityCollection<Index> indexes = service.getIndexes();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

        for (Index entity: indexes.values()) {
            System.out.println(
                entity.getTitle() +
                " (" + entity.getContent().get("totalEventCount") + ")");
        }

        Index idx = indexes.get("sdk-tests");
        String date = sdf.format(new Date());

        // submit method
        idx.submit(date + " 1");
        idx.submit(date + " 2");
        idx.submit(date + " 3");

        // stream method
        Socket sock = idx.attach();
        OutputStream ostream = sock.getOutputStream();
        DataOutputStream ds = new DataOutputStream(ostream);

        ds.writeBytes(date + " ONE\r\n");
        ds.writeBytes(date + " TWO\r\n");
        ds.writeBytes(date + " THREE\r\n");
        sock.close();
    }
}
