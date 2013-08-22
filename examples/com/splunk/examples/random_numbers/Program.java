/*
 * Copyright 2013 Splunk, Inc.
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

package com.splunk.examples.random_numbers;

import com.splunk.modularinput.*;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.Random;

// All modular inputs should inherit from the abstract base class com.splunk.modularinput.Script. They must override
// the getScheme and streamEvents methods, and, if the scheme returned by getScheme had
// Scheme.setUseExternalValidation(true) called on it, the validateInput method. The user must provide a main
// method since static methods can't be inherited in Java. However, the main is very simple.
public class Program extends Script {

    // All main methods for modular inputs need only be one line. They can't be inherited because they must create
    // an instance of this class, and the abstract base class has no way to do so. So if the class were called MyInput,
    // the one line would be
    //
    //      new MyInput().run(args);
    //
    public static void main(String[] args) {
        new Program().run(args);
    }

    // When Splunk starts, it looks for all the modular inputs defined by its configuration, and tries to run them
    // with the argument --scheme. Splunkd expects the modular inputs to print a description of the input in XML
    // on stdout. The modular input framework takes care of all the details of formatting XML and printing it. The
    // user need only override getScheme and return a new Scheme object.
    @Override
    public Scheme getScheme() {
        // "random_numbers" is the name Splunk will display to users for this input.
        Scheme scheme = new Scheme("random_numbers");

        scheme.setDescription("Generates events containing a random number.");
        // If you set external validation, without overriding validateInput, the script will accept anything as valid.
        // Generally you only need external validation if there are relationships you must maintain among the
        // parameters, such as requiring min to be less than max in this example, or you need to check that some
        // resource is reachable or valid. Otherwise, Splunk lets you specify a validation string for each argument
        // and will run validation internally using that string.
        scheme.setUseExternalValidation(true);
        scheme.setUseSingleInstance(true);

        Argument minArgument = new Argument("min");
        minArgument.setDataType(Argument.DataType.NUMBER);
        minArgument.setDescription("Minimum value to be produced by this input.");
        minArgument.setRequiredOnCreate(true);
        // If you are not using external validation, you would add something like:
        //
        // setValidation("min > 0");
        scheme.addArgument(minArgument);

        Argument maxArgument = new Argument("max");
        maxArgument.setDataType(Argument.DataType.NUMBER);
        maxArgument.setDescription("Maximum value to be produced by this input.");
        maxArgument.setRequiredOnCreate(true);
        scheme.addArgument(maxArgument);

        return scheme; // ...and don't forget to return the scheme.
    }

    // In this example we are using external validation, since we want max to always be greater than min.
    // If validateInput does not throw an Exception, the input is assumed to be valid. Otherwise it prints the
    // exception as an error message when telling splunkd that the configuration is not valid.
    //
    // When using external validation, after splunkd calls the modular input with --scheme to get a scheme, it calls it
    // again with --validate-arguments for each instance of the modular input in its configuration files, feeding XML
    // on stdin to the modular input to get it to do validation. It calls it the same way again whenever a modular
    // input's configuration is changed.
    @Override
    public void validateInput(ValidationDefinition definition) throws Exception {
        // Get the values of the two parameters. There are also methods getFloat, getInt, getBoolean, etc.,
        // and getValue to get the string representation.
        double min = ((SingleValueParameter)definition.getParameters().get("min")).getDouble();
        double max = ((SingleValueParameter)definition.getParameters().get("max")).getDouble();

        if (min >= max) {
            throw new Exception("min must be less than max; found min=" + Double.toString(min) +
                    ", max=" + Double.toString(max));
        }
    }

    // Finally, the real action: splunk calls the modular input with no arguments, streams a bunch of XML describing
    // the inputs to stdin, and waits for XML on stdout describing events.
    //
    // If you set setUseSingleInstance(true) on the scheme in getScheme, it will pass all the instances of this input
    // to a single instance of this script and it's your job to handle them all. Otherwise, it starts a JVM for each
    // instance of the input.
    //
    // We are using a single instance, and starting a thread for each instance of the modular input. For scripts that
    // are not single instance, it is simpler to do the work directly in the streamEvents method.
    @Override
    public void streamEvents(InputDefinition inputs, EventWriter ew) throws MalformedDataException,
            XMLStreamException, IOException {
        for (String inputName : inputs.getInputs().keySet()) {
            // We get the parameters for each input and start a new thread for each one. All the real work
            // happens in the Generator class below.
            double min = ((SingleValueParameter)inputs.getInputs().get(inputName).get("min")).getDouble();
            double max = ((SingleValueParameter)inputs.getInputs().get(inputName).get("max")).getDouble();

            Thread t = new Thread(new Generator(ew, inputName, min, max));
            t.run();
        }
    }

    // A Runnable that generates events with a random number in the proper range every half second. All the important
    // stuff to look at is in the run method.
    class Generator implements Runnable {
        private double min, max;
        EventWriter ew;
        String inputName;

        public Generator(EventWriter ew, String inputName, double min, double max) {
            super();
            this.min = min;
            this.max = max;
            this.ew = ew;
            this.inputName = inputName;
        }

        public void run() {
            // First we log an INFO message that this thread has started. This will show up in splunkd.log and in
            // Splunk's _internal index.

            // EventWriter provides both log and synchronizedLog (one a synchronized version of the other). In
            // this case, synchronizing at the level of each log message and event is exactly what we want. In
            // more complicated cases, you may want to use the unsynchronized version and do your own
            // synchronization.
            ew.synchronizedLog(EventWriter.INFO, "Random number generator " + inputName +
                    " started, generating numbers between " +
                    Double.toString(min) + " and " + Double.toString(max));

            final Random randomGenerator = new Random();

            while (true) {
                // Write a new event. The minimum that you must set on an event is the stanza it is supposed to
                // go to (which you can skip if your modular input is not single instance, and the data of the
                // event.
                Event event = new Event();
                event.setStanza(inputName);
                event.setData("number=" + (randomGenerator.nextDouble() * (max - min) + min));

                try {
                    ew.writeEvent(event);
                } catch (MalformedDataException e) {
                    ew.synchronizedLog(EventWriter.ERROR, "MalformedDataException in writing event to input" +
                            inputName + ": " + e.toString());
                }

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }
}
