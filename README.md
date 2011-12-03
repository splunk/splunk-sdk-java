# The Splunk Software Development Kit for Java (Preview Release)

This SDK contains library code and examples designed to enable developers to
build applications using Splunk.

Splunk is a search engine and analytic environment that uses a distributed
map-reduce architecture to efficiently index, search and process large 
time-varying data sets.

The Splunk product is popular with system administrators for aggregation and
monitoring of IT machine data, security, compliance and a wide variety of other
scenarios that share a requirement to efficiently index, search, analyze and
generate real-time notifications from large volumes of time series data.

The Splunk developer platform enables developers to take advantage of the same
technology used by the Splunk product to build exciting new applications that
are enabled by Splunk's unique capabilities.

## License

The Splunk Software Development Kit for Java is licensed under the Apache
License 2.0. Details can be found in the file LICENSE.

## This SDK is a Preview Release

1.  This Preview release a pre-beta release.  There will also be a beta release 
    prior to a general release. It is incomplete and may have bugs.

2.  The Apache license only applies to the SDK and no other Software provided 
    by Splunk.

3.  Splunk in using the Apache license is not providing any warranties, 
    indemnification or accepting any liabilities  with the Preview SDK.

4.  Splunk is not accepting any Contributions to the Preview release of the SDK.  
    All Contributions during the Preview SDK will be returned without review.

## Getting started

In order to use the SDK you are going to need a copy of Splunk. If you don't 
already have a copy you can download one from http://www.splunk.com/download.

You can get a copy of the SDK sources by cloning into the repository with git:

> git clone https://github.com/splunk/splunk-sdk-java.git

### Requirements

The SDK requires Java version 1.6+, aka Java SE 6+.

A good description of the Java version history can be found at:

* http://en.wikipedia.org/wiki/Java_version_history

### Building

In order to build the SDK, simply type the following from the root of the 
repository.

> ant

In order to remove all build artifacts from the repository simply type:

> ant clean

And to build the javadocs for the SDK, type:

> ant javadoc

Take a look at `build.xml` for other targets if you are interested in more
control of the build process.

You can also type `ant {target} -find` from anywhere in the repository and 
ant will automatically find the root build file.

### Setup for Windows

If not already installed, install both Java JDK from Oracle and Ant from
Apache. 

* http://www.oracle.com/technetwork/java/javase/downloads/index.html
* http://ant.apache.org/ respectively.

Set the environment variables ANT_HOME to the location you unzipped the ant
installation. Set the environment variable JAVA_HOME to the directory
you installed the Java JDK.

### Running the examples and unit tests

The SDK examples and unit tests require a common set of command line arguments
that specify things like the Splunk host, port and login credentials. You can
get a full list of command line arguments by typing `--help` as an argument
to any of the command line examples.

#### .splunkrc

The examples and units are also desigend to receive arguments from an optional
`.splunkrc` file located in your home directory. The format of the file is
simply a list of key=value pairs, same as the options that are taken on the
command line, for example:

    host=localhost
    username=admin
    password=changeme

The `.splunkrc` file is a feature of the SDK examples and unit tests and not
the Splunk platform or SDK libraries and is intended simply as convenience for
developers using the SDK.

The `.splunkrc` file should not be used for storing user credentials for apps
built on Splunk and should not be used if you are concerned about the security
of the credentails used in your development environment.

You can view a sample `.splunkrc` file by looking at the `splunkrc.spec` file
in the root directory of the repistory.

#### Examples

You can run any of the SDK examples by invoking the Java interpreter on the
command line, using the -jar flag to specify the target example jar file and
adding any other command line arguments required by the example.

The build process deposits examples under ./dist/examples, so in order to
launch the search example, you might type the following:

> java -jar dist/examples/search.jar "search * | head 10" --output_mode=csv

And to get a list of all command line arguments supported by the example
you would type:

> java -jar dist/examples/search.jar --help

There is also a helper script called `run` in the root of the repository that 
simplifies running the SDK examples, so instead of the command above, you 
could simply type:

> ./run search "search * | head 10" --output_mode=csv

#### Unit tests

In order to run the SDK unit tests, you simply type the following from the
root of the repository.

> ant test

You can also use `ant test -find` to run the units from anywhere in the
repository.
    
It's also possible to run the units from within popular Java IDEs such as
IntelliJ.

### Using Eclipse

_TODO_

### Using IntelliJ

_TODO_

## Overview

The Splunk developer platform consists of three primary components: `splunkd`,
the engine, `splunkweb`, the app framework that sits on top of the engine,
and the Splunk SDKs that interface with the REST API and extension points.

This SDK enables developers to target `splunkd` by making calls against the
engine's REST API and by accessing the various `splunkd` extension points such
as custom search commands, lookup functions, scripted inputs and custom REST
handlers.

You can find additional information about building applications on Splunk at
our developer portal at http://dev.splunk.com.

### Service

The `Service` class is the primary entry point for the client library. You 
construct an instance of the Service class, providing any arguments required
to connect to an available Splunk server. Once the Service instance is created, 
you need to call the `login` method and provide login credentials. Once you
have an authenticated Service instance, you can use it to navigate, enumerate
and operate on a wide variety of Splunk resources. You can also use it to issue
searches and even make low level REST API calls.

The following example creates a Service instance and prints the Splunk version
number.

    import com.splunk.Service;

    public class Program {
        public static void run(String[] args) {
            Service service = new Service("localhost", 8089);
            service.login("admin", "changeme");
            ServiceInfo serviceInfo = service.getInfo();
            System.out.println(serviceInfo.getVersion());
        }
    }

The Service class provides a variety of constructor overloads to handle 
various scenarios that require different combinations of arguments and in the
most general case takes a map of arguments, which simplifies passing large
and varying combinations of arguments.

    Map<String, Object> args = new HashMap<String, Object>();
    args.put("host", "localhost");
    args.put("port", 8089);
    Service service = new Service(args);
    service.login("admin", "changeme");

The Service class also provides a static helper methods that takes a map and
combines the construction and authentication of the instance into a single
call. This is the most general and flexible method and is the method most often
used in the SDK examples and unit tests.

    Map<String, Object> args = new HashMap<String, Object>();
    args.put("host", "localhost");
    args.put("port", 8089);
    args.put("scheme", "https");
    args.put("app", "MyApp");
    args.put("owner", "nobody");
    args.put("username", "admin");
    args.put("password", "changeme");
    Service service = Service.connect(args);

### Entities and collections

The Splunk REST API is a rich interface consisting of over 160 endpoints that
provide access to virtually every feature area of Splunk. The majority of this
API follows a convention of exposing resorces as collections of entities, where
an entity is simply a resource that has properties, actions and metadata that
describes the entity. The entity/collection pattern provides a consistent
approach to interacting with resources and collections of resources.

For example, the following code fragment prints out all Splunk users.

    Service service = Service.connect(...);
    for (User user : service.getUsers().values())
        System.out.println(user.getName());

And in a very similar way, the following code prints out all the Splunk apps.

    Service service = Service.connect(...);
    for (Application app : service.getApplications().values())
        System.out.println(app.getName());

Collections have a common way of creating and removing entities. Entities have
a common way of retrieving property values, updating those values and accessing
entity metadata. So once you are familiar with this pattern you will have a
reasonable understanding of how much of the SDK and underlying REST API work.

The SDK contains the base classes `Entity` and `EntityCollection`. Both Entity 
and EntityCollection derive from the common base class `Resource`. Notice that
Service itself is not a Resource, but is a container that provides access to
all features associated with a Splunk instance.

Following is the class hierarchy for the core of the SDK library:

    Service
    Resource
        Entity
        ResourceCollection
            EntityCollection

### Search

One of the primary features of Splunk is the ability to exceute searches and
retrive search results. To help get you started, the following code fragment
shows creating a search job, polling for completion and retrieving the result
stream.

    Service service = Service.connect(...);
    Job job = service.getJobs().create("search * | head 10")
    while (!job.isDone()) {
        Thread.sleep(2000);
        job.refresh();
    }
    InputStream stream = job.getResults();

The Splunk search API has many features, including realtime search, numerious
search options, various kinds of search results and the ability to execute 
synchronous search requests in addition to the basic async interface show above.

The core search features are explored in the SDK `search` example and other
variations are explored in various other SDK examples.

### Client state

_TODO_

## Repository

<table>
<tr>
<td><em>build</em><td>
<td>This directory is created by the build and contains intermediate build ouputs</td>
</tr>

<tr>
<td><em>dist</em><td>
<td>This directory is created by the build and contains final build outputs</td>
</tr>

<tr>
<td><em>docs</em><td>
<td>Documentation</td>
</tr>

<tr>
<td><em>examples</em><td>
<td>Examples demonstrating various SDK features</td>
</tr>

<tr>
<td><em>lib</em><td>
<td>3rd party libraries used by examples and unit tests</td>
</tr>

<tr>
<td><em>splunk</em><td>
<td>Source for <code>com.splunk</code></td>
</tr>

<tr>
<td><em>splunk-sdk</em><td>
<td>Source for <code>com.splunk.sdk</code>, utilities shared by examples and units</td>
</tr>

<tr>
<td><em>tests</em><td>
<td>Source for unit tests</td>
</tr>
</table>

### Changelog

The file CHANGELOG.md in the root of the repository contains a description
of changes for each version of the SDK. You can also find it online at:

* https://github.com/splunk/splunk-sdk-java/blob/master/CHANGELOG.md

### Branches

The `master` branch always represents a stable and released version of the SDK.
You can read more about our branching model on our Wiki:

* https://github.com/splunk/splunk-sdk-java/wiki/Branching-Model

## Resources

You can find anything having to do with developing on Splunk at the Splunk
developer portal:

* http://dev.splunk.com

You can also find reference documentation for the REST API:

* http://docs.splunk.com/Documentation/Splunk/latest/RESTAPI

For an introduction to the Splunk product and some of its capabilities:

* http://docs.splunk.com/Documentation/Splunk/latest/User/SplunkOverview

## Community

Stay connected with other developers building on Splunk.

<table>

<tr>
<td><em>Email</em></td>
<td>devinfo@splunk.com</td>
</tr>

<tr>
<td><em>Issues</em>
<td>https://github.com/splunk/splunk-sdk-java/issues/</td>
</tr>

<tr>
<td><em>Answers</em>
<td>http://splunk-base.splunk.com/tags/java/</td>
</tr>

<tr>
<td><em>Blog</em>
<td>http://blogs.splunk.com/dev/</td>
</tr>

<tr>
<td><em>Twitter</em>
<td>@splunkdev</td>
</tr>

</table>

### How to contribute

We aren't ready to accept code contributions yet, but will be shortly.  Check 
this README for more updates soon.

### Support

* SDKs in Preview will not be Splunk supported. Once the Java SDK moves to an
  Open Beta we will provide more detail on support.  

* Issues should be filed here: https://github.com/splunk/splunk-sdk-java/issues

### Contact Us

You can reach the Dev Platform team at devinfo@splunk.com

