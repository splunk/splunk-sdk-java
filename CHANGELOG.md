# Splunk Java SDK Changelog

## Version 1.0

### New features

* Specialized Args classes that make it easier to determine what arguments can
  be passed to various methods:
    - Service's constructor
    - Service.getSavedSearches()
    - Service.getJobs()
    - Service.getIndexes()
    - Service.export()
    - JobCollection.create()
    - Job.getResults()
    - Job.getResultsPreview()
    - Job.getEvents()
    - Job.getSummary()
    - SavedSearch.dispatch()

* Modular inputs for Splunk >= 5.0.
    - InputCollection now handles arbitrary input kinds represented by modular
      inputs.
    - The set of InputKinds on the connected Splunk instance is available by
      calling InputCollection.getInputKinds().

* Removing indexes for Splunk >= 5.0.

* TcpInputs.attach() and submit() convenience methods allow you to send data
  to the input.

* UdpInput.submit() convenience method allows you to send data to the input.

* setRestrictToHost() on TcpInput, TcpSplunkInput, and UdpInput are now
  supported for Splunk >= 5.0.

* DistributedConfiguration.enable() and disable() convenience methods that
  immediately enable/disable the configuration.

* More accessors on Index.
    - bucketRebuildMemoryHint
    - maxTimeUnreplicatedNoAcks
    - maxTimeUnreplicatedWithAcks

* SplunkExceptions now provide their error message when printed.

* Massive cleanup of the test suite.
    - Better coverage.
    - Runs a ton faster, mostly due to elimination of unnecessary restarts.
    - Strictly requires tests to handle restart requests.

### Breaking Changes

* JAR changes:
    - Everything is now in splunk.jar.
      There is no longer a separate splunk-external.jar.
    - splunk-sdk.jar has been removed.

* Arguments are now submitted to Splunk in a consistent order.
    - This improves behavior in certain edge cases.
  
* InputKind has changed from an enum to a class.
    - It has static members identical to the enum values, but you can no longer
      use a switch statement over the values. Instead you must use a series of
      if-else blocks.
    - This was necessary to support arbitrary modular input kinds.

* All text is encoded in UTF-8 consistently. Previously the platform-native
  encoding was used in some cases.
    - Http requests are sent in UTF-8. In particular the values of Args classes
      are always encoded in UTF-8.
    - Results and events from jobs are read in as UTF-8.

* Index.setAssureUTF8() fails for Splunk >= 5.0, since that field has become a
  global setting instead of a per-index setting.

* Index.clean() now throws SplunkException.INTERRUPTED when interrupted instead
  of swallowing the condition. Additionally the timeout parameter is obeyed more
  accurately.

* WindowsRegistryInput.getType() and setType() changed to be String[] instead of String.

* DistributedPeer.getBuild() now returns an int instead of a String to be
  consistent with ServiceInfo.getBuild().

* Deprecated ServiceArgs public fields in favor of the new setter methods.
    - This was needed to maintain consistency with the new-style Args subclasses.

* Deprecated Application.isManageable() and setManageable() as of Splunk >= 5.0.

* Deprecated several getters on DistributedConfiguration as of Splunk >= 5.0.

* Fix OutputDefault.update() when no "name" key was specified.
    - Previously, failing to specify a "name" key would cause this method to
      fail.

* setRestrictToHost() on TcpInput, TcpSplunkInput, and UdpInput throws an
  exception for Splunk 4.x.
    - Previously this method failed silently.

* Fix Service.versionCompare(). It was completely broken before.

* StormService has been removed. It will be restored in a subsequent release.

* ResultsReader methods now throws IOException instead of a plain Exception.
    - Callers no longer need to handle a plain Exception.

* The get(), remove(), and contains() methods on entity collections now throw
  an exception when passed a wildcarded namespace instead of incorrectly
  returning an empty list or taking no action.

* SavedSearch.getDispatchMaxTime() was previously returning a String, but is now
  returning an int.

* LicensePool.getSlavesUsageBytes() was previously returning an integer, instead
  of a map from each slave GUID to the license usage of that slave.


## Version 0.8.0 (beta)

### Breaking Changes

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
  ["Overview of the Splunk Java SDK"](http://dev.splunk.com/view/java-sdk/SP-CAAAECN) 
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

Initial Java SDK release.
