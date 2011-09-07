import com.splunk.asyncsdk.Binding;

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


public class async_info {
    /**
      * Creates a new instance of a sample app that post's and get's to
      * Splunk's RESTful API
      */

    public async_info() {
    }

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

        System.out.println("Finished!");
    }

}
