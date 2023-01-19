[![Java SDK Test](https://github.com/splunk/splunk-sdk-java/actions/workflows/test.yml/badge.svg?branch=master)](https://github.com/splunk/splunk-sdk-java/actions/workflows/test.yml)
# The Splunk Software Development Kit for Java

#### Version 1.9.3

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
[Installing & Running Splunk](http://dev.splunk.com/view/SP-CAAADRV). The Splunk SDK for Java has been tested with Splunk Enterprise 9.0 and 8.2.

#### Splunk SDK for Java

[Get the Splunk SDK for Java](http://dev.splunk.com/view/SP-CAAAECN)&mdash;download the SDK as a ZIP, then extract the files and build the SDK. Or, download the JAR and add it to your project.

If you want to contribute to the SDK, clone the repository from [GitHub](https://github.com/splunk/splunk-sdk-java).

#### Java using Maven

You can use [Apache Maven](http://maven.apache.org/) to build your Splunk SDK for Java projects. With a few updates to your project's `pom.xml` file, it will retrieve all necessary dependencies and seamlessly build your project.

To add the Splunk SDK for Java `.JAR` file as a dependency:

1. Add the repository to your project's `pom.xml` file:

```xml
<repositories>
  ...
  <repository>
    <id>splunk-artifactory</id>
    <name>Splunk Releases</name>
    <url>http://splunk.jfrog.io/splunk/ext-releases-local</url>
  </repository>
</repositories>
```

2. Add the dependency to the `pom.xml` file:

```xml
<dependencies>
  ...
  <dependency>
    <groupId>com.splunk</groupId>
    <artifactId>splunk</artifactId>
    <version>1.9.3</version>
  </dependency>
</dependencies>
```

Be sure to update the version number to match the version of the Splunk SDK for Java that you are using.

> Note: You can make similar changes to use [Gradle](http://www.gradle.org/) as well.

### Building the SDK and documentation

To build the SDK, open a command prompt in the **/splunk-sdk-java**
directory and enter:

    mvn

or

    mvn package

This command builds all of the .class and .jar files. If you just want to build
the .class files, enter:

    mvn compile

To remove all build artifacts from the repository, enter:

    mvn clean

To build the documentation for the SDK, it is being automatically generated with <b>mvn package</b>, otherwise enter:

    cd splunk
    mvn javadoc:javadoc

###SSL Certificate Verification
SSL Certificate validation is turned ON by default in Splunk Java SDK. Set SSL Certificate as shown below.
```java
    HttpService.setSSLCert(<byte[] sslCert>);
```
Note:- For local/Non-production/any other use cases SSL Certificate validation can be disabled as shown below.
```java
    HttpService.setValidateCertificates(false).
```


### Usage
#### Login using username and password
```java
import com.splunk.HttpService;
import com.splunk.Service;
import com.splunk.ServiceArgs;

/**
 * Login using username and password
 */
public class SplunkLogin {

    static Service service = null;
    public static void main(String args[]) {
        ServiceArgs loginArgs = new ServiceArgs();
        loginArgs.setPort(8089);
        loginArgs.setHost("localhost");
        loginArgs.setScheme("https");
        loginArgs.setUsername("USERNAME"); // Use your username
        loginArgs.setPassword("PASSWORD"); // Use your password

        //set SSL Certificate for verification
        byte[] sslCert = <read Certificate file into byte array>
        HttpService.setSSLCert(sslCert);
        // Initialize the SDK client
        service = Service.connect(loginArgs);
    }
}
```

#### Login using Session Token
```java
import com.splunk.HttpService;
import com.splunk.Service;
import com.splunk.ServiceArgs;

/**
 * Login using Session token
 */
public class SplunkLogin {

    static Service service = null;
    /**
     * Session Token.
     * Actual token length would be longer than this token length.
     */
    static String token = "1k_Ostpl6NBe4iVQ5d6I3Ohla_U5";
    
    public static void main(String args[]) {
        ServiceArgs loginArgs = new ServiceArgs();
        loginArgs.setPort(8089);
        loginArgs.setHost("localhost");
        loginArgs.setScheme("https");
        loginArgs.setToken(String.format("Splunk %s", token));

        //set SSL Certificate for verification
        byte[] sslCert = <read Certificate file into byte array>
        HttpService.setSSLCert(sslCert);
        // Initialize the SDK client
        service = Service.connect(loginArgs);
    }
}
```
* Login using username and password will create Session token internally.
* Login using Credentials (username & password) OR directly using Session token are similar.
* In above two approaches, there is one limitation that expiration time of Session token cannot be extended. User has to re-login every time when token expires.
* To overcome this limitation, **Authentication** token is used instead of Session token.
* In **Authentication** token, user has a provision to set token expiration time. Splunk allows user to set relative/absolute time for token expiration.
* In other words, **Authentication** token is configurable whereas Session token cannot be configured.

#### Login using Authentication Token (RECOMMENDED)
```java
import com.splunk.HttpService;
import com.splunk.Service;
import com.splunk.ServiceArgs;

/**
 * Login using Authentication token
 */
public class SplunkLogin {

    static Service service = null;
    /**
     * Authentication Token.
     * Actual token length would be longer than this token length.
     */
    static String token = "1k_Ostpl6NBe4iVQ5d6I3Ohla_U5";
    
    public static void main(String args[]) {
        ServiceArgs loginArgs = new ServiceArgs();
        loginArgs.setPort(8089);
        loginArgs.setHost("localhost");
        loginArgs.setScheme("https");
        loginArgs.setToken(String.format("Bearer %s", token));

        //set SSL Certificate for verification
        byte[] sslCert = <read Certificate file into byte array>
        HttpService.setSSLCert(sslCert);
        // Initialize the SDK client
        service = Service.connect(loginArgs);
    }
}
```

#### Example of running a simple search by first creating the search job
```java
import com.splunk.Job;
import com.splunk.ResultsReader;
import com.splunk.ResultsReaderXml;
import com.splunk.HttpService;
import com.splunk.Service;
import com.splunk.ServiceArgs;

/**
 * Logged in using Authentication token.
 * Assuming that authentication token is already created from Splunk web.
 * Create Job using search creation.
 * Read results and print _raw fields
 */
public class SearchExample {

    static Service service = null;

    /**
     * Authentication Token.
     * Actual token length would be longer than this token length.
     */
    static String token = "1k_Ostpl6NBe4iVQ5d6I3Ohla_U5";
    
    public static void main(String args[]) {

        ServiceArgs loginArgs = new ServiceArgs();
        loginArgs.setPort(8089);
        loginArgs.setHost("localhost");
        loginArgs.setScheme("https");
        loginArgs.setToken(String.format("Bearer %s", token));

        //set SSL Certificate for verification
        byte[] sslCert = <read Certificate file into byte array>
        HttpService.setSSLCert(sslCert);
        // Initialize the SDK client
        service = Service.connect(loginArgs);

        // Run a simple search by first creating the search job
        Job job = service.getJobs().create("search index=_internal | head 10");

        // Waiting for search results to be ready
        while (!job.isReady()) {
            try {
                Thread.sleep(500); // 500 ms
            } catch (Exception e) {
                // Handle exception here.
            }
        }

        // Read results
        try {
            ResultsReader reader = new ResultsReaderXml(job.getEvents());

            // Iterate over events and print _raw field
            reader.forEach(event -> System.out.println(event.get("_raw")));

        } catch (Exception e) {
            // Handle exception here.
        }
    }
}
```

For more information on authentication using tokens, please visit [Splunk Docs](https://docs.splunk.com/Documentation/Splunk/latest/Security/Setupauthenticationwithtokens).

### Unit tests

The Splunk SDK for Java includes several unit tests that are run at
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

#### Run unit tests

To run the SDK unit tests, open a command prompt in the **/splunk-sdk-java**
directory and enter:

    mvn test

You can also run specific test classes by passing the class to the -Dtest=
option, e.g.,

    mvn test -Dtest=AtomFeedTest

The maven configuration can also produce an HTML report of all the tests automatically when **mvn package / mvn test** are executed.
Alternate way to generate report is using below command under splunk directory:

    mvn jacoco:report

The report will be written in **/splunk-sdk-java/splunk/target/site/surefire-report.html**.

It's also possible to run the units within Java IDEs such as IntelliJ and
Eclipse. For example, to open the Splunk SDK for Java project in Eclipse:

1. Click **File**, **Import**.
2. Click **General**, **Existing Projects into Workspace**, then click
   **Next**.
3. In **Select root directory**, type the path to the Splunk SDK for Java root
   directory (or click **Browse** to locate it), then click **Finish**.

#### Measure code coverage

Measurement of code coverage is generated along with <b>mvn package / mvn test</b>:

    mvn jacoco:report

To view the coverage report, open
**/splunk-sdk-java/splunk/target/test-report/index.html** in your web browser.

## Repository

<table>
<tr>
<td><b>/argsGenerator</b></td>
<td>This directory is created by the build and contains intermediate build
ouputs</td>
</tr>

<tr>
<td><b>/splunk/target</b></td>
<td>This directory is created by the build and contains intermediate build
ouputs</td>
</tr>

<tr>
<td><b>/splunk/src/main</b></td>
<td>Source for <code>com.splunk</code></td>
</tr>

<tr>
<td><b>/splunk/src/test</b></td>
<td>Source for unit tests</td>
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
