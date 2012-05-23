# Splunk Java SDK Changelog

## develop

### Added Features

* Add a `Receiver` class.

* Add support for index-less (default index) and allow optional parameters for
  streaming connections. The Index class now uses the new Receiver class.

* Add paginate feature for splunk return data. This allows for count/offset
  method to page through splunk meta data instead of retrieving all the data
  at once:
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
* Add namespacing feature. Added as optional arguments to the collection
  create and get methods, `app` and `owner` and `sharing` control the namespace.
  For a more detailed description of Splunk namespaces in the Splunk REST API
  reference under the section on accessing Splunk resources, see:

  http://docs.splunk.com/Documentation/Splunk/latest/RESTAPI/RESTresources

  An example of using the optional namespace to restrict the creation and
  selection of saved searches to the specific namespace where `owner` is set to
  "magilicuddy" and the `app` is set to "oneMeanApp".
```
    String searchName = "My scoped search";
    String search = "index=main * | head 10";
    args args = new Args();
    args.put("owner", "magilicuddy");
    args.put("app",  "oneMeanApp");

    // ... other creation arguments also get set into the args map

    savedSearches.create(searchName, search, args);
```

  And the subsequent usage elsewhere, will return all the saved searches within
  the scoped namespace.

```
    args args = new Args();
    args.put("owner", "magilicuddy");
    args.put("app",  "oneMeanApp");
    SavedSearchCollection
        mySavedSearches = service.getSavedSearches(args);
```

* Add XML, JSON and CSV streaming results reader. This feature allows one to
  retrieve event data via an incremental streaming mechanism. Return data is in
  key/value pairs. The XML form uses built-in JDK XML parsing support. The JSON
  and CSV form requires third party JSON and CSV tokenizers which are included
  as ancillary jar files in the SDK. For clarity, the JSON and CSV streaming
  results reader, which require the external tokenizers, are contained in a
  separate splunk jar file named, splunk-external.jar.

  The example here is using the built-in XML streaming reader:
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

* Add an Upload class. This class is available to query in-progress file
  uploads.

* Add class setter methods.

* Add missing getter methods.

* Add support for Splunk Storm. Instead of connecting to `Service`, connect to
  `Storm`. The same semantics that `Service` uses, applies here. Get a
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
### Minor Additions

* Add `genevents` example, to generate events and push into splunk using various
  methods.
* Add second time format when parsing time. A second time format is required to
  accommodate the `data/input/oneshot` endpoint that does not return a
  standard time-format and does not allow a time-format specifier.
* Add streaming reader to search examples. The main search example `search`,
  shows how to use all three result readers. Note that there are  build
  modifications in build.xml to include the ancillary jar files for JSON and
  CSV.
* Add a Input example to display splunk inputs and their attributes.
* Add alias `log` for `submit` to the Receiver class.

### Bug fixes

* Fix argument processing in tail example.
* Fix timing window during search job creation;
  add splunk exception `JOB_NOT_READY`.
* Fix `Index` cleaning to require timeout value; add splunk exception `TIMEOUT`.
* Fix LicensePool creation to use string quota instead integer.
  Allows for `MAX` and `<number>[M|G|T]`
* Fix `action` when trying to update `Settings`.
* Fix user creation to force lowercase usernames.
* Fix ServiceInfo missing get methods.
* Fix a number of getter methods.
