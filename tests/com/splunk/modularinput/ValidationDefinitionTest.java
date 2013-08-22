package com.splunk.modularinput;

import com.splunk.SDKTestCase;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Checks that parsing XML into ValidationDefinitions works correctly.
 */
public class ValidationDefinitionTest extends ModularInputTestCase {
    /**
     * Checks that the ValidationDefinition parsed from XML is what we expect.
     */
    @Test
    public void testParseValidationDefinition() throws ParserConfigurationException, SAXException, MalformedDataException,
            IOException {
        InputStream stream = SDKTestCase.openResource("modularinput/data/validation.xml");
        ValidationDefinition found = ValidationDefinition.parseDefinition(stream);

        ValidationDefinition expected = new ValidationDefinition();
        expected.setServerHost("tiny");
        expected.setServerUri("https://127.0.0.1:8089");
        expected.setCheckpointDir("/opt/splunk/var/lib/splunk/modinputs");
        expected.setSessionKey("123102983109283019283");
        expected.setName("aaa");
        List<Parameter> parameters = new ArrayList<Parameter>();
        parameters.add(new SingleValueParameter("param1", "value1"));
        parameters.add(new SingleValueParameter("param2", "value2"));
        parameters.add(new SingleValueParameter("disabled", "0"));
        parameters.add(new SingleValueParameter("index", "default"));
        MultiValueParameter mvp = new MultiValueParameter("multiValue");
        mvp.appendValue("value1");
        mvp.appendValue("value2");
        parameters.add(mvp);
        mvp = new MultiValueParameter("multiValue2");
        mvp.appendValue("value3");
        mvp.appendValue("value4");
        parameters.add(mvp);
        expected.setParameters(parameters);

        Assert.assertTrue(expected.equals(found));
    }
}
