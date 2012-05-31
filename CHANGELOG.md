# Splunk Java SDK Changelog, Preview Refresh

## Version 0.5.0

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
