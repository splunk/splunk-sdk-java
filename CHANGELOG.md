# Splunk Enterprise SDK for Java Changelog

## Version 1.9.5

### New Features and APIs
* Added static method _addClusterMasterURIsToHosts_ in HttpService class to update list of Valid Hosts with Cluster Master Hosts (GitHub PR [#215](https://github.com/splunk/splunk-sdk-java/pull/215))
* Added instance method _getClusterMasters_ in Service class to get list of cluster master hosts

## Version 1.9.4

### Minor Changes
* Added check for localhost IPv6 address, for IPv6 compatible apps (GitHub PR [#210](https://github.com/splunk/splunk-sdk-java/pull/210))
* Updating SSL_SOCKET_FACTORY instance on changing _validateCertificates_ flag.  (GitHub PR [#206](https://github.com/splunk/splunk-sdk-java/pull/210))

## Version 1.9.3

### Minor Changes
* Re-fetch logic for instancetype and version fields if not set within Service instance to avoid NPE (GitHub PR [#202](https://github.com/splunk/splunk-sdk-java/pull/202))
* Check for local IP as alternative to _localhost_ within HostnameVerifier, addressing issue with certain local workflows

## Version 1.9.2

### New Features and APIs
* Added feature that allows to update ACL properties of an entity (GitHub PR [#196](https://github.com/splunk/splunk-sdk-java/pull/196))

### Minor Changes
* Added null check for child to handle error when no value is passed for a parameter in modular-inputs (Ref issue [#198](https://github.com/splunk/splunk-sdk-java/issues/198) & GitHub PR [#199](https://github.com/splunk/splunk-sdk-java/pull/199))

## Version 1.9.1

### New Features and APIs
* SDK Support for third-party (Load Balancer) "sticky sessions"(cookie persistence) (Github PR [#192](https://github.com/splunk/splunk-sdk-java/pull/192))
* Added Args option for Saved Search history method (GitHub Issue [#126](https://github.com/splunk/splunk-sdk-java/issues/126) & PR [#188](https://github.com/splunk/splunk-sdk-java/pull/188) )

### Minor Changes
* Special handling related to the semantic versioning of specific Search APIs functional in Splunk Enterprise 9.0.2 and (Splunk Cloud 9.0.2209). These SDK changes will enable seamless transition between the APIs based on the version of the Splunk Enterprise/Cloud (Github PR [#193](https://github.com/splunk/splunk-sdk-java/pull/193))
* Updated checks to fetch Storage Passwords with wildcards in namespace. (GitHub PR [#187](https://github.com/splunk/splunk-sdk-java/pull/187))

## Version 1.9.0

### New Features and APIs
* SDK Support for splunkd search API changes, for Splunk 9.0+. (Github PR [#189](https://github.com/splunk/splunk-sdk-java/pull/189))

### Minor Changes
* Automated docs generation using GitHub actions. (Github PR [#184](https://github.com/splunk/splunk-sdk-java/pull/184))

## Version 1.8.0

### New Features and APIs
* Added a support to add custom headers in Service class. (Github PR [#176](https://github.com/splunk/splunk-sdk-java/pull/176)).
* SSL Certificate validation (default implementation) added. (Github PR [#175](https://github.com/splunk/splunk-sdk-java/pull/175)).
  * Boolean flag is introduced to skip/validate certificate. Use _HttpService.setValidateCertificates()_ to enable/disable certificate validation.
  * Breaking change: Certificate validation is now enforced by default, for local or non-production use cases use _HttpService.setValidateCertificates(false)_.
* Apps/app-install replaced with **apps/local**. (Github PR [#168](https://github.com/splunk/splunk-sdk-java/pull/168))
* Breaking change: HttpService.useTLS flag removed, please use _HttpService.setSslSecurityProtocol()_ to set a specific SSL/TLS implementation or else TLS v1.2 is used by default for Java 1.8.

### Minor Changes

* External Entities restricted in XML factory. (Github PR [#180](https://github.com/splunk/splunk-sdk-java/pull/180)).
  * Prevent expansion of external entities in Document Builder factory.
* Headers modified in Socket creation. (Github PR [#179](https://github.com/splunk/splunk-sdk-java/pull/179)).
  * Http Request uses raw request headers by including escape characters which seems vulnerable. It was replaced with PrintWriter methods to avoid escape characters.
  * Host parameter used in Socket is omitted to prevent exposing it to external users.
* README.md file modified with all login methods along with Splunk Search creation example. (Github PR [#177](https://github.com/splunk/splunk-sdk-java/pull/177)).
* Deploy plugin is removed from Splunk module pom to avoid redundancy. (Github PR [#172](https://github.com/splunk/splunk-sdk-java/pull/172)).
* Setter methods for Session and Bearer token added along with test case. (Github PR [#171](https://github.com/splunk/splunk-sdk-java/pull/171))
  * **Use:** service.setSplunkToken() for session tokens and service.setBearerToken() for long-lived tokens.
* Modular input folder name renamed based on newer splunk folder name validation. (Github PR [#168](https://github.com/splunk/splunk-sdk-java/pull/168))
* SDK app collection URL has been updated to v1.1.0 in docker compose file. (Github PR [#168](https://github.com/splunk/splunk-sdk-java/pull/168))
  * Test files in sdk app collections are modified based on python v3 syntax.
* Added Saved Search test case based on title. (Github PR [#166](https://github.com/splunk/splunk-sdk-java/pull/166))

## Version 1.7.1

### Minor Changes
* Dependency breaking changes are resolved.

## Version 1.7.0

### New Features and APIs
* Added Credits.md file along with licences. (Github PR [#162](https://github.com/splunk/splunk-sdk-java/pull/162)).
* Improved TLS implementation as default behavior and turned on hostname verification for all hosts. (Github PR [#158](https://github.com/splunk/splunk-sdk-java/pull/158)).

### Minor changes
* Replaced Travis CI with Github actions (Github PR [#161](https://github.com/splunk/splunk-sdk-java/pull/161)).
* Fixed Javadoc generation while project packaging. (Github PR [#159](https://github.com/splunk/splunk-sdk-java/pull/159)).
* Fixed breaking change of **Index.getMaxHotBuckets()** method behavior to return as String instead of int.
* SDK build is migrated from ant build tool to maven build tool (Github PR [#157](https://github.com/splunk/splunk-sdk-java/pull/157)).
  * Dependencies directory for ant build is removed and maven dependencies are added using pom.xml.
  * Dependencies are upgraded to its LTS version.
  * Project structure is re-organized with respect to Maven.

## Version 1.6.5

### Bug Fixes

* Fixed bug for push back buffer is full when exporting data in XML (GitHub PR [#125](https://github.com/splunk/splunk-sdk-java/pull/125)).

## Version 1.6.4

### Bug Fixes

* Fixed bug in modinput Windows shims that caused Splunk Enterprise to fail to restart (GitHub PR [#120](https://github.com/splunk/splunk-sdk-java/pull/120)).
* Fixed bug with data model endpoint on Splunk Enterprise 7+ (GitHub PR [#117](https://github.com/splunk/splunk-sdk-java/pull/117)).
* Fixed bug with invalid `Index.submit()` forming an invalid REST API path for namespaced services ([#118](https://github.com/splunk/splunk-sdk-java/pull/118)).
* Fixed bug with `Value.toDate(string value)` not being thread safe (GitHub PR [#109](https://github.com/splunk/splunk-sdk-java/pull/109)).

## Version 1.6.3

### New features and APIs

* Added the `updated` property on all `Resource` objects (GitHub PR [#104](https://github.com/splunk/splunk-sdk-java/pull/104)).

## Version 1.6.2

### Bug Fixes

* Update modular input shims to ensure Java processes are killed (GitHub issue [#92](https://github.com/splunk/splunk-sdk-java/issues/92)).

## Version 1.6.1

### Bug Fixes

* Fix authentication issues when the Splunk `Set-Cookie` header is not the first one.

## Version 1.6.0

### New features and APIs

* Added support for retrieving `Password` entities scoped by realm and username.
* Added getter methods for embed `SavedSearch` properties.
* Added support for custom `HttpURLConnection` connection timeouts on `HttpService`.
* Performance improvement to `SavedSearch.dispatch()`.
* Added getter methods to the `Job` class for retrieving `long` values (`getEventCountLong`, `getResultCountLong`, `getScanCountLong`).
* Added `setFieldList()` to the `JobExportArgs` class.
* Added support for the `manualRebuilds` `DataModel` setting.

### Bug Fixes

* Fixed `SavedSearch.Dispatch()` throwing a `NullPointerException` in some load-balanced search head clustering environments.
* Fixed non-limit `PivotFilter` constructing the wrong JSON blob.

### Minor changes

* Added support for Travis CI.

## Version 1.5.0

### New features and APIs

* Added support for cookie-based authentication, for Splunk 6.2+.

### Bug Fixes

* Fixed failure parsing XML responses. Pull Request #76.
* Fixed bug where `Job` is never ready leading to infinite loops.

### Minor changes

* The SDK is now properly compiled with the `Command` class used in examples.

## Version 1.4.0

### New features and APIs

* Added support for Java 8, when manually configuring the `Service` class to use TLSv1.2, TLSv1.1, or TLSv1 defined in the `SSLSecurityProtocol` enum. The default is still SSLv3.
* Allow setting a custom `SSLSocketFactory` on the `HTTPService` and `Service` classes.

### New examples

* `ssl_protocols`: tries to connect to Splunk over HTTPS using different SSL/TLS protocols, then using a custom SSL and TLS `SSLSocketFactory`.

### Minor changes

* The SDK is now compiled with the `Command` class used in examples.

## Version 1.3.2

### Performance improvements

* `Job` objects will only be refreshed by `isReady()` if the `Job` is not ready. This minimizes HTTP requests when getting properties of the `Job`.
* The `Service` class now has a `getJob()` method that is used to retrieve a `Job` object by its sid String. This is better than calling `service.getJobs().get(sid)`, which has the overhead of getting all `Job` objects from Splunk in order to access a single `Job`.

### New examples

* `endpoint_instantiation`: shows how to manually instantiate any Splunk REST API endpoint.
* `get_job`: shows how to get a `Job` by its sid using the new `Service.getJob()` method.

### Minor changes

* The `Entity` and `EntityCollection` classes each have a public constructor, so any Splunk endpoint can be manually instantiated.
* The `FiredAlert` class now has a `getParsedExpirationTime()` method that returns a `Date` object.
* Some test have been modified to work with the latest release of Splunk Enterprise (6.2.x).

## Version 1.3.1

* Removed deprecated functions from `DistributedConfiguration` class.
* Oneshot searches, called from `Index.upload`, can now be passed search arguments.
* Fixed a resource leak in the `Command.load` function.

## Version 1.3

### New features and APIs

* Added support for data models and pivots.

### Bug Fixes

* When setting the sourcetype of a modular input event, events are indexed properly.
  Previously Splunk would encounter an error and skip them. Also updated some of the modular input test data to reflect this change.
* ResultsReaderXml now works with alternate XML parser libraries.

## Version 1.2.2

### Bug fixes

* Hot fix to ResultsReaderXml to work with woodstox XML parsing library.

## Version 1.2.1

### New features and APIs

* The Splunk Enterprise SDK for Java is fully compatible with Splunk Enterprise 6.0 as of this release.

### Bug fixes

* **JobCollection.create()** previously invalidated the collection and refreshed it to see whether the job had
  appeared. This was problematic for Splunk Enterprise instances running many jobs at once. The method has been changed
  to only interact with the endpoint specific to the newly created job.
* Namespaces that contain special characters such as '@' in their owner or app are now handled correctly.

### Breaking changes

* Removed **Application.isManageable** and **Application.setManageable**, since they are deprecated or nonexistent
  in all supported Splunk versions as of this release.
* **OutputDefault.getMaxQueueSize** now returns a String instead of a long to match the behavior of
  **setMaxQueueSize**.

### Known issues

* Certain combinations of requests and restarts of splunkd can cause splunkd to hang on OS X v10.8 Mountain Lion and
  OS X v10.9 Mavericks running Splunk 6.0.0. This issue is not present when running Splunk Enterprise on earlier
  versions of OS X. This will be fixed in a future release.

* The modular input support in the Splunk Enterprise SDK for Java is not compatible with Windows Server 2003 or Windows Server
  2003 R2.

## Version 1.2

### New features and APIs

* Added support for building modular input scripts in Java using the Splunk Enterprise SDK for Java.

### Bug fixes

* Any errors returned by Splunk in formats besides XML (for example, when Splunk returns JSON
  if an error occurs during a search with output_mode=json), are passed on as is in the resulting
  HttpException, replacing the uninformative error about XML parsing that was produced before.

## Version 1.1

### Breaking changes

* The default setting for all search jobs is now `segmentation=none` unless
  you explicitly set it otherwise. This setting returns results as a raw-text
  string rather than a string in XML format.

* The `ResultReaderCsv` class no longer supports streams from the `Service.export` method.
  Instead, use the `ResultReaderXml` class with XML output, or use the `ResultReaderJson`
  class with JSON output.

### New features and APIs

* New classes have been added, `MultiResultsReaderXml` and `MultiResultsReaderJson`,
  to read search results streams with multiple result sets from `Service.Export` methods.

* The `ResultsReader` classes now support `Iterable` and `Iterator` interfaces.

* The `Event.getSegmentedRaw` method has been added to return raw data from events, preserving
  segmentation information.

### Bug fixes

* The `ServiceInfo` class now uses the `services/*` endpoint rather than the
  default namespace (`servicesNS/*`) for HTTP requests. This change is a workaround to
  avoid a bug in Splunk that returns HTTP code 403 when the `server/info` endpoint
  is accessed using certain namespaces.

* The `ResultsReaderXml` class can now read search results streams from the
  `Job.getResultsPreview` method.

## Version 1.0

### New features and APIs

* Specialized *args* classes have been added to make it easier to pass
  entity-specific arguments:
    - `CollectionArgs`
    - `IndexCollectionArgs`
    - `JobArgs`
    - `JobEventsArgs`
    - `JobExportArgs`
    - `JobResultsArgs`
    - `JobResultsPreviewArgs`
    - `JobSummaryArgs`
    - `SavedSearchCollectionArgs`
    - `SavedSearchDispatchArgs`

    These new *args* classes are used with the following methods:
    - `Service` constructor
    - `Service.getSavedSearches` method
    - `Service.getJobs` method
    - `Service.getIndexes` method
    - `Service.export` method
    - `JobCollection.create` method
    - `Job.getResults` method
    - `Job.getResultsPreview` method
    - `Job.getEvents` method
    - `Job.getSummary` method
    - `SavedSearch.dispatch` method

* `ResultsReader.getNextEvent` now returns an `Event` object, which provides
  better handling for multi-value fields. This change is backward-compatible
  with older code that expects a `HashMap<String, String>`. However this new
  `Event` object is read-only.

* Modular input functionality has been implemented (requiring Splunk 5.0+)
  and the following classes have been added:
    - `ModularInputKind`
    - `ModularInputKindArgument`

  The `InputCollection` class also now handles arbitrary input kinds represented
  by modular inputs. You can call `InputCollection.getInputKinds` to get the set
  of `InputKinds` on the connected Splunk instance.

* The `ReceiverBehavior` interface has been added to work with output streams.

* The `IndexCollection` class has been added as a specialized collection class
  for indexes.

* The `JobCollection` class has been added as a specialized collection class for
  jobs.

* You can now programatically remove indexes using the `IndexCollection.remove`
  method (requires Splunk 5.0+).

* You can now send data to an input using the `TcpInputs.attach`,
  `TcpInputs.submit`, and `UdpInput.submit` convenience methods.

* You can now restrict inputs to a specified host using the `setRestrictToHost`
  method on `TcpInput`, `TcpSplunkInput`, and `UdpInput` (this method requires
  Splunk 5.0+).

* The `DistributedConfiguration.enable` and `DistributedConfiguration.disable`
  convenience methods have been added, allowing you to immediately enable or
  disable the configuration.

* The following methods have been added to the `Index` class:
    - `getBucketRebuildMemoryHint`
    - `getMaxTimeUnreplicatedNoAcks`
    - `getMaxTimeUnreplicatedWithAcks`
    - `setBucketRebuildMemoryHint`
    - `setMaxTimeUnreplicatedNoAcks`
    - `setMaxTimeUnreplicatedWithAcks`

### Breaking changes

* The JAR files have changed so that everything is now included in the
  **splunk.jar** file. The **splunk-external.jar** and **splunk-sdk.jar** files
  have been removed.

* Arguments are now submitted to Splunk in a consistent order, which improves
  behavior in certain cases.

* The `InputKind` enum is now a class. The `InputKind` class has static members
  identical to the enum values, but you can no longer use a `switch` statement
  over the values. Instead, use a series of `if-else` blocks. This change was
  necessary to support arbitrary modular input kinds.

* All text is now consistently UTF-8 encoded. Previously, the platform-native
  encoding was used in certain cases. For example:
    - HTTP requests are sent in UTF-8. In particular the values of *args*
      classes are always encoded in UTF-8.
    - Results and events from jobs are read as UTF-8.

* The `Index.setAssureUTF8` method fails for Splunk 5.0+ because this field has
  become a global setting rather than a per-index setting.

* The `Index.clean` method now throws `SplunkException.INTERRUPTED` when
  interrupted. Additionally, the `maxSeconds` parameter is obeyed more
  accurately.

* The `WindowsRegistryInput.getType` and `WindowsRegistryInput.setType` method
  type has changed to `String[]` instead of `String`.

* The `DistributedPeer.getBuild` method now returns an `int` instead of a
  `String` to be consistent with the `ServiceInfo.getBuild` method.

* The `setRestrictToHost` method on `TcpInput`, `TcpSplunkInput`, and `UdpInput`
  throws an exception for Splunk 4.x. Previously, this method failed silently.

* The `StormService` class has been removed, but will be restored in a
  subsequent release.

* The methods in the `ResultsReader` class now throw `IOException` instead of a
  plain `Exception`, so callers no longer need to handle a plain `Exception`.

* The `SplunkException` class now provides error messages when printed.

* The test suite has been completely cleaned up, resulting in better coverage
  and faster performance, mostly by eliminating unnecessary restarts. The test
  suite strictly requires tests to handle restart requests.

* The `get`, `remove`, and `contains` methods for entity collections now throw
  an exception when a wildcarded namespace is passed, rather than incorrectly
  returning an empty list or taking no action.

* The `HashMap` and `Event` objects returned by `ResultsReader.getNextEvent` are
  now read-only.

* The `SavedSearch.getDispatchMaxTime` method previously returned a `String`,
  but now returns an `int`.

* The `LicensePool.getSlavesUsageBytes` method now returns a map from each slave
  GUID to its license usage, instead of returning a `long`.

* The `Service.oneshot` method has been renamed to `Service.oneshotSearch`.

* The `Service.oneshot(String query, Map inputArgs, Map outputArgs)` overload
  has been removed, because `outputArgs` had no effect.

* The `SavedSearch.setArgsWildcard` method has been removed. To set a wildcard
  parameter, specify it as a key-value pair in a map and pass it to the
  `SavedSearch.dispatch(java.util.Map args)` method.

* The `SavedSearch.setActionWildcard` method has been removed. Use the specific
  setters to update these parameters.

* The `SavedSearch.setDispatchWildcard` method has been removed. Use the
  specific setters to update these parameters.

* The `Service.getFiredAlerts` method has been renamed to
  `getFiredAlertsGroups`.

* The `Entity.reload` method has been removed.

* The `Entity.toUpdate` field is no longer public.

* The `Service.search(query)` and `Service.search(query, args)` methods now
  return a search job instead of blocking and returning results.

* The `Service.search(query, inputArgs, outputArgs)` overload has been removed.

* The `OutputServer.setsslRootCAPPath` method has been renamed to
  `setSslRootCAPPath`.

* The `SavedSearch.getDispatchReduceFreq` method, which returned a `String`, has
  been replaced with `getDispatchReduceFrequency`, which returns an `int`.

* The `setRestrictToHost` method has been removed from the `TcpInput` and
  `UdpInput` classes.

* The `Settings.setMgmtHostPort` method has been renamed to `setMgmtPort` and
  this method now returns an `int`.

### Bug fixes

* The `Service.versionCompare` method has been fixed to work as expected.

* The `OutputDefault.update` method has been fixed so that when a "name"
  parameter is not specified, the method no longer fails.

### Deprecated features

The following list contains the main features that have been deprecated (trivial
changes are not included):

* The public fields in the `ServiceArgs` class have been deprecated in favor of
  the new setter methods to maintain consistency with the new *args* subclasses.

* The `Application.isManageable` and `Application.setManageable` methods have
  been deprecated in Splunk 5.0 and later.

* The `DistributedConfiguration.getServerTimeout` method has been deprecated in
  Splunk 5.0 and later.


## Version 0.8.0 (beta)

### Breaking changes

* Changed how isDone() behaves with respect to job creation. Previously if a job
  was not ready on the server, calling job.isDone() would cause an exception.
  Now, calling isDone() will return false under the two following conditions:
  1) The job has not yet been scheduled. 2) The job has been scheduled but the
  results are not ready. In addition, isDone() implicitly invokes job.refresh()
  so the caller does not need to. This simplifies the code waiting for a job
  result to this: (with a 500 millisecond polling interval)

  ```
        while (!job.isDone()) {
            sleep(500);
        }
  ```

* Added isReady() method to the Job class. This method detects whether or
  not the job is ready to return data (i.e. be queried). It also implicitly
  invokes job.refresh(). This allows for jobs with previews but that have not
  necessarily completed to be accessed: (with a 500 millisecond polling
  interval)

  ```
        while (!job.isReady()) {
            sleep(500);
        }
  ```

* All Job class accessors will call refresh once before accessing the object.

### Bug fixes

* Fixed ordering of collections when using pagination. Previously the order
  could be random. Now it maintains the order of the entities returned by the
  server.

* Fixed XML streaming reader to properly work with paginated result sets.

* Large collections can cause a default JVM to run out of memory: The Atom
  parsing uses the streaming XML parser as opposed to a DOM parser.

* Fixed Index class getSync() method to return an integer instead of a boolean.

* Added Index class get method getEnableOnlineBucketRepair().

* Added Index class get method getMaxBloomBackfillBucketAge().

## Version 0.5.0 (preview)

### New APIs
* `StormService` class
* `Receiver` class
* `Upload` class
* New setter methods for all classes
* New getter methods for various classes

### New features
* Added support for a default index, allowing optional parameters
  for streaming connections. The `Index` class now uses the new `Receiver`
  class.

* Added a paginate feature for Splunk return data. This feature allows for
  `count` and `offset` methods to page through Splunk meta data instead of
  retrieving all the data at once:

  ```
    ConfCollection confs;
    Args args = new Args();
    args.put("count", 30);
    args.put("offset", 0);

    confs = service.getConfs(args);
    // ... operate on the first 30 elements
    offset = offset + 30;
    args.put("offset", offset)
    confs = service.getConfs(args);
    // ... operate on the next 30 elements
  ```

* Added a namespacing feature as optional arguments (`app`, `owner`, `sharing`)
  to the collection's `create` and `get` methods. For more information about
  namespaces, see
  ["Overview of the Splunk Enterprise SDK for Java"](http://dev.splunk.com/view/java-sdk/SP-CAAAECN)
  on the Developer Portal.

  The following example shows how to use the optional namespace to restrict
  creating and selecting saved searches to the namespace "owner = magilicuddy,
  app = oneMeanApp":

  ```
    String searchName = "My scoped search";
    String search = "index=main * | head 10";
    args args = new Args();
    args.put("owner", "magilicuddy");
    args.put("app",  "oneMeanApp");

    // ... other creation arguments also get set into the args map

    savedSearches.create(searchName, search, args);
  ```

  This example shows how to returns all saved searches within the same scoped
  namespace:

  ```
    args args = new Args();
    args.put("owner", "magilicuddy");
    args.put("app",  "oneMeanApp");
    SavedSearchCollection
        mySavedSearches = service.getSavedSearches(args);
  ```

* Added an XML, JSON, and CSV streaming results reader. This feature allows you
  to retrieve event data using an incremental streaming mechanism. Return data
  is in key-value pairs. The XML form uses built-in JDK XML parsing support. The
  JSON and CSV form requires third-party JSON and CSV tokenizers, which are
  included as ancillary .jar files in the SDK. The JSON and CSV streaming
  results reader, which requires the external tokenizers, are contained in a
  separate Splunk .jar file named `splunk-external.jar`.

  The following example uses the built-in XML streaming reader:

  ```
    Job job = service.getJobs().create(query, queryArgs);
    ...

    HashMap<String, String> map;
    stream = job.getResults(outputArgs);
    ResultsReader resultsReader = new ResultsReaderXml(stream);
    while ((map = resultsReader.getNextEvent()) != null) {
        for (String key: map.keySet())
            System.out.println(key + " --> " + map.get(key));
    }
  ```

* Added support for Splunk Storm. Instead of connecting to `Service`, you
  connect to the new `StormService` class using similar arguments. Then, get a
  `Receiver` object and log events. `StormService` requires the `index` key and
  `sourcetype` parameters when sending events:

  ```
    // the storm token provided by Splunk
    Args loginArgs = new Args("StormToken",
        "p-n8SwuWEqPlyOXdDU4PjxavFdAn1CnJea9LirgTvzmIhMEBys6w7UJUCtxp_7g7Q9XopR5dW0w=");
    Storm service = StormService.connect(loginArgs);

    // get the receiver object
    Receiver receiver = service.getReceiver();

    // index and source type are required for storm event submission
    Args logArgs = new Args();
    logArgs.put("index", "0e8a2df0834211e1a6fe123139335741");
    logArgs.put("sourcetype", "yoursourcetype");

    // log an event.
    receiver.log("This is a test event from the SDK", logArgs);
  ```

### Minor additions

* Added a `genevents` example to generate events and push into Splunk using
  various methods.
* Added a second time format when parsing time. A second time format is required
  to accommodate the `data/input/oneshot` endpoint that does not return a
  standard time format and does not allow a time-format specifier.
* Added a streaming reader to search examples. The main search example `search`
  shows how to use all three result readers. There are build
  modifications in build.xml to include the ancillary .jar files for JSON and
  CSV.
* Added an `Input` example to display Splunk inputs and their attributes.
* Added an alias `log` for `submit` to the `Receiver` class.
* Updated eclipse .classpath file, accounting for new additions.

### Bug fixes

* Fixed argument processing in the tail example.
* Fixed timing window during search job creation; added `JOB_NOT_READY`
  exception.
* Fixed `Index` cleaning to require a timeout value; added `TIMEOUT` exception.
* Fixed `LicensePool` type to use string quota instead of integer. This change
  allows for `MAX` and `<number>[M|G|T]`.
* Fixed `action` when trying to update `Settings`.
* Fixed user creation to force lowercase usernames.
* Fixed the missing get methods for `ServiceInfo`.
* Fixed a number of getter methods.

## Version 0.1.0 (preview)

Initial Splunk Enterprise SDK for Java release.
