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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        for (String index: dclients.list()) {
            DeploymentClient dclient = new DeploymentClient(service, index);
            dclient.get(); // force a read and do nothing with the data
        }

        DeploymentServers dservers =  new DeploymentServers(service);
        for (String index: dservers.list()) {
            DeploymentServer dserver = new DeploymentServer(service, index);
            dserver.get(); // force a read and do nothing with the data
        }

        DeploymentServerclasses dsclasses =  new DeploymentServerclasses(service);
        for (String index: dsclasses.list()) {
            DeploymentServerclass dsclass = new DeploymentServerclass(service, index);
            dsclass.get(); // force a read and do nothing with the data
        }

        DeploymentTenants dtenants =  new DeploymentTenants(service);
        for (String index: dtenants.list()) {
            DeploymentTenant dtenant = new DeploymentTenant(service, index);
            dtenant.get(); // force a read and do nothing with the data
        }

        DistributedPeers dpeers =  new DistributedPeers(service);
        for (String index: dpeers.list()) {
            DistributedPeer dpeer = new DistributedPeer(service, index);
            dpeer.get(); // force a read and do nothing with the data
        }
    }

    @Test public void testCapabilities() throws Exception {

        System.out.println("Testing Capabilities");

        Service service = connect();

        List <String> expected = new ArrayList <String>();
        expected.add("admin_all_objects");
        expected.add("change_authentication");
        expected.add("change_own_password");
        expected.add("delete_by_keyword");
        expected.add("edit_deployment_client");
        expected.add("edit_deployment_server");
        expected.add("edit_dist_peer");
        expected.add("edit_forwarders");
        expected.add("edit_httpauths");
        expected.add("edit_input_defaults");
        expected.add("edit_monitor");
        expected.add("edit_roles");
        expected.add("edit_scripted");
        expected.add("edit_search_server");
        expected.add("edit_server");
        expected.add("edit_splunktcp");
        expected.add("edit_splunktcp_ssl");
        expected.add("edit_tcp");
        expected.add("edit_udp");
        expected.add("edit_user");
        expected.add("edit_web_settings");
        expected.add("get_metadata");
        expected.add("get_typeahead");
        expected.add("indexes_edit");
        expected.add("license_edit");
        expected.add("license_tab");
        expected.add("list_deployment_client");
        expected.add("list_forwarders");
        expected.add("list_httpauths");
        expected.add("list_inputs");
        expected.add("request_remote_tok");
        expected.add("rest_apps_management");
        expected.add("rest_apps_view");
        expected.add("rest_properties_get");
        expected.add("rest_properties_set");
        expected.add("restart_splunkd");
        expected.add("rtsearch");
        expected.add("schedule_search");
        expected.add("search");
        expected.add("use_file_operator");

        Capabilities caps = new Capabilities(service);
        Element element = caps.get();

        // capabilities is a list in the "capabilities" key
        List<String> cap = new ArrayList<String>();
        cap.add("capabilities");
        Map<String,String> map = element.read(cap);
        for (String index: expected) {
            Assert.assertTrue(map.get("capabilities").contains(index));
        }
    }

    @Test public void testConfs() throws Exception {

        System.out.println("Testing Config/Stanza");

        Service service = connect();

        Confs confs = new Confs(service);
        for (String index: confs.list()) {
            Conf conf = new Conf(service, index);
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

        for (String index: indexes.list()) {
            Index idx = new Index(service, index);
            idx.get(); // force a read and do nothing with the data
        }

        if (!indexes.contains("sdk-tests")) {
            indexes.create("sdk-tests");
        }

        Assert.assertTrue(indexes.contains("sdk-tests"));

        List<String> attrs = new ArrayList<String>();
        attrs.add("thawedPath");
        attrs.add("quarantineFutureSecs");
        attrs.add("isInternal");
        attrs.add("maxHotBuckets");
        attrs.add("disabled");
        attrs.add("homePath");
        attrs.add("compressRawdata");
        attrs.add("maxWarmDBCount");
        attrs.add("frozenTimePeriodInSecs");
        attrs.add("memPoolMB");
        attrs.add("maxHotSpanSecs");
        attrs.add("minTime");
        attrs.add("blockSignatureDatabase");
        attrs.add("serviceMetaPeriod");
        attrs.add("coldToFrozenDir");
        attrs.add("quarantinePastSecs");
        attrs.add("maxConcurrentOptimizes");
        attrs.add("maxMetaEntries");
        attrs.add("minRawFileSyncSecs");
        attrs.add("maxMemMB");
        attrs.add("maxTime");
        attrs.add("partialServiceMetaPeriod");
        attrs.add("maxHotIdleSecs");
        attrs.add("coldToFrozenScript");
        attrs.add("thawedPath_expanded");
        attrs.add("coldPath_expanded");
        attrs.add("defaultDatabase");
        attrs.add("throttleCheckPeriod");
        attrs.add("totalEventCount");
        attrs.add("enableRealtimeSearch");
        attrs.add("indexThreads");
        attrs.add("maxDataSize");
        attrs.add("currentDBSizeMB");
        attrs.add("homePath_expanded");
        attrs.add("blockSignSize");
        attrs.add("syncMeta");
        attrs.add("assureUTF8");
        attrs.add("rotatePeriodInSecs");
        attrs.add("sync");
        attrs.add("suppressBannerList");
        attrs.add("rawChunkSizeBytes");
        attrs.add("coldPath");
        attrs.add("maxTotalDataSizeMB");

        for (String index: indexes.list()) {
            Index idx = new Index(service, index);
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
        map = element.read(getme);
        Assert.assertEquals(map.get("disabled"), "1");

        element = index.enable();
        map = element.read(getme);
        Assert.assertEquals(map.get("disabled"), "0");

        element = index.clean();
        map = element.read(getme);
        Assert.assertEquals(map.get("totalEventCount"), "0");

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

    @Test public void testInfo() throws Exception {

        System.out.println("Testing System Information");

        Service service = connect();

        List <String> expected = new ArrayList <String>();
        expected.add("build");
        expected.add("cpu_arch");
        expected.add("guid");
        expected.add("isFree");
        expected.add("isTrial");
        expected.add("licenseKeys");
        expected.add("licenseSignature");
        expected.add("licenseState");
        expected.add("master_guid");
        expected.add("mode");
        expected.add("os_build");
        expected.add("os_name");
        expected.add("os_version");
        expected.add("serverName");
        expected.add("version");

        Info info = new Info(service);
        Map<String,String> map = info.read(expected);
        for (String index: expected) {
            Assert.assertTrue(map.get(index).length() > 0);
        }
    }
}
