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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * The {@code AtomEntry} class represents an Atom {@code <entry>} element.
 */
public class AtomEntry extends AtomObject {
    /** The value of the Atom entry's {@code <published>} element. */
    public String published;

    /** The value of the Atom entry's {@code <content>} element. */
    public Record content;

    /**
     * Creates a new {@code AtomEntry} instance.
     *
     * @return A new {@code AtomEntry} instance.
     */
    static AtomEntry create() {
        return new AtomEntry();
    }

    /**
     * Creates a new {@code AtomEntry} instance based on a given stream.
     * A few endpoints, such as {@code search/jobs/{sid}},
     * return an Atom {@code <entry>} element as the root of the response.
     *
     * @param input The input stream.
     * @return An {@code AtomEntry} instance representing the parsed stream.
     */

    public static AtomEntry parseStream(InputStream input) {
        XMLStreamReader reader = createReader(input);

        AtomEntry result = AtomEntry.parse(reader);

        try {
            reader.close();
        }
        catch (XMLStreamException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        return result;
    }

    /**
     * Creates a new {@code AtomEntry} instance based on a given XML reader.
     *
     * @param reader The XML reader.
     * @return An {@code AtomEntry} instance representing the parsed XML.
     */
    static AtomEntry parse(XMLStreamReader reader) {
        AtomEntry entry = AtomEntry.create();
        entry.load(reader, "entry");
        return entry;
    }

    /**
     * Initializes the current instance using the given XML reader.
     *
     * @param reader The XML reader.
     */
    @Override void init(XMLStreamReader reader) {
        assert reader.isStartElement();

        String name = reader.getLocalName();

        if (name.equals("published")) {
            this.published = parseText(reader);
        }
        else if (name.equals("content")) {
            this.content = parseContent(reader);
        }
        else {
            super.init(reader);
        }
    }

    /**
     * Parses the {@code <content>} element of an Atom entry.
     *
     * @param reader The XML reader.
     * @return A {@code Record} object containing the parsed values.
     */
    private Record parseContent(XMLStreamReader reader) {
        assert isStartElement(reader, "content");

        scan(reader);

        // The content element should contain a single <dict> element

        if (!isStartElement(reader, "dict"))
            syntaxError(reader);

        content = parseDict(reader);

        if (!isEndElement(reader, "content"))
            syntaxError(reader);

        scan(reader); // Consume </content>

        return content;
    }

    /**
     * Parses a {@code <dict>} content element and returns a {@code Record}
     * object containing the parsed values.
     *
     * @param reader The {@code <dict>} element to parse.
     * @return A {@code Record} object containing the parsed values.
     */
    private Record parseDict(XMLStreamReader reader) {
        assert isStartElement(reader, "dict");

        Record result = new Record();

        scan(reader);
        while (isStartElement(reader, "key")) {
            String key = reader.getAttributeValue(null, "name");
            Object value = parseValue(reader);
            // Null values, the result of empty elements, are parsed as though
            // they don't exist, making it easier for the client framework to
            // supply more meaningful default values.
            if (value != null) result.put(key, value);
        }

        if (!isEndElement(reader, "dict"))
            syntaxError(reader);

        scan(reader); // Consume </dict>

        return result;
    }

    /**
     * Parses a {@code <list>} element and returns a {@code List} object
     * containing the parsed values.
     *
     * @param reader The XML reader.
     * @return A {@code List} object containing the parsed values.
     */
    private List parseList(XMLStreamReader reader) {
        assert isStartElement(reader, "list");

        List result = new ArrayList();

        scan(reader);
        while (isStartElement(reader, "item")) {
            Object value = parseValue(reader);
            result.add(value);
        }

        if (!isEndElement(reader, "list"))
            syntaxError(reader);

        scan(reader); // Consume </list>

        return result;
    }

    // Parses either a dict or list structure.
    private Object parseStructure(XMLStreamReader reader) {
        String name = reader.getLocalName();

        if (name.equals("dict"))
            return parseDict(reader);

        if (name.equals("list"))
            return parseList(reader);

        syntaxError(reader);

        return null; // Unreached
    }

    /**
     * Parses the value contained by the element at the current cursor position
     * of the given reader. 
     * <p>
     * <b>Note:</b> This function takes the parent element as its starting point
     * so that it can correctly match the end element. The function takes the
     * start element and its corresponding end element, then returns the 
     * contained value. The cursor is then located at the next element to be 
     * parsed.
     *
     * @param reader The XML reader to parse.
     * @return An object containing the parsed values. If the source was a text
     * value, the object is a {@code String}. If the source was a {@code <dict>}
     * element, the object is a {@code Record}. If the source was a
     * {@code <list>} element, the object is a {@code List} object.
     */
    Object parseValue(XMLStreamReader reader) {
        assert reader.isStartElement();

        String name = reader.getLocalName();

        scan(reader);

        Object value;
        switch (reader.getEventType()) {
        case XMLStreamConstants.CHARACTERS:
            value = reader.getText();
            scan(reader); // Advance cursor
            break;

        case XMLStreamConstants.START_ELEMENT:
            value = parseStructure(reader);
            break;

        case XMLStreamConstants.END_ELEMENT:
            value = null; // Empty element
            break;

        default:
            value = null;
            syntaxError(reader);
        }

        if (!isEndElement(reader, name))
            syntaxError(reader);

        scan(reader); // Consume end element

        return value;
    }
}
