package com.splunk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: wcolgate
 * Date: 10/7/11
 * Time: 2:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class Index extends Entity {

    private String localname = null;

    public Index(Service service, String relpath) {
        super(service, "/services/data/indexes/" + relpath);
        localname = relpath;
    }

    public void attach() {

        //UNDONE
        /*
        def attach(self, host=None, source=None, sourcetype=None):
            """Opens a stream for writing events to the index."""
            args = { 'index': self.name }
            if host is not None: args['host'] = host
            if source is not None: args['source'] = source
            if sourcetype is not None: args['sourcetype'] = sourcetype
            path = "receivers/stream?%s" % urlencode(args)

            # Since we need to stream to the index connection, we have to keep
            # the connection open and use the Splunk extension headers to note
            # the input mode
            cn = self.service.connect()
            cn.write("POST %s HTTP/1.1\r\n" % self.service.fullpath(path))
            cn.write("Host: %s:%s\r\n" % (self.service.host, self.service.port))
            cn.write("Accept-Encoding: identity\r\n")
            cn.write("Authorization: %s\r\n" % self.service.token)
            cn.write("X-Splunk-Input-Mode: Streaming\r\n")
            cn.write("\r\n")
            return cn
          */
    }

    public void clean () throws Exception {
        List<String> list = new ArrayList<String>();
        list.add("maxTotalDataSizeMB");
        list.add("frozenTimePeriodInSecs");
        Map<String,String> saved = super.read(list);

        Map<String,String> reset = new HashMap<String, String>();
        reset.put("maxTotalDataSizeMB", "1");
        reset.put("frozenTimePeriodInSecs", "1");
        super.update(reset);
        super.post("/roll-hot-buckets");

        List<String> count = new ArrayList<String>();
        count.add("totalEventCount");
        Map<String,String> result = new HashMap<String, String>();
        while (true) {
            Thread.sleep(1000); // 1000ms (1 second sleep)
            result = super.read(count);
            String value = result.get("totalEventCount");
            if (value.equals("0")) {
                break;
            }
        }
        super.update(saved);
    }

    public void submit() {
        // UNDONE
         /*
    def submit(self, event, host=None, source=None, sourcetype=None):
        """Submits an event to the index via HTTP POST."""
        args = { 'index': self.name }
        if host is not None: args['host'] = host
        if source is not None: args['source'] = source
        if sourcetype is not None: args['sourcetype'] = sourcetype

        # The reason we use service.request directly rather than POST
        # is that we are not sending a POST request encoded using
        # x-www-form-urlencoded (as we do not have a key=value body),
        # because we aren't really sending a "form".
        path = "receivers/simple?%s" % urlencode(args)
        message = { 'method': "POST", 'body': event }
        response = self.service.request(path, message)
          */
    }

    public Element upload(String filename, Map<String, String> args) throws Exception {
        args.put("name", filename);
        args.put("index", localname); // established at class instantiation
        // not a base-relative path, need to reach into the endpoints class to post
        Convert converter = new Convert();
        return converter.convertXMLData(service.post("/services/data/inputs/oneshot", args).getContent());
    }

}
