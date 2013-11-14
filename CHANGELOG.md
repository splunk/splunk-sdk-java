# Splunk SDK for Java Changelog

## Version 1.2.1

### New features and APIs

* The Splunk SDK for Java is fully compatible with Splunk Enterprise 6.0 as of this release.

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

* Certain combinations of requests and restarts of splunkd can cause splunkd to hang on OS X v10.8 Mountain Lion 
  running Splunk 6.0.0. This issue is not present when running Splunk Enterprise on OS X v10.7 Lion or OS X v10.9   
  Mavericks. This will be fixed in a future release.

* The modular input support in the Splunk SDK for Java is not compatible with Windows Server 2003 or Windows Server 
  2003 R2.

## Version 1.2

### New features and APIs

* Added support for building modular input scripts in Java using the Splunk SDK for Java.

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
  ["Overview of the Splunk SDK for Java"](http://dev.splunk.com/view/java-sdk/SP-CAAAECN)
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

Initial Splunk SDK for Java release.
