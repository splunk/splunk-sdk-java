package com.splunk;

import java.util.ArrayList;
import java.util.List;

public class MultiValueParameter extends Parameter {
    protected String name;
    protected List<String> values;

    public MultiValueParameter(String name) {
        this.name = name;
        this.values = new ArrayList<String>();
    }

    public String getName() {
        return this.name;
    }

    public List<String> getValues() {
        return this.values;
    }

    public void appendValue(String value) {
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
