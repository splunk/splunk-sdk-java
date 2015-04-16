package com.splunk;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

public class HttpExceptionTest {

    private static final String responseMessage = "Splunk VERY FATAL error";
    private static final String responseMessageXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<response><messages><msg type=\"FATAL\">"
            + responseMessage
            + "</msg></messages></response>";

    private ResponseMessage response;

    @Before
    public void setUp() {
        InputStream in = new ByteArrayInputStream(responseMessageXML.getBytes());
        response = new ResponseMessage(503, in);
    }

    @Test
    public void testCreate() {
        HttpException e = HttpException.create(response);
        assertEquals("Couldn't parse error details", responseMessage, e.getDetail());
        assertEquals("create() modified response code", 503, e.getStatus());
    }
}
