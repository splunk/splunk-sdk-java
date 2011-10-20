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

package com.splunk.sdk.tests.com.splunk;

import java.io.IOException;
import java.util.*;

import com.splunk.Service;
import junit.framework.TestCase;
import org.junit.*;

import junit.framework.Assert;

import com.splunk.*;
import com.splunk.sdk.Program;

public class ClientTest extends TestCase {
    Program program = new Program();

    //public ServiceTest() {}

    Service connect() throws IOException {
        return new Service(
            program.host, program.port, program.scheme)
                .login(program.username, program.password);
    }

    @Before public void setUp() {
        this.program.init(); // Pick up .splunkrc settings
    }

    // Nota Bene: deleting an app, then creating one requires a splunk reboot in between.
    @Test public void testApps() throws Exception {
/*
        System.out.println("Testing Applications");

        Service service = connect();

        Apps apps = new Apps(service);

        if (apps.contains("sdk-tests")) {
            apps.delete("sdk-tests");
        }
        Assert.assertEquals(false, apps.contains("sdk-tests"));

        apps.create("sdk-tests");
        Assert.assertEquals(true, apps.contains("sdk-tests"));

        App app = new App(service, "sdk-tests");
        Element element = app.get();
        List<String> getme = new ArrayList<String>();
        getme.add("author");
        Map<String, String> map = app.read(getme);

        Assert.assertFalse(map.get("author").equals("Splunk"));

        map.put("author", "Splunk");
        app.update(map);

        Assert.assertTrue(map.get("author").equals("Splunk"));

        apps.delete("sdk-tests");
        Assert.assertEquals(false, apps.contains("sdk-tests"));
*/
    }

    @Test public void testDeployments() throws Exception {

        System.out.println("Testing Deployments");

        Service service = connect();

        DeploymentClients dclients =  new DeploymentClients(service);
        for (String name: dclients.list()) {
            DeploymentClient dclient = new DeploymentClient(service, name);
            dclient.get(); // force a read and do nothing with the data
        }

        DeploymentServers dservers =  new DeploymentServers(service);
        for (String name: dservers.list()) {
            DeploymentServer dserver = new DeploymentServer(service, name);
            dserver.get(); // force a read and do nothing with the data
        }

        DeploymentServerclasses dsclasses = new DeploymentServerclasses(service);
        for (String name: dsclasses.list()) {
            DeploymentServerclass dsclass = new DeploymentServerclass(service,
                                                                      name);
            dsclass.get(); // force a read and do nothing with the data
        }

        DeploymentTenants dtenants =  new DeploymentTenants(service);
        for (String name: dtenants.list()) {
            DeploymentTenant dtenant = new DeploymentTenant(service, name);
            dtenant.get(); // force a read and do nothing with the data
        }

        DistributedPeers dpeers =  new DistributedPeers(service);
        for (String name: dpeers.list()) {
            DistributedPeer dpeer = new DistributedPeer(service, name);
            dpeer.get(); // force a read and do nothing with the data
        }
    }

    @Test public void testCapabilities() throws Exception {

        System.out.println("Testing Capabilities");

        Service service = connect();

        List <String> expected = Arrays.asList(
                "admin_all_objects", "change_authentication",
                "change_own_password", "delete_by_keyword",
                "edit_deployment_client", "edit_deployment_server",
                "edit_dist_peer", "edit_forwarders", "edit_httpauths",
                "edit_input_defaults", "edit_monitor", "edit_roles",
                "edit_scripted", "edit_search_server", "edit_server",
                "edit_splunktcp", "edit_splunktcp_ssl", "edit_tcp", "edit_udp",
                "edit_user", "edit_web_settings", "get_metadata",
                "get_typeahead", "indexes_edit", "license_edit", "license_tab",
                "list_deployment_client", "list_forwarders", "list_httpauths",
                "list_inputs", "request_remote_tok", "rest_apps_management",
                "rest_apps_view", "rest_properties_get", "rest_properties_set",
                "restart_splunkd", "rtsearch", "schedule_search", "search",
                "use_file_operator");

        Capabilities caps = new Capabilities(service);
        Element element = caps.get();

        // capabilities is a list in the "capabilities" key
        List<String> cap = new ArrayList<String>();
        cap.add("capabilities");
        Map<String,String> map = element.read(cap);
        for (String name: expected) {
            Assert.assertTrue(map.get("capabilities").contains(name));
        }
    }

    @Test public void testConfs() throws Exception {

        System.out.println("Testing Config/Stanza");

        Service service = connect();

        Confs confs = new Confs(service);
        for (String name: confs.list()) {
            Conf conf = new Conf(service, name);
            conf.get(); // force a read and do nothing with the data
        }

        Assert.assertTrue(confs.contains("props"));
        Conf props = new Conf(service, "props");
        Element stanza = props.create("sdk-tests");
        Assert.assertTrue(props.contains("sdk-tests"));

        Map<String,String> map;
        List<String> getme = new ArrayList<String>();
        getme.add("name");
        getme.add("maxDist");
        map = stanza.read(getme);
        Assert.assertTrue(map.containsKey("maxDist"));

        int value = Integer.parseInt(map.get("maxDist"));
        map.put("maxDist", Integer.toString(value+1));
        stanza = props.update("sdk-tests", map);
        map = stanza.read(getme);
        int value2 = Integer.parseInt(map.get("maxDist"));
        Assert.assertEquals(value+1, value2);

        props.delete("sdk-tests");
        Assert.assertFalse(props.contains("sdk-tests"));
    }

    @Test public void testIndexes() throws Exception {

        System.out.println("Testing Indexes");

        Service service = connect();

        Indexes indexes = new Indexes(service);

        for (String name: indexes.list()) {
            Index idx = new Index(service, name);
            idx.get(); // force a read and do nothing with the data
        }

        if (!indexes.contains("sdk-tests")) {
            indexes.create("sdk-tests");
        }

        Assert.assertTrue(indexes.contains("sdk-tests"));

        List<String> attrs = Arrays.asList(
            "thawedPath", "quarantineFutureSecs", "isInternal", "maxHotBuckets",
            "disabled", "homePath", "compressRawdata", "maxWarmDBCount",
            "frozenTimePeriodInSecs", "memPoolMB", "maxHotSpanSecs", "minTime",
            "blockSignatureDatabase", "serviceMetaPeriod", "coldToFrozenDir",
            "quarantinePastSecs", "maxConcurrentOptimizes", "maxMetaEntries",
            "minRawFileSyncSecs", "maxMemMB", "maxTime",
            "partialServiceMetaPeriod", "maxHotIdleSecs", "coldToFrozenScript",
            "thawedPath_expanded", "coldPath_expanded", "defaultDatabase",
            "throttleCheckPeriod", "totalEventCount", "enableRealtimeSearch",
            "indexThreads", "maxDataSize", "currentDBSizeMB",
            "homePath_expanded", "blockSignSize", "syncMeta", "assureUTF8",
            "rotatePeriodInSecs", "sync", "suppressBannerList",
            "rawChunkSizeBytes", "coldPath", "maxTotalDataSizeMB");

        for (String name: indexes.list()) {
            Index idx = new Index(service, name);
            Element element = idx.get();
            Map<String,String> map = element.read(attrs);
            for (String attr: attrs) {
                Assert.assertTrue(map.containsKey(attr));
            }
        }

        Map<String,String> map;
        Index index = new Index(service, "sdk-tests");
        Element element;
        List<String> getme = new ArrayList<String>();
        getme.add("disabled");
        getme.add("totalEventCount");

        element = index.disable();
        Assert.assertEquals(element.read(getme).get("disabled"), "1");

        element = index.enable();
        Assert.assertEquals(element.read(getme).get("disabled"), "0");

        element = index.clean();
        Assert.assertEquals(element.read(getme).get("totalEventCount"), "0");

/*
        UNDONE: attach and submit

        cn = index.attach()
        cn.write("Hello World!")
        cn.close()
        wait_event_count(index, '1', 30)
        self.assertEqual(index['totalEventCount'], '1')

        index.submit("Hello again!!")
        wait_event_count(index, '2', 30)
        self.assertEqual(index['totalEventCount'], '2')

        # test must run on machine where splunkd runs,
        # otherwise an failure is expected
        testpath = path.dirname(path.abspath(__file__))
        index.upload(path.join(testpath, "testfile.txt"))
        wait_event_count(index, '3', 30)
        self.assertEqual(index['totalEventCount'], '3')

        index.clean()
        self.assertEqual(index['totalEventCount'], '0')
 */

    }


    @Test public void testIndexMetadata() throws Exception {

        System.out.println("Testing Index metadata");

        Service service = connect();

        Indexes indexes = new Indexes(service);

        List<String> getme = new ArrayList<String>();
        getme.add("eai:acl");
        getme.add("eai:attributes");
        Map<String,String> map = indexes.get().read(getme);
        Assert.assertTrue(map.size() > 0);

        for (String name: indexes.list()) {
            Entity ent = new Index(service, name);
            map = ent.readmeta();
            Assert.assertTrue(map.size() > 0);
        }
    }

    @Test public void testInfo() throws Exception {

        System.out.println("Testing System Information");

        Service service = connect();

        List <String> expected = Arrays.asList(
            "build", "cpu_arch", "guid", "isFree", "isTrial", "licenseKeys",
            "licenseSignature", "licenseState", "master_guid", "mode",
            "os_build", "os_name", "os_version", "serverName", "version");

        Info info = new Info(service);
        Map<String,String> map = info.read(expected);
        for (String name: expected) {
            Assert.assertTrue(map.get(name).length() > 0);
        }
    }

    @Test public void testInputs() throws Exception {

        System.out.println("Testing Inputs");

        Service service = connect();

        Inputs allInputs = new Inputs(service);
        Input tcpInput;
        Input nnnnInput;

        List<String> getme = new ArrayList<String>();
        getme.add("disabled");
        getme.add("index");

        for (String name: allInputs.list()) {
            Input input = new Input(service, name);
            Element element = input.get();
            for (Entry entry: element.entry) {
                if (entry.content.size() > 0) {
                    for (String attr: getme) {
                        Assert.assertTrue(entry.content.containsKey(attr));
                    }
                }
            }
        }

        tcpInput = new Input(service, allInputs.kindpath("tcp"));
        Element element = tcpInput.get();

        if (element.list().contains("9999")) {
            element = allInputs.delete("tcp", "9999");
        }
        Assert.assertFalse(element.list().contains("9999"));

        Map<String,String> map = new HashMap<String, String>();
        map.put("host", "sdk-test");
        allInputs.create("tcp", "9999", map);
        element = tcpInput.get();
        Assert.assertTrue(element.list().contains("9999"));

        Entry ent = element.locate("9999");
        getme.clear();
        getme.add("host");
        map = ent.read(getme);
        Assert.assertTrue(map.get("host").equals("sdk-test"));

        nnnnInput = new Input(service, allInputs.kindpath("tcp") + "/9999");
        map.clear();
        map.put("host", "foo");
        map.put("sourcetype", "bar");
        element = nnnnInput.update(map);

        getme.add("sourcetype");
        ent = element.locate("9999");
        map = ent.read(getme);
        Assert.assertTrue(map.get("host").equals("foo"));
        Assert.assertTrue(map.get("sourcetype").equals("bar"));

        allInputs.delete("tcp", "9999");
        element = tcpInput.get();
        Assert.assertFalse(element.list().contains("9999"));

        /*
        UNDONE:
        for kind in inputs.kinds:
            for key in inputs.list(kind):
                input = inputs[key]
                self.assertEqual(input.kind, kind)

         */
    }

    @Test public void testLoggers() throws Exception {

        System.out.println("Testing Loggers");

        Service service = connect();

        List <String> expected = Arrays.asList(
                "INFO", "WARN", "ERROR", "DEBUG", "CRIT");

        Loggers loggers = new Loggers(service);
        List<String> getme = new ArrayList<String>();
        getme.add("level");

        for (String name: loggers.list()) {
            Logger logger = new Logger(service, name);
            Element element = logger.get();

            Map<String,String> levels = element.read(getme);
            Assert.assertTrue(expected.contains(levels.get("level")));
        }

        Assert.assertTrue(loggers.list().contains("AuditLogger"));
        Logger logger = new Logger(service, "AuditLogger");

        Map<String,String> saved = logger.get().read(getme);

        for (String level: expected) {
            Map<String,String> update = new HashMap<String,String>();
            update.clear();
            update.put("level", level);
            logger.update(update);
            Map<String,String> updated = logger.get().read(getme);
            Assert.assertEquals(level, updated.get("level"));
        }

        logger.update(saved);
        Assert.assertEquals(saved.get("level"), logger
                                        .get()
                                        .read(getme)
                                        .get("level"));
    }


    @Test public void testMessages() throws Exception {

        System.out.println("Testing Messages");

        Service service = connect();

        Messages messages = new Messages(service);

        if (messages.list().contains("sdk-test-message1")) {
            messages.delete("sdk-test-message1");
        }

        if (messages.list().contains("sdk-test-message2")) {
            messages.delete("sdk-test-message2");
        }

        Assert.assertFalse(messages.list().contains("sdk-test-message1"));
        Assert.assertFalse(messages.list().contains("sdk-test-message2"));

        //UNDONE: message should be placed into "value" put appears to be placed
        // into key-name 'sdk-test-message1'
        Map<String,String> args1 = new HashMap<String, String>();
        args1.put("value", "hello.");
        messages.create("sdk-test-message1", args1);
        Assert.assertTrue(messages.list().contains("sdk-test-message1"));
        Message message1 = new Message(service, "sdk-test-message1");

        //UNDONE: message should be placed into "value" put appears to be placed
        // into key-name 'sdk-test-message2'
        Map<String,String> args2 = new HashMap<String, String>();
        args2.put("value", "world.");
        messages.create("sdk-test-message2", args2);
        Assert.assertTrue(messages.list().contains("sdk-test-message2"));
        Message message2 = new Message(service, "sdk-test-message2");

        messages.delete("sdk-test-message1");
        messages.delete("sdk-test-message2");
        Assert.assertFalse(messages.list().contains("sdk-test-message1"));
        Assert.assertFalse(messages.list().contains("sdk-test-message2"));
    }
}
