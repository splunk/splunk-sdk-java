[![Build Status](https://travis-ci.org/splunk/splunk-sdk-java.svg?branch=master)](https://travis-ci.org/splunk/splunk-sdk-java)
# The Splunk Software Development Kit for Java

#### Version 1.6.5

The Splunk Software Development Kit (SDK) for Java contains library code and examples designed to enable developers to build applications using the Splunk platform.

The Splunk platform is a search engine and analytic environment that uses a distributed map-reduce architecture to efficiently index, search, and process large time-varying data sets.

The Splunk platform is popular with system administrators for aggregation and monitoring of IT machine data, security, compliance, and a wide variety of other scenarios that share a requirement to efficiently index, search, analyze, and generate real-time notifications from large volumes of time-series data.

The Splunk developer platform enables developers to take advantage of the same technology used by the Splunk platform to build exciting new applications.

For more information, see [Splunk Enterprise SDK for Java](https://dev.splunk.com/enterprise/docs/devtools/java/sdk-java/) on the Splunk Developer Portal.

## Getting started with the Splunk SDK for Java

The Splunk SDK for Java contains library code and examples that show how to programmatically interact with the Splunk platform for a variety of scenarios including searching, saved searches, data inputs, and many more, along with building complete applications.

### Requirements

Here's what you need to get going with the Splunk SDK for Java.

*  Splunk
  
   If you haven't already installed Splunk, download it [here](http://www.splunk.com/download). 
   For more information, see the Splunk Enterprise [_Installation Manual_](https://docs.splunk.com/Documentation/Splunk/latest/Installation).
    
*  Splunk SDK for Java
  
   Get the JAR from the [Splunk Developer Portal](https://dev.splunk.com/enterprise/downloads/) or clone the repository from [GitHub](https://github.com/splunk/splunk-sdk-java) if you want to contribute to the SDK. To use Maven, see "Use Maven to build projects" below.
   
*  Java version 6 or higher, from [OpenJDK](https://openjdk.java.net) or [Oracle](https://www.oracle.com/technetwork/java). For instructions, see [Java Platform Installation](http://www.oracle.com/technetwork/java/javase/index-137561.html) on the Oracle website.

   The Splunk SDK for Java is compatible with Java 8. Be aware that **Java 8 disables Secure Sockets Layer version 3 (SSLv3) by default**, so you will need to use Transport Layer Security (TLS) instead. To see an example of how to do this, see the [ssl_protocols](https://github.com/splunk/splunk-sdk-java/blob/master/examples/com/splunk/examples/ssl_protocols/Pro...) example. Alternatively, you can re-enable SSLv3 in Java settings, although this is not recommended.

*  Ant, from the [Apache website](http://ant.apache.org/bindownload.cgi). For instructions, see [Installing Apache Ant](http://ant.apache.org/manual/install.html). 

### Set system variables (Windows)

If you are using Windows, make sure the following system variables are created and set:
*   **ANT_HOME** should be set to the location where Ant is installed.
*   **JAVA_HOME** should be set to the directory where the JDK is installed.
*   **PATH** should include the path to the **%ANT_HOME%\bin** directory.

### Use Maven to build projects
   
   You can use [Apache Maven](http://maven.apache.org/) to build your Splunk SDK for Java projects with a few updates to your project's **pom.xml** file. You can retrieve all necessary dependencies and build your project.
   
   To add the Splunk SDK for Java JAR file as a dependency:
   
   1. Add the repository to your project's **pom.xml** file:

      ```
      <repositories>
        ...
        <repository>
          <id>splunk-artifactory</id>
          <name>Splunk Releases</name>
          <url>http://splunk.jfrog.io/splunk/ext-releases-local</url>
        </repository>
      </repositories>
      ```

   2. Add the dependency to the **pom.xml** file and update the version number to match the version of the Splunk SDK for Java that you are using:

      ```
      <dependencies>
        ...
        <dependency>
          <groupId>com.splunk</groupId>
          <artifactId>splunk</artifactId>
          <version>1.6.5.0</version>
        </dependency>
      </dependencies>
      ```

    >**Note**: You can make similar changes to use [Ivy](http://ant.apache.org/ivy/history/latest-milestone/tutorial/start.html) or [Gradle](http://www.gradle.org/) as well.

### Use ant to build the SDK and documentation

To build the SDK (all of the CLASS and JAR files), open a command prompt in the **/splunk-sdk-java** directory and enter either of these commands: 
  
    ant

or
    
    ant dist

If you want to build only the CLASS files, enter:
  
    ant build

To remove all build artifacts from the repository, enter:
  
    ant clean

To build the documentation for the SDK, enter:
    
    ant javadoc


### Examples and unit tests

The Splunk SDK for Java includes several examples and unit tests to run at the command line.

#### Create a .splunkrc convenience file

To connect to Splunk Enterprise, many of the SDK examples and unit tests take command-line arguments that specify values for the host, port, and login credentials for Splunk Enterprise. For convenience during development, you can store these arguments as key-value pairs in a text file named **.splunkrc**. Then, the SDK examples and unit tests use the values from the **.splunkrc** file when you don't specify them.

>**Note**: Storing login credentials in the **.splunkrc** file is only for convenience during development. This file isn't part of the Splunk platform and shouldn't be used for storing user credentials for production. And, if you're at all concerned about the security of your credentials, enter them at the command line rather than saving them in this file.

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
    # Your version of Splunk
    version=8.0

Save the file as **.splunkrc** in the current user's home directory.

*   For example on OS X, save the file as:

        ~/.splunkrc

*   On Windows, save the file as:

        C:\Users\currentusername\.splunkrc

    You might get errors in Windows when you try to name the file because ".splunkrc" appears to be a nameless file with an extension. You can use the command line to create this file by going to the **C:\Users\<currentusername>** directory and entering the following command:

        Notepad.exe .splunkrc

    Click **Yes**, then continue creating the file.


#### Run examples

After you build the SDK, examples are put in the **/splunk-sdk-java/dist/examples** directory. To run the examples, run the Java interpreter at the command line using the `-jar` flag to specify the target example JAR file, and include any arguments that are required by the example. To get help for an example, use the `--help` argument with an example.

For example, to see the command-line arguments for the Search example, open a command prompt in the **/splunk-sdk-java** directory and enter:

    java -jar dist/examples/search.jar --help

To run the Search example, open a command prompt in the **/splunk-sdk-java** directory and enter:

    java -jar dist/examples/search.jar "search * | head 10" --output_mode=csv

There is also a helper script called run in the root of the repository that simplifies running the SDK examples. For example, on Mac OS X you could enter:

    ./run search "search * | head 10" --output_mode=csv

All the the example jars are completely self contained and can be used independently of the SDK's repository.

#### Run unit tests

To run the SDK unit tests, open a command prompt in the **/splunk-sdk-java** directory and enter:

    ant test

To run the units from anywhere in the repository, enter:

    ant test -find

You can run specific test classes by passing the class to the `-Dtestcase=` option: 

    ant test -Dtestcase=AtomFeedTest

The ant configuration can produce a single HTML report of all the tests run using the target testreport (which also takes the `-Dtestcase=` option): 

    ant testreport

The report is written in **build/reports/tests/index.html**.

You can also run the units within Java IDEs such as IntelliJ and Eclipse. For example, to open the Splunk SDK for Java project in Eclipse:

  1. Click **File**, then click **Import**.
  2. Click **General**, click **Existing Projects into Workspace**, then click **Next**.
  3. In **Select root directory**, type the path to the Splunk SDK for Java root directory (or click **Browse** to locate it), then click **Finish**.

#### Measure code coverage

To measure the code coverage of the test suite, open a
command prompt in the **/splunk-sdk-java** directory and enter:

    ant coverage

To run code coverage from anywhere in the repository, enter:

    ant coverage -find

To view the coverage report, open
**/splunk-sdk-java/build/reports/coverage/index.html** in your web browser.

## Repository

| Directory      | Description                                                                  |
|:-------------- |:---------------------------------------------------------------------------- |
| /argsGenerator | This directory is created by the build and contains intermediate build ouputs|
|/build          | This directory is created by the build and contains intermediate build ouputs|
|/dist           | This directory is created by the build and contains final build outputs      |
|/examples       | Examples demonstrating various SDK features                                  |
|/lib            | Third-party libraries used by examples and unit tests                        |
|/splunk         | Source for `com.splunk`                                                      |
|/tests          | Source for unit tests                                                        |
|/util           | Utilities shared by examples and units                                       |

### Changelog

The [CHANGELOG](CHANGELOG.md) contains a description of changes for each version of the SDK. For the latest version, see the [CHANGELOG.md](https://github.com/splunk/splunk-sdk-java/blob/master/CHANGELOG.md) on GitHub.

### Branches

The **master** branch represents a stable and released version of the SDK.
To learn about our branching model, see [Branching Model](https://github.com/splunk/splunk-sdk-java/wiki/Branching-Model) on GitHub.

## Documentation and resources

| Resource                | Description |
|:----------------------- |:----------- |
| [Splunk Developer Portal](http://dev.splunk.com) | General developer documentation, tools, and examples |
| [Integrate the Splunk platform using development tools for Java](https://dev.splunk.com/enterprise/docs/devtools/java)| Documentation for Java development |
| [Splunk SDK for Java Reference](http://docs.splunk.com/Documentation/JavaSDK) | SDK API reference documentation |
| [REST API Reference Manual](https://docs.splunk.com/Documentation/Splunk/latest/RESTREF/RESTprolog) | Splunk REST API reference documentation |
| [Splunk>Docs](https://docs.splunk.com/Documentation) | General documentation for the Splunk platform |
| [GitHub Wiki](https://github.com/splunk/splunk-sdk-java/wiki/) | Documentation for this SDK's repository on GitHub |

## Community

Stay connected with other developers building on the Splunk platform.

* [Email](mailto:devinfo@splunk.com)
* [Issues and pull requests](https://github.com/splunk/splunk-sdk-java/issues/)
* [Community Slack](https://splunk-usergroups.slack.com/app_redirect?channel=appdev)
* [Splunk Answers](https://community.splunk.com/t5/Splunk-Development/ct-p/developer-tools)
* [Splunk Blogs](https://www.splunk.com/blog)
* [Twitter](https://twitter.com/splunkdev)


### Contributions

If you would like to contribute to the SDK, see [Contributions to Splunk](https://www.splunk.com/en_us/form/contributions.html).

### Support

 *  You will be granted support if you or your company are already covered under an existing maintenance/support agreement. Submit a new case in the [Support Portal](https://www.splunk.com/en_us/support-and-services.html) and include "Splunk SDK for Java" in the subject line.

   If you are not covered under an existing maintenance/support agreement, you can find help through the broader community at [Splunk Answers](https://community.splunk.com/t5/Splunk-Development/ct-p/developer-tools).

*  Splunk will NOT provide support for SDKs if the core library (the code in the <b>/splunk</b> directory) has been modified. If you modify an SDK and want support, you can find help through the broader community and [Splunk Answers](https://community.splunk.com/t5/Splunk-Development/ct-p/developer-tools). 

   We would also like to know why you modified the core library, so please send feedback to _devinfo@splunk.com_.

*  File any issues on [GitHub](https://github.com/splunk/splunk-sdk-java/issues).

### Contact Us

You can reach the Splunk Developer Platform team at _devinfo@splunk.com_.

## License

The Splunk Software Development Kit for Java is licensed under the Apache License 2.0. See [LICENSE](LICENSE) for details.
