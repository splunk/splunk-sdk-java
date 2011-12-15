# The Splunk Software Development Kit for Java (Preview Release)

This SDK contains library code and examples designed to enable developers to
build applications using Splunk.

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

## License

The Splunk Software Development Kit for Java is licensed under the Apache
License 2.0. Details can be found in the file LICENSE.

## This SDK is a Preview Release

1.  This Preview release a pre-beta release.  There will also be a beta 
    release prior to a general release. It is incomplete and may have bugs.

2.  The Apache license only applies to the SDK and no other Software provided 
    by Splunk.

3.  Splunk in using the Apache license is not providing any warranties, 
    indemnification or accepting any liabilities  with the Preview SDK.

4.  Splunk is not accepting any Contributions to the Preview release of 
    the SDK.  
    All Contributions during the Preview SDK will be returned without review.

## Getting started with the Splunk Java SDK

The Splunk Java SDK contains library code and examples that show how to 
programmatically interact with Splunk for a variety of scenarios including 
searching, saved searches, data inputs, and many more, along with building 
complete applications. 

### Requirements

Here's what you need to get going with the Splunk Java SDK.

#### Splunk

If you haven't already installed Splunk, download it here: 
http://www.splunk.com/download. For more about installing and running Splunk 
and system requirements, see Installing & Running Splunk 
(http://dev.splunk.com/view/SP-CAAADRV). 

#### Splunk Java SDK

Get the Splunk Java SDK from GitHub (https://github.com/) and clone the 
resources to your computer. For example, use the following command: 

>  git clone https://github.com/splunk/splunk-sdk-Java.git

#### Java and Ant

You'll need Java SE version 6 or higher, which you can download from the 
Oracle web site 
(http://www.oracle.com/technetwork/java/javase/downloads/index.html). 

You'll also need Ant, which you can install from the Apache website 
(http://ant.apache.org/bindownload.cgi). 

If you are using Windows, you'll need to make sure the following system 
variables are created and set: 

*   ANT_HOME should be set to the location where Ant is installed.

*   JAVA_HOME should be set to the directory where the JDK is installed.

*   PATH should include the path to the %ANT_HOME%\bin directory.

For full installation instructions, you can find more information here:
 
*   Java Platform Installation 
    (http://www.oracle.com/technetwork/java/javase/index-137561.html)

*   Installing Apache Ant 
    (http://ant.apache.org/manual/install.html)

### Building the SDK and documentation

To build the SDK, open a command prompt at the root of the Splunk Java SDK 
repository and enter: 

>  ant

To remove all build artifacts from the repository, enter:

>  ant clean

To build the documentation for the SDK, enter: 

>  ant javadoc

If you are interested in more control of the build process, see the build.xml 
file. You can also type the following command from anywhere in the repository 
and Ant will find the root build file:

>  ant {target} -find

### Examples and unit tests

The Splunk Java SDK includes several examples and unit tests that are run at 
the command line. 


#### Set up the .splunkrc file

To connect to Splunk, many of the SDK examples and unit tests take command-
line arguments that specify values for the host, port, and login credentials 
for Splunk. For convenience during development, you can store these arguments 
as key-value pairs in a text file named .splunkrc. Then, when you don't 
specify these arguments at the command line, the SDK examples and unit tests 
use the values from the .splunkrc file. 

To use a .splunkrc file, create a text file with the following format:

    # Host at which Splunk is reachable (OPTIONAL)
    host=localhost
    # Port at which Splunk is reachable (OPTIONAL)
    # Use the admin port, which is 8089 by default.
    port=8089
    # Splunk username
    username=admin
    # Splunk password
    password=changeme
    # Access scheme (OPTIONAL)
    scheme=https
    # Application context (OPTIONAL)
    app=MyApp
    # Owner context (OPTIONAL)
    owner=User1

Save the file as .splunkrc in the current user's home directory.

*   For example on Mac OS X, save the file as: 

    >  ~/.splunkrc

*   On Windows, save the file as: 

    >  C:\Users\currentusername\\.splunkrc

    You might get errors in Windows when you try to name the file because
    ".splunkrc" looks like a nameless file with an extension. You can use
    the command line to create this file--go to the 
    C:\Users\currentusername directory and enter the following command: 

    >  Notepad.exe .splunkrc

    Click Yes, then continue creating the file.

NOTE: Storing login credentials in the .splunkrc file is only for convenience 
during development—this file isn't part of the Splunk platform and 
shouldn't be used for storing user credentials for production. And, if you're 
at all concerned about the security of your credentials, just enter them at 
the command line rather than saving them in the .splunkrc file. 


#### Run examples

After you build the SDK, examples are put in the splunk-sdk-
java/dist/examples directory. To run the examples, run the Java interpreter 
at the command line using the -jar flag to specify the target example jar 
file, and include any arguments that are required by the example. To get help 
for an example, use the --help argument with an example. 

For example, to see the command-line arguments for the Search example, open a 
command prompt in the splunk-sdk-java directory and enter: 

>  java -jar dist/examples/search.jar --help

To run the Search example, open a command prompt in the splunk-sdk-java 
directory and enter: 

>  java -jar dist/examples/search.jar "search * | head 10" --output_mode=csv

There is also a helper script called run in the root of the repository that 
simplifies running the SDK examples. For example, on Mac OS X you could 
simply enter: 

>  ./run search "search * | head 10" --output_mode=csv

#### Run unit tests

To run the SDK unit tests, open a command prompt at the root of the Splunk 
Java SDK repository and enter: 

>  ant test

To run the units from anywhere in the repository, enter:

>  ant test -find

It's also possible to run the units within Java IDEs such as IntelliJ and 
Eclipse. For example, to open the Splunk Java SDK project in Eclipse: 
1.  Click File, Import. 
2.  Click General, Existing Projects into Workspace, then click Next. 
3.  In Select root directory, type the path to the Splunk Java SDK root
    directory (or click Browse to locate it), then click Finish. 

## The Splunk Java SDK components

The Splunk developer platform consists of three primary components: splunkd, 
the engine; splunkweb, the app framework that sits on top of the engine; and 
the Splunk SDKs that interface with the REST API and extension points.

The Splunk Java SDK lets you target splunkd by making calls against the 
engine's REST API and accessing the various splunkd extension points such as 
custom search commands, lookup functions, scripted inputs, and custom REST 
handlers.

For more information about the API, see the Splunk Java SDK documentation 
(http://splunk.github.com/splunk-sdk-java/docs/0.1.0/index.html).

### Service

The Service class is the primary entry point for the client library. 
Construct an instance of the Service class and provide any arguments that are 
required to connect to an available Splunk server. Once the Service instance 
is created, call the login() method and provide login credentials. Once you 
have an authenticated Service instance, you can use it to navigate, 
enumerate, and operate on a wide variety of Splunk resources. You can also 
use it to issue searches and make REST API calls. 

The following example creates a Service instance and prints the Splunk 
version number:

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
various scenarios that require different combinations of arguments. In the 
most general case, the Service class takes a map of arguments, which 
simplifies passing large and varying combinations of arguments, as shown in 
the following example:

    Map<String, Object> args = new HashMap<String, Object>();
    args.put("host", "localhost");
    args.put("port", 8089);
    Service service = new Service(args);
    service.login("admin", "changeme");

The Service class also provides a static helper method that takes a map and 
combines the construction and authentication of the instance into a single 
call. Because of its flexibility, it is the method that is used most often in 
the SDK examples and unit tests. The following example shows how to use this 
helper method:

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

The Splunk REST API consists of over 160 endpoints that provide access to 
almost every feature of Splunk. The majority of the Splunk Java SDK API 
follows a convention of exposing resources as collections of entities, where 
an entity is a resource that has properties, actions, and metadata that 
describes the entity. The entity/collection pattern provides a consistent 
approach to interacting with resources and collections of resources. 

For example, the following code prints all Splunk users:

    Service service = Service.connect(...);
    for (User user : service.getUsers().values())
        System.out.println(user.getName());

Similarly, the following code prints all the Splunk apps:

    Service service = Service.connect(...);
    for (Application app : service.getApplications().values())
    System.out.println(app.getName());

Collections use a common mechanism to create and remove entities. Entities 
use a common mechanism to retrieve and update property values, and access 
entity metadata. Once you're familiar with this pattern, you'll have a 
reasonable understanding of how the SDK and underlying REST API work. 

The SDK contains the base classes Entity and EntityCollection, both of which 
derive from the common base class Resource. Note that Service is not a 
Resource, but is a container that provides access to all features associated 
with a Splunk instance. 

The class hierarchy for the core SDK library is as follows:

    Service
    Resource
        Entity
        ResourceCollection
            EntityCollection

### Search

One of the primary features of Splunk is running searches and retrieving 
search results. The following code example shows how to create a search job, 
poll for completion, and retrieve the result stream:

    Service service = Service.connect(...);
    Job job = service.getJobs().create("search * | head 10")
    while (!job.isDone()) {
        Thread.sleep(2000);
        job.refresh();
    }
    InputStream stream = job.getResults();

The SDK API has many features such as real-time search, numerous search 
options, various types of search results, and the ability to execute 
synchronous and asynchronous search requests. To explore core search 
features, see the Search example included in the SDK. 


### Client state

_TODO_

## Repository

<table>
<tr>
<td><em>build</em><td>
<td>This directory is created by the build and contains intermediate build 
ouputs</td>
</tr>

<tr>
<td><em>dist</em><td>
<td>This directory is created by the build and contains final build 
outputs</td>
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
<td>Source for <code>com.splunk.sdk</code>, utilities shared by examples and 
units</td>
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

The `master` branch always represents a stable and released version of the 
SDK.
You can read more about our branching model on our Wiki:

* https://github.com/splunk/splunk-sdk-java/wiki/Branching-Model

## Resources

You can find anything having to do with developing on Splunk at the Splunk
developer portal:

* http://dev.splunk.com

For Splunk Java SDK reference documentation, see: 

* http://splunk.github.com/splunk-sdk-java/docs/0.1.0/index.html

You can also find reference documentation for the REST API:

* http://docs.splunk.com/Documentation/Splunk/latest/RESTAPI

For an introduction to the Splunk product and some of its capabilities:

* http://docs.splunk.com/Documentation/Splunk/latest/User/SplunkOverview

For more information on the SDK and this repository check out our GitHub Wiki

* https://github.com/splunk/splunk-sdk-java/wiki/

## Community

Stay connected with other developers building on Splunk.

<table>

<tr>
<td><em>Email</em></td>
<td>devinfo@splunk.com</td>
</tr>

<tr>
<td><em>Issues</em>
<td><span>https://github.com/splunk/splunk-sdk-java/issues/</span></td>
</tr>

<tr>
<td><em>Answers</em>
<td><span>http://splunk-base.splunk.com/tags/java/</span></td>
</tr>

<tr>
<td><em>Blog</em>
<td><span>http://blogs.splunk.com/dev/</span></td>
</tr>

<tr>
<td><em>Twitter</em>
<td>@splunkdev</td>
</tr>

</table>

### How to contribute

We aren't ready to accept code contributions yet, but will be shortly. Check 
this README for more updates soon.

### Support

* SDKs in Preview will not be Splunk supported. Once the Java SDK moves to an
  Open Beta we will provide more detail on support.  

* Issues should be filed here: 
  https://github.com/splunk/splunk-sdk-java/issues

### Contact Us

You can reach the Dev Platform team at devinfo@splunk.com

