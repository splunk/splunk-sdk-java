# The Splunk Software Development Kit for Java

#### Version 1.2.1

The Splunk Software Development Kit (SDK) for Java contains library code and 
examples designed to enable developers to build applications using Splunk.

Splunk is a search engine and analytic environment that uses a distributed
map-reduce architecture to efficiently index, search and process large 
time-varying data sets.

The Splunk product is popular with system administrators for aggregation and
monitoring of IT machine data, security, compliance and a wide variety of 
other scenarios that share a requirement to efficiently index, search, analyze
and generate real-time notifications from large volumes of time series data.

The Splunk developer platform enables developers to take advantage of the 
same technology used by the Splunk product to build exciting new applications
that are enabled by Splunk's unique capabilities.


## Getting started with the Splunk SDK for Java

The Splunk SDK for Java contains library code and examples that show how to 
programmatically interact with Splunk for a variety of scenarios including 
searching, saved searches, data inputs, and many more, along with building 
complete applications. 

The information in this Readme provides steps to get going quickly, but for more
in-depth information be sure to visit the 
[Splunk Developer Portal](http://dev.splunk.com/view/java-sdk/SP-CAAAECN). 

### Requirements

Here's what you need to get going with the Splunk SDK for Java.

#### Splunk

If you haven't already installed Splunk, download it 
[here](http://www.splunk.com/download). For more about installing and running 
Splunk and system requirements, see 
[Installing & Running Splunk](http://dev.splunk.com/view/SP-CAAADRV). 

#### Splunk SDK for Java

[Get the Splunk SDK for Java](http://dev.splunk.com/view/SP-CAAAECN)&mdash;download the SDK as a ZIP, then extract the files and build the SDK. Or, download the JAR and add it to your project.

If you want to contribute to the SDK, clone the repository from [GitHub](https://github.com/splunk/splunk-sdk-java).


#### Java and Ant

You'll need Java SE version 6 or higher, which you can download from the 
[Oracle web site](http://www.oracle.com/technetwork/java/javase/downloads/index.html). 

You'll also need Ant, which you can install from the 
[Apache website](http://ant.apache.org/bindownload.cgi). 

If you are using Windows, you'll need to make sure the following system 
variables are created and set: 

*   **ANT_HOME** should be set to the location where Ant is installed.

*   **JAVA_HOME** should be set to the directory where the JDK is installed.

*   **PATH** should include the path to the **%ANT_HOME%\bin** directory.

For full installation instructions, you can find more information here:
 
*   [Java Platform Installation](http://www.oracle.com/technetwork/java/javase/index-137561.html)

*   [Installing Apache Ant](http://ant.apache.org/manual/install.html)

### Building the SDK and documentation

To build the SDK, open a command prompt in the **/splunk-sdk-java** 
directory and enter: 

    ant

or

    ant dist

This command builds all of the .class and .jar files. If you just want to build
the .class files, enter:

    ant build

To remove all build artifacts from the repository, enter:

    ant clean

To build the documentation for the SDK, enter: 

    ant javadoc

### Examples and unit tests

The Splunk SDK for Java includes several examples and unit tests that are run at 
the command line. 

#### Set up the .splunkrc file

To connect to Splunk, many of the SDK examples and unit tests take command-line
arguments that specify values for the host, port, and login credentials for
Splunk. For convenience during development, you can store these arguments as
key-value pairs in a text file named **.splunkrc**. Then, the SDK examples and 
unit tests use the values from the **.splunkrc** file when you don't specify 
them.

To use this convenience file, create a text file with the following format:

    # Splunk host (default: localhost)
    host=localhost
    # Splunk admin port (default: 8089)
    port=8089
    # Splunk username
    username=admin
    # Splunk password
    password=changeme
    # Access scheme (default: https)
    scheme=https
    # Your version of Splunk (default: 5.0)
    version=5.0

Save the file as **.splunkrc** in the current user's home directory.

*   For example, on Mac OS X, save the file as: 

        ~/.splunkrc

*   On Windows, save the file as: 

        C:\Users\currentusername\.splunkrc

    You might get errors in Windows when you try to name the file because
    ".splunkrc" looks like a nameless file with an extension. You can use
    the command line to create this file&mdash;go to the 
    **C:\Users\currentusername** directory and enter the following command: 

        Notepad.exe .splunkrc

    Click **Yes**, then continue creating the file.

**Note**: Storing login credentials in the **.splunkrc** file is only for 
convenience during development. This file isn't part of the Splunk platform and 
shouldn't be used for storing user credentials for production. And, if you're 
at all concerned about the security of your credentials, just enter them at 
the command line rather than saving them in this file. 


#### Run examples

After you build the SDK, examples are put in the **/splunk-sdk-
java/dist/examples** directory. To run the examples, run the Java interpreter 
at the command line using the `-jar` flag to specify the target example jar 
file, and include any arguments that are required by the example. To get help 
for an example, use the `--help` argument with an example. 

For example, to see the command-line arguments for the Search example, open a 
command prompt in the **/splunk-sdk-java** directory and enter: 

    java -jar dist/examples/search.jar --help

To run the Search example, open a command prompt in the **/splunk-sdk-java**
directory and enter: 

    java -jar dist/examples/search.jar "search * | head 10" --output_mode=csv

There is also a helper script called run in the root of the repository that 
simplifies running the SDK examples. For example, on Mac OS X you could 
simply enter: 

    ./run search "search * | head 10" --output_mode=csv

All the the example jars are completely self contained. They can be used
completely independently of the SDK's repository.

#### Run unit tests

To run the SDK unit tests, open a command prompt in the **/splunk-sdk-java** 
directory and enter: 

    ant test

To run the units from anywhere in the repository, enter:

    ant test -find

You can also run specific test classes by passing the class to the -Dtestcase=
option, e.g.,

    ant test -Dtestcase=AtomFeedTest

The ant configuration can also produce a single HTML report of all the tests run
using the target testreport (which also understands the -Dtestcase= option), e.g.

    ant testreport

The report will be written in build/reports/tests/index.html.

It's also possible to run the units within Java IDEs such as IntelliJ and 
Eclipse. For example, to open the Splunk SDK for Java project in Eclipse: 

  1. Click **File**, **Import**. 
  2. Click **General**, **Existing Projects into Workspace**, then click 
     **Next**. 
  3. In **Select root directory**, type the path to the Splunk SDK for Java root
     directory (or click **Browse** to locate it), then click **Finish**.

#### Measure code coverage

To measure the code coverage of the test suite, open a 
command prompt in the **/splunk-sdk-java** directory and enter:

    ant coverage

To run code coverage from anywhere in the repository, enter:

    ant coverage -find

To view the coverage report, open 
**/splunk-sdk-java/build/reports/coverage/index.html** in your web browser.

## Repository

<table>
<tr>
<td><b>/argsGenerator</b></td>
<td>This directory is created by the build and contains intermediate build 
ouputs</td>
</tr>

<tr>
<td><b>/build</b></td>
<td>This directory is created by the build and contains intermediate build 
ouputs</td>
</tr>

<tr>
<td><b>/dist</b></td>
<td>This directory is created by the build and contains final build 
outputs</td>
</tr>

<tr>
<td><b>/examples</b></td>
<td>Examples demonstrating various SDK features</td>
</tr>

<tr>
<td><b>/lib</b></td>
<td>Third-party libraries used by examples and unit tests</td>
</tr>

<tr>
<td><b>/splunk</b></td>
<td>Source for <code>com.splunk</code></td>
</tr>

<tr>
<td><b>/tests</b></td>
<td>Source for unit tests</td>
</tr>

<tr>
<td><b>/util</b></td>
<td>Utilities shared by examples and units</td>
</tr>
</table>

### Changelog

The **CHANGELOG.md** file in the root of the repository contains a description
of changes for each version of the SDK. You can also find it online at
[https://github.com/splunk/splunk-sdk-java/blob/master/CHANGELOG.md](https://github.com/splunk/splunk-sdk-java/blob/master/CHANGELOG.md).

### Branches

The **master** branch always represents a stable and released version of the SDK.
You can read more about our branching model on our Wiki at 
[https://github.com/splunk/splunk-sdk-java/wiki/Branching-Model](https://github.com/splunk/splunk-sdk-java/wiki/Branching-Model).

## Documentation and resources

If you need to know more: 

* For all things developer with Splunk, your main resource is the [Splunk Developer Portal](http://dev.splunk.com).

* For conceptual and how-to documentation, see the [Overview of the Splunk SDK for Java](http://dev.splunk.com/view/SP-CAAAECN).

* For API reference documentation, see the [Splunk SDK for Java Reference](http://docs.splunk.com/Documentation/JavaSDK).

* For more about the Splunk REST API, see the [REST API Reference](http://docs.splunk.com/Documentation/Splunk/latest/RESTAPI).

* For more about about Splunk in general, see [Splunk>Docs](http://docs.splunk.com/Documentation/Splunk).

* For more about this SDK's repository, see our [GitHub Wiki](https://github.com/splunk/splunk-sdk-java/wiki/).

## Community

Stay connected with other developers building on Splunk.

<table>

<tr>
<td><b>Email</b></td>
<td>devinfo@splunk.com</td>
</tr>

<tr>
<td><b>Issues</b>
<td><span>https://github.com/splunk/splunk-sdk-java/issues/</span></td>
</tr>

<tr>
<td><b>Answers</b>
<td><span>http://splunk-base.splunk.com/tags/java/</span></td>
</tr>

<tr>
<td><b>Blog</b>
<td><span>http://blogs.splunk.com/dev/</span></td>
</tr>

<tr>
<td><b>Twitter</b>
<td>@splunkdev</td>
</tr>

</table>


### How to contribute

If you would like to contribute to the SDK, go here for more information:

* [Splunk and open source](http://dev.splunk.com/view/opensource/SP-CAAAEDM)

* [Individual contributions](http://dev.splunk.com/goto/individualcontributions)

* [Company contributions](http://dev.splunk.com/view/companycontributions/SP-CAAAEDR)

### Support

1. You will be granted support if you or your company are already covered 
   under an existing maintenance/support agreement. Send an email to 
   _support@splunk.com_ and include "Splunk SDK for Java" in the subject line. 

2. If you are not covered under an existing maintenance/support agreement, you 
   can find help through the broader community at:

   <ul>
   <li><a href='http://splunk-base.splunk.com/answers/'>Splunk Answers</a> (use
    the <b>sdk</b>, <b>java</b>, <b>python</b>, and <b>javascript</b> tags to 
    identify your questions)</li>
   <li><a href='http://groups.google.com/group/splunkdev'>Splunkdev Google 
    Group</a></li>
   </ul>
3. Splunk will NOT provide support for SDKs if the core library (the 
   code in the <b>splunk</b> directory) has been modified. If you modify an SDK
   and want support, you can find help through the broader community and Splunk 
   answers (see above). We would also like to know why you modified the core 
   library&mdash;please send feedback to _devinfo@splunk.com_.
4. File any issues on [GitHub](https://github.com/splunk/splunk-sdk-java/issues).


### Contact Us

You can reach the Developer Platform team at _devinfo@splunk.com_.

## License

The Splunk Java Software Development Kit is licensed under the Apache
License 2.0. Details can be found in the LICENSE file.
