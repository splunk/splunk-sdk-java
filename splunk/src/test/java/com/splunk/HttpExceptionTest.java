package com.splunk;

import org.junit.BeforeClass;
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

    private static ResponseMessage xmlResponse;
    private static ResponseMessage stringResponse;

    @BeforeClass
    public static void setUp() {
        InputStream inXml = new ByteArrayInputStream(responseMessageXML.getBytes());
        xmlResponse = new ResponseMessage(503, inXml);
        InputStream inString = new ByteArrayInputStream(responseMessage.getBytes());
        stringResponse = new ResponseMessage(503, inString);
    }

    @Test
    public void testCreateFromXML() {
        HttpException e = HttpException.create(xmlResponse);
        assertEquals("Couldn't parse XML error details", responseMessage, e.getDetail());
        assertEquals("create() modified response status code", 503, e.getStatus());
    }

    @Test
    public void testCreateFromString() {
        HttpException e = HttpException.create(stringResponse);
        assertEquals("Raw response message was modified", responseMessage, e.getDetail());
        assertEquals("create() modified response status code", 503, e.getStatus());
    }
}
