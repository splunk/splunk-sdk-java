package com.splunk;

/**
 * ModularInputScript is an abstract base class for implementing modular inputs. Subclasses
 * should override getScheme and streamEvents, and optionall configureValidator if the modular
 * input is using external validation.
 */
public abstract class ModularInputScript {
    /**
     * run encodes all the common behavior of modular inputs. Users should have no reason
     * to override this method in most cases.
     *
     * @param argv An array of command line arguments passed to this script.
     */
    public void run(String[] argv) {

    }

    /**
     * The scheme defines the parameters understood by this modular input.
     *
     * @return a Scheme object representing the parameters for this modular input.
     */
    public abstract Scheme getScheme();

    /**
     * validateInput handles external validation for modular input kinds. When Splunk
     * called a modular input script in validation mode, it will pass in an XML document
     * giving information about the Splunk instance (so you can call back into it if needed)
     * and the name and parameters of the proposed input.
     *
     * If this function does not throw an exception, the validation is assumed to succeed. Otherwise
     * any error throws will be turned into a string and logged back to Splunk.
     *
     * The default implementation always passes.
     *
     * @param definition The parameters for the proposed input passed by splunkd.
     */
    public void validateInput(ValidationDefinition definition) {}

    /**
     * The method called to stream events into Splunk. It should do all of its output via
     * EventWriter rather than assuming that there is a console attached.
     *
     * @param ew An object with methods to write events and log messages to Splunk.
     */
    public abstract void streamEvents(EventWriter ew);
}
