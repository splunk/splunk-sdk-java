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
 * Test the parsing of InputDefinition classes from XML.
 */
public class InputDefinitionTest extends ModularInputTestCase {
    /**
     * Check whether the InputDefinition parsed from a stream with metadata and zero inputs is what we expect.
     */
    @Test
    public void testParseStreamWithZeroInputs() throws IOException, SAXException, ParserConfigurationException,
            MalformedDataException {
        InputDefinition expectedDefinition = new InputDefinition();
        expectedDefinition.setServerHost("tiny");
        expectedDefinition.setServerUri("https://127.0.0.1:8089");
        expectedDefinition.setCheckpointDir("/some/dir");
        expectedDefinition.setSessionKey("123102983109283019283");

        InputStream stream = SDKTestCase.openResource("modularinput/data/conf_with_0_inputs.xml");
        InputDefinition foundDefinition = InputDefinition.parseDefinition(stream);

        Assert.assertTrue(expectedDefinition.equals(foundDefinition));
    }

    /**
     * Check whether the InputDefinition parsed from a stream with metadata and three inputs is what we expect.
     */
    @Test
    public void testParseStreamWithThreeInputs() throws ParserConfigurationException, SAXException,
            MalformedDataException, IOException {
        InputDefinition expectedDefinition = new InputDefinition();
        expectedDefinition.setServerHost("tiny");
        expectedDefinition.setServerUri("https://127.0.0.1:8089");
        expectedDefinition.setCheckpointDir("/some/dir");
        expectedDefinition.setSessionKey("123102983109283019283");

        List<Parameter> parameters = new ArrayList<Parameter>();
        parameters.add(new SingleValueParameter("param1", "value1"));
        parameters.add(new SingleValueParameter("param2", "value2"));
        parameters.add(new SingleValueParameter("disabled", "0"));
        parameters.add(new SingleValueParameter("index", "default"));
        expectedDefinition.addInput("foobar://aaa", parameters);

        parameters = new ArrayList<Parameter>();
        parameters.add(new SingleValueParameter("param1", "value11"));
        parameters.add(new SingleValueParameter("param2", "value22"));
        parameters.add(new SingleValueParameter("disabled", "0"));
        parameters.add(new SingleValueParameter("index", "default"));
        parameters.add(new MultiValueParameter("multiValue") {{
            appendValue("value1");
            appendValue("value2");
        }});
        parameters.add(new MultiValueParameter("multiValue2") {{
            appendValue("value3");
            appendValue("value4");
        }});
        expectedDefinition.addInput("foobar://bbb", parameters);

        InputStream stream = SDKTestCase.openResource("modularinput/data/conf_with_2_inputs.xml");
        InputDefinition foundDefinition = InputDefinition.parseDefinition(stream);

        Assert.assertTrue(expectedDefinition.equals(foundDefinition));
    }

    /**
     * Checks that parsing an InputDefinition from malformed XML produces the expected exception.
     */
    @Test
    public void testParseMalformedInputDefinition() throws ParserConfigurationException, SAXException, IOException {
        try {
            InputStream stream = SDKTestCase.openResource("modularinput/data/conf_with_invalid_inputs.xml");
            InputDefinition foundDefinition = InputDefinition.parseDefinition(stream);
        } catch (MalformedDataException e) {
            Assert.assertTrue(true);
            return;
        }
        Assert.fail();
    }
}
