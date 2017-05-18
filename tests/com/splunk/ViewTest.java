/*
 * Copyright 2012 Splunk, Inc.
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

package com.splunk;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.text.MessageFormat;

public class ViewTest extends SDKTestCase {

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
    }


    @After
    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }
    

    @Test
    public void testListViews() {
    	String search=MessageFormat.format("((isDashboard=1 AND isVisible=1) AND (eai:type!=\"html\") AND ((eai:acl.sharing=\"user\" AND eai:acl.owner=\"{0}\") OR (eai:acl.sharing!=\"user\")))", service.getUsername());
    	final EntityCollection<View> views = service.getViews("oidemo",search);
    	for (View view : views.values()) {
    		String data=view.getData();
    		if(data.indexOf("</module>")>0&&data.indexOf("</view>")>0) {
    			//this is not simple xml, skip it
    			continue;
    		}
    		System.out.println(view.getTitle());
    		System.out.println(view.getLabel());
    		System.out.println(view.getData());
    		System.out.println("==========");
    		//break;
    	}
    }


}
