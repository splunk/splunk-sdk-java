package com.splunk;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a parameter containing multiple values that is passed as part of a definition
 * of a modular input instance. MultiValueParameter objects correspond to XML fragments of the form
 *
 * <param_list name="multiValue">
 *     <value>value1</value>
 *     <value>value2</value>
 * </param_list>
 */
public class MultiValueParameter extends Parameter {
    protected String name;
    protected List<String> values;

    // Note: package private constructor by design so parameters cannot be instantiated by the user.
    MultiValueParameter(String name) {
        this.name = name;
        this.values = new ArrayList<String>();
    }

    /**
     * @return The name of this parameter.
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return A list of all values of this parameter.
     */
    public List<String> getValues() {
        return this.values;
    }

    // Package private by design.
    void appendValue(String value) {
        this.values.add(value);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof MultiValueParameter)) {
            return false;
        } else {
            MultiValueParameter that = (MultiValueParameter)other;
            return this.values.equals(that.values) && this.name.equals(that.name);
        }
    }
}
