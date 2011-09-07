import com.ning.http.client.*;
import com.splunk.asyncsdk.Binding;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Sample
 * version 1.0
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


public class async_sample {

    /**
     * Creates a new instance of a sample app that post's and get's to
     * Splunk's RESTful API
     */

    public async_sample() {
    }

    //sample endpoints for GETs
    private final static List<String> getEndpoints = Arrays.asList(
            "/services/apps",
            "/services/server/logger/AdminHandler:AuthenticationHandler",
            "/services",
            "/services/alerts",
            "/services/apps",
            "/services/authentication",
            "/services/authorization",
            "/services/data",
            "/services/deployment",
            "/services/licenser",
            "/services/messages",
            "/services/configs",
            "/services/saved",
            "/services/scheduled",
            "/services/search",
            "/services/server",
            "/services/streams",
            "/services/server/control",
            "/services/server/control/restart",
            "/services/server/info",
            "/services/server/logger",
            "/services/server/settings",

/*
 * N.B.: some of these endpoints resets the open SSL connection...
 *

            "/services/server/settings/settings",
            "/services/server/logger/AdminHandler:AuthenticationHandler",
            "/services/server/logger/AdminHandler:DeprecatedLicenseHandler",
            "/services/server/logger/AdminHandler:DeprecatedLicenseRevertHandler",
            "/services/server/logger/AdminHandler:DirectoryService",
            "/services/server/logger/AdminHandler:DistributedSearchHandler",
            "/services/server/logger/AdminHandler:DMSummaryIndexes",
            "/services/server/logger/AdminHandler:Exec",
            "/services/server/logger/AdminHandler:ExperimentalAdminHandler",
            "/services/server/logger/AdminHandler:FIFO",
            "/services/server/logger/AdminHandler:Logger",
            "/services/server/logger/AdminHandler:MetaHandler",
            "/services/server/logger/AdminHandler:Monitor",
            "/services/server/logger/AdminHandler:PersistMessages",
            "/services/server/logger/AdminHandler:ServerControl",
            "/services/server/logger/AdminHandler:Settings",
            "/services/server/logger/AdminHandler:SmallHandlers",
            "/services/server/logger/AdminHandler:Sourcetype",
            "/services/server/logger/AdminHandler:SyslogOut",
            "/services/server/logger/AdminHandler:TCP",
            "/services/server/logger/AdminHandler:TCPOut",
            "/services/server/logger/AdminHandler:AUDP",
            "/services/server/logger/AdminManager",
            "/services/server/logger/AggregatorMiningProcessor",
            "/services/server/logger/AlertActionsHandler",
            "/services/server/logger/AlertsManager",
            "/services/server/logger/AnalysisProcessor",
            "/services/server/logger/AnomalyProcessor",
            "/services/server/logger/AppendProcessor",
            "/services/server/logger/Application",
            "/services/server/logger/ApplicationManager",
            "/services/server/info/server-info",
            "/services/search/commands",
            "/services/search/distributed",
            "/services/search/fields",
            "/services/search/fields/_reload",
            "/services/search/jobs",
            "/services/search/tags",
            "/services/search/fields/_indextime",
            "/services/search/fields/_sourcetype",
            "/services/search/fields/date_hour",
            "/services/search/fields/date_mday",
            "/services/search/fields/date_minute",
            "/services/search/fields/date_month",
            "/services/search/fields/date_second",
            "/services/search/fields/date_wday",
            "/services/search/fields/date_year",
            "/services/search/fields/date_zone",
            "/services/search/fields/default",
            "/services/search/fields/evtlog_account",
            "/services/search/fields/evtlog_category",
            "/services/search/fields/evtlog_domain",
            "/services/search/fields/evtlog_id",
            "/services/search/fields/evtlog_severity",
            "/services/search/fields/evtlog_sid",
            "/services/search/fields/evtlog_sid_type",
            "/services/search/fields/host",
            "/services/search/fields/index",
            "/services/search/fields/linecount",
            "/services/search/fields/punct",
            "/services/search/fields/source",
            "/services/search/fields/sourcetype",
            "/services/search/fields/splunk_server",
            "/services/search/fields/timeendpos",
            "/services/search/fields/timestartpos",
            "/services/search/distributed/config",
            "/services/search/distributed/peers",
            "/services/search/distributed/peers/_new",
            "/servicesNS/admin/search/search/distributed/peers/_new",
            "/services/search/distributed/config/distributedSearch",
            "/services/scheduled/views",
            "/services/scheduled/views/_reload",
            "/services/saved/eventtypes",
            "/services/saved/eventtypes/_new",
            "/services/saved/eventtypes/_reload",
            "/services/saved/searches",
            "/services/saved/searches/_new",
            "/services/saved/searches/_reload",
            "/servicesNS/admin/search/saved/searches/_new",
            "/servicesNS/admin/search/saved/searches/_reload",
            "/servicesNS/admin/search/saved/searches/report1",
            "/servicesNS/admin/search/saved/searches/report1/history",
            "/servicesNS/admin/search/saved/eventtypes/_new",
            "/servicesNS/admin/search/saved/eventtypes/_reload",
            "/services/configs/deploymentclient",
            "/services/configs/deploymentclient/_reload",
            "/services/configs/inputs",
            "/services/configs/inputs/_reload",
            "/services/configs/wmi",
            "/services/configs/wmi/_reload",
            "/services/messages/_new",
            "/servicesNS/admin/search/messages/_new",
            "/services/licenser/groups",
            "/services/licenser/licenses",
            "/services/licenser/licenses/_new",
            "/services/licenser/localslave",
            "/services/licenser/messages",
            "/services/licenser/pools",
            "/services/licenser/pools/_new",
            "/services/licenser/pools/_reload",
            "/services/licenser/slaves",
            "/services/licenser/stacks",
            "/services/licenser/stacks/enterprise",
            "/services/licenser/stacks/forwarder",
            "/services/licenser/stacks/free",
            "/servicesNS/admin/search/licenser/pools/_new",
            "/servicesNS/admin/search/licenser/pools/_reload",
            "/services/licenser/localslave/license",
            "/servicesNS/admin/search/licenser/licenses/_new",
            "/services/licenser/licenses/1F7D9386159D3B6840B5E1112C4FBD6A25D76899C58A837E854926A07DAED29B",
            "/services/licenser/licenses/FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFD",
            "/services/licenser/licenses/FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF",
            "/services/licenser/groups/Enterprise",
            "/services/licenser/groups/Forwarder",
            "/services/licenser/groups/Free",
            "/services/deployment/client",
            "/services/deployment/server",
            "/services/deployment/server/_reload",
            "/services/deployment/serverclass",
            "/services/deployment/serverclass/_new",
            "/services/deployment/serverclass_status",
            "/services/deployment/serverclass_status/_new",
            "/services/deployment/tenants",
            "/services/deployment/tenants/_reload",
            "/servicesNS/admin/search/deployment/serverclass_status/_new",
            "/servicesNS/admin/search/deployment/serverclass/_new",
            "/services/data/commands",
            "/services/data/commands/_reload",
            "/services/data/indexes",
            "/services/data/indexes/_new",
            "/services/data/indexes/_reload",
            "/services/data/inputs",
            "/services/data/lookup-table-files",
            "/services/data/lookup-table-files/_new",
            "/services/data/lookup-table-files/_reload",
            "/services/data/outputs",
            "/services/data/props",
            "/services/data/transforms",
            "/services/data/ui",
            "/services/data/user-prefs",
            "/servicesNS/admin/user-prefs/data/user-prefs/general",
            "/services/data/ui/manager",
            "/services/data/ui/manager/_new",
            "/services/data/ui/manager/_reload",
            "/services/data/ui/nav",
            "/services/data/ui/nav/_new",
            "/services/data/ui/nav/_reload",
            "/services/data/ui/quickstart",
            "/services/data/ui/quickstart/_new",
            "/services/data/ui/quickstart/_reload",
            "/services/data/ui/times",
            "/services/data/ui/times/_new",
            "/services/data/ui/times/_reload",
            "/services/data/ui/views",
            "/services/data/ui/views/_new",
            "/services/data/ui/views/_reload",
            "/services/data/ui/viewstates",
            "/services/data/ui/viewstates/_new",
            "/services/data/ui/viewstates/_reload",
            "/services/data/ui/workflow-actions",
            "/services/data/ui/workflow-actions/_new",
            "/services/data/ui/workflow-actions/_reload",
            "/servicesNS/admin/search/data/ui/workflow-actions/_new",
            "/servicesNS/admin/search/data/ui/workflow-actions/_reload",
            "/servicesNS/admin/search/data/ui/viewstates/_new",
            "/servicesNS/admin/search/data/ui/viewstates/_reload",
            "/servicesNS/admin/search/data/ui/viewstates/flashtimeline%3A_current",
            "/servicesNS/admin/search/data/ui/viewstates/report_builder_format_report%3A_current",
            "/servicesNS/admin/search/data/ui/views/_new",
            "/servicesNS/admin/search/data/ui/views/_reload",
            "/servicesNS/admin/search/data/ui/times/_new",
            "/servicesNS/admin/search/data/ui/times/_reload",
            "/servicesNS/admin/search/data/ui/quickstart/_new",
            "/servicesNS/admin/search/data/ui/quickstart/_reload",
            "/servicesNS/admin/search/data/ui/nav/_new",
            "/servicesNS/admin/search/data/ui/nav/_reload",
            "/servicesNS/admin/search/data/ui/manager/_new",
            "/servicesNS/admin/search/data/ui/manager/_reload",
            "/services/data/transforms/extractions",
            "/services/data/transforms/extractions/_new",
            "/services/data/transforms/extractions/_reload",
            "/services/data/transforms/lookups",
            "/services/data/transforms/lookups/_new",
            "/services/data/transforms/lookups/_reload",
            "/servicesNS/admin/search/data/transforms/lookups/_new",
            "/servicesNS/admin/search/data/transforms/lookups/_reload",
            "/servicesNS/admin/search/data/transforms/extractions/_new",
            "/servicesNS/admin/search/data/transforms/extractions/_reload",
            "/services/data/props/extractions",
            "/services/data/props/extractions/_new",
            "/services/data/props/fieldaliases",
            "/services/data/props/fieldaliases/_new",
            "/services/data/props/lookups",
            "/services/data/props/lookups/_new",
            "/services/data/props/sourcetype-rename",
            "/services/data/props/sourcetype-rename/_new",
            "/servicesNS/admin/search/data/props/sourcetype-rename/_new",
            "/servicesNS/admin/search/data/props/lookups/_new",
            "/servicesNS/admin/search/data/props/fieldaliases/_new",
            "/servicesNS/admin/search/data/props/extractions/_new",
            "/services/data/outputs/tcp",
            "/services/data/outputs/tcp/default",
            "/services/data/outputs/tcp/default/_new",
            "/services/data/outputs/tcp/default/_reload",
            "/services/data/outputs/tcp/group",
            "/services/data/outputs/tcp/group/_new",
            "/services/data/outputs/tcp/group/_reload",
            "/services/data/outputs/tcp/server",
            "/services/data/outputs/tcp/server/_new",
            "/services/data/outputs/tcp/server/_reload",
            "/services/data/outputs/tcp/syslog",
            "/services/data/outputs/tcp/syslog/_new",
            "/servicesNS/admin/search/data/outputs/tcp/syslog/_new",
            "/servicesNS/admin/search/data/outputs/tcp/server/_new",
            "/servicesNS/admin/search/data/outputs/tcp/server/_reload",
            "/servicesNS/admin/search/data/outputs/tcp/group/_new",
            "/servicesNS/admin/search/data/outputs/tcp/group/_reload",
            "/servicesNS/admin/search/data/outputs/tcp/default/_new",
            "/servicesNS/admin/search/data/outputs/tcp/default/_reload",
            "/servicesNS/admin/search/data/lookup-table-files/_new",
            "/servicesNS/admin/search/data/lookup-table-files/_reload",
            "/services/data/inputs/monitor",
            "/services/data/inputs/monitor/_new",
            "/services/data/inputs/monitor/_reload",
            "/services/data/inputs/oneshot",
            "/services/data/inputs/oneshot/_new",
            "/services/data/inputs/script",
            "/services/data/inputs/script/_new",
            "/services/data/inputs/script/_reload",
            "/services/data/inputs/tcp",
            "/services/data/inputs/udp",
            "/services/data/inputs/udp/_new",
            "/services/data/inputs/udp/_reload",
            "/servicesNS/admin/search/data/inputs/udp/_new",
            "/servicesNS/admin/search/data/inputs/udp/_reload",
            "/services/data/inputs/tcp/cooked",
            "/services/data/inputs/tcp/cooked/_new",
            "/services/data/inputs/tcp/cooked/_reload",
            "/services/data/inputs/tcp/raw",
            "/services/data/inputs/tcp/raw/_new",
            "/services/data/inputs/tcp/raw/_reload",
            "/services/data/inputs/tcp/ssl",
            "/services/data/inputs/tcp/ssl/_reload",
            "/servicesNS/admin/search/data/inputs/tcp/raw/_new",
            "/servicesNS/admin/search/data/inputs/tcp/raw/_reload",
            "/servicesNS/admin/search/data/inputs/tcp/cooked/_new",
            "/servicesNS/admin/search/data/inputs/tcp/cooked/_reload",
            "/servicesNS/admin/search/data/inputs/script/_new",
            "/servicesNS/admin/search/data/inputs/script/_reload",
            "/servicesNS/admin/search/data/inputs/oneshot/_new",
            "/servicesNS/admin/search/data/inputs/monitor/_new",
            "/servicesNS/admin/search/data/inputs/monitor/_reload",
            "/servicesNS/admin/search/data/indexes/_new",
            "/servicesNS/admin/search/data/indexes/_reload",
            "/services/authorization/capabilities",
            "/services/authorization/roles",
            "/services/authorization/roles/_new",
            "/servicesNS/admin/search/authorization/roles/_new",
            "/services/authorization/roles/admin",
            "/services/authorization/roles/can_delete",
            "/services/authorization/roles/power",
            "/services/authorization/roles/user",
            "/services/authorization/capabilities/capabilities",
            "/services/authentication/auth-tokens",
            "/services/authentication/changepassword",
            "/services/authentication/changepassword/_new",
            "/services/authentication/current-splunkContext",
            "/services/authentication/httpauth-tokens",
            "/services/authentication/providers",
            "/services/authentication/roles",
            "/services/authentication/roles/_new",
            "/services/authentication/users",
            "/services/authentication/users/_new",
            "/servicesNS/admin/search/authentication/users/_new",
            "/services/authentication/users/admin",
            "/servicesNS/admin/search/authentication/roles/_new",
            "/services/authentication/roles/admin",
            "/services/authentication/roles/can_delete",
            "/services/authentication/roles/power",
            "/services/authentication/roles/user",
            "/services/authentication/providers/LDAP",
            "/services/authentication/providers/LDAP/_new",
            "/services/authentication/providers/Scripted",
            "/services/authentication/providers/Scripted/_new",
            "/services/authentication/providers/Splunk",
            "/services/authentication/providers/services",
            "/services/authentication/providers/services/_reload",
            "/services/authentication/providers/services/active_authmodule",
            "/services/authentication/providers/Splunk/Splunk",
            "/servicesNS/admin/search/authentication/providers/Scripted/_new",
            "/servicesNS/admin/search/authentication/providers/LDAP/_new",
            "/services/authentication/current-splunkContext/splunkContext",
            "/servicesNS/admin/search/authentication/changepassword/_new",
            "/services/authentication/changepassword/admin",
            "/services/apps/appinstall",
            "/services/apps/appinstall/_new",
            "/services/apps/apptemplates",
            "/services/apps/local",
            "/services/apps/local/_new",
            "/services/apps/local/_reload",
            "/servicesNS/admin/search/apps/local/_new",
            "/servicesNS/admin/search/apps/local/_reload",
            "/services/apps/local/eaitest",
            "/services/apps/local/eaitest/_reload",
            "/services/apps/local/gettingstarted",
            "/services/apps/local/gettingstarted/_reload",
            "/services/apps/local/help",
            "/services/apps/local/help/_reload",
            "/services/apps/local/launcher",
            "/services/apps/local/launcher/_reload",
            "/services/apps/local/learned",
            "/services/apps/local/learned/_reload",
            "/services/apps/local/legacy",
            "/services/apps/local/legacy/_reload",
            "/services/apps/local/sample_app",
            "/services/apps/local/sample_app/_reload",
            "/services/apps/local/search",
            "/services/apps/local/search/_reload",
            "/services/apps/local/sentiment",
            "/services/apps/local/sentiment/_reload",
            "/services/apps/local/SplunkDeploymentMonitor",
            "/services/apps/local/SplunkDeploymentMonitor/_reload",
            "/services/apps/local/SplunkForwarder",
            "/services/apps/local/SplunkForwarder/_reload",
            "/services/apps/local/SplunkLightForwarder",
            "/services/apps/local/SplunkLightForwarder/_reload",
            "/services/apps/local/SplunkUniversalForwarder",
            "/services/apps/local/SplunkUniversalForwarder/_reload",
            "/services/apps/local/stubby",
            "/services/apps/local/stubby/_reload",
            "/services/apps/local/stubby/setup",
            "/services/apps/local/tcpdump",
            "/services/apps/local/tcpdump/_reload",
            "/services/apps/local/testing",
            "/services/apps/local/testing/_reload",
            "/services/apps/apptemplates/barebones",
            "/services/apps/apptemplates/sample_app",
            "/servicesNS/admin/search/apps/appinstall/_new",
            "/services/alerts/fired_alerts",

*/
            "/servicesNS/admin/search/alerts/fired_alerts/-"
    );


    /**
     * main()
     *
     * @param args the command line arguments; but this sample application does not use program parameters
     */
    public static void main(String[] args) {

        String data;

        // try some splunkContext accesses
        System.out.println("Starting");

        // create a new splunkContext SplunkBinding
        Binding splunk = new Binding();

        // login to splunkd using credentials from the .splunkrc
        try {
            // use credentials from .splunkrc
            splunk.login();
        } catch (Exception e) {
            System.out.println(e + ", Failed to login");
            return;
        }

        // GETS
        for (String getEndpoint : getEndpoints) {
            String url = null;
            try {
                // access a splunk GET REST endpoint
                url = getEndpoint;
                System.out.println("[GET] endpoint: " + url);
                Future<String> future = splunk.get(url, new AsyncHandler<String>() {
                    private final StringBuilder builder = new StringBuilder();

                    //@Override
                    public STATE onStatusReceived(HttpResponseStatus status) throws Exception {
                        //int statusCode = status.getStatusCode();
                        // The Status have been read
                        // If you don't want to read the headers,body or stop processing the response
                        return STATE.CONTINUE;
                    }

                    //@Override
                    public STATE onHeadersReceived(HttpResponseHeaders h) throws Exception {
                        //FluentCaseInsensitiveStringsMap headers = h.getHeaders();
                        // The headers have been read
                        // If you don't want to read the body, or stop processing the response
                        return STATE.CONTINUE;
                    }

                    //@Override
                    public STATE onBodyPartReceived(HttpResponseBodyPart bodyPart) throws Exception {
                        builder.append(new String(bodyPart.getBodyPartBytes()));
                        return STATE.CONTINUE;
                    }

                    //@Override
                    public String onCompleted() throws Exception {
                        // Will be invoked once the response has been fully read or a ResponseComplete exception
                        // has been thrown.
                        return builder.toString();
                    }

                    //@Override
                    public void onThrowable(Throwable t) {
                    }

                });
                try {
                    // block here until data is ready
                    data = future.get();
                    // print out the result
                    System.out.println("[GET] " + url + ": " + data);
                } catch (Exception e) {
                    System.out.println("Exception: " + e);
                }
            } catch (IOException e) {
                System.out.println("[GET] " + url + " SplunkException: " + e);
            }
        }

        // POSTS

        // sample POST
        String url = "/services/search/jobs";
        HashMap<String, String> argsList = new HashMap<String, String>();
        argsList.put("search", "search");
        argsList.put("index", "_internal");
        argsList.put("group", "mpool");
        argsList.put("exec_mode", "oneshot");

        try {
            // POST to REST endpoint
            System.out.println("[POST] endpoint: " + url);
            Future<String> future = splunk.post(url, argsList, new AsyncHandler<String>() {
                    private final StringBuilder builder = new StringBuilder();

                    //@Override
                    public STATE onStatusReceived(HttpResponseStatus status) throws Exception {
                        //int statusCode = status.getStatusCode();
                        // The Status have been read
                        // If you don't want to read the headers,body or stop processing the response
                        return STATE.CONTINUE;
                    }

                    //@Override
                    public STATE onHeadersReceived(HttpResponseHeaders h) throws Exception {
                        //FluentCaseInsensitiveStringsMap headers = h.getHeaders();
                        // The headers have been read
                        // If you don't want to read the body, or stop processing the response
                        return STATE.CONTINUE;
                    }

                    //@Override
                    public STATE onBodyPartReceived(HttpResponseBodyPart bodyPart) throws Exception {
                        builder.append(new String(bodyPart.getBodyPartBytes()));
                        return STATE.CONTINUE;
                    }

                    //@Override
                    public String onCompleted() throws Exception {
                        // Will be invoked once the response has been fully read or a ResponseComplete exception
                        // has been thrown.
                        return builder.toString();
                    }

                    //@Override
                    public void onThrowable(Throwable t) {
                    }

                });

                try {
                    // block here until response is ready
                    data = future.get();
                    // print out the result
                    System.out.println("[POST] " + url + ": " + data);
                } catch (Exception e) {
                    System.out.println("[POST] " + url + " Exception: " + e);
                }
        } catch (IOException e) {
            System.out.println("[POST] " + url + " SplunkException: " + e);
        }

        System.out.println("Finished!");
    }
}
