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

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LoggerTest extends SDKTestCase {
    private static final List<String> VALID_LEVELS = Arrays.asList(
            "INFO", "WARN", "ERROR", "DEBUG", "CRIT", "FATAL");
    
    // NOTE: Ideally we would create our own logger instead of using an existing
    //       one. However there is no REST API call to create a Logger, and
    //       we can't use an app to install one, either.
    private static final String TEST_LOGGER_NAME = "AuditLogger";
    
    private Logger logger;
    private String originalLoggerLevel;
    
    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        
        logger = service.getLoggers().get(TEST_LOGGER_NAME);
        originalLoggerLevel = logger.getLevel();
    }

    @After
    @Override
    public void tearDown() throws Exception {
        logger.setLevel(originalLoggerLevel);
        
        super.tearDown();
    }
    
    @Test
    public void testDefaultLoggersHaveValidLevels() {
        for (Logger curLogger : service.getLoggers().values()) {
            assertTrue(VALID_LEVELS.contains(curLogger.getLevel()));
        }
    }
    
    @Test
    public void testLevelSetter() {
        for (String curLevel : VALID_LEVELS) {
            logger.setLevel(curLevel);
            logger.update();
            assertEquals(curLevel, logger.getLevel());
        }
    }
    
    @Test
    public void testLevelUpdate() {
        for (String curLevel : VALID_LEVELS) {
            logger.update(new Args("level", curLevel));
            assertEquals(curLevel, logger.getLevel());
        }
    }
}
