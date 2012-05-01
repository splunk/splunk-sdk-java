# Splunk Java SDK Changelog

## develop

### Added Features

* Add a Receiver class.

* Add support for index-less (default index) and allow optional parameters for
  streaming connections. The Index class now uses the added Receiver class.

* Add paginate feature for splunk return data. This allows for count/offset
  method to page through splunk meta data instead of retrieving all the data
  at once.

* Add namespacing feature. Added as overload collection creation methods, each
  method supporting namespaces has a method variant to accept an optional
  `app` and `owner` and `sharing` map. For a more detailed description of Splunk
  namespaces in the Splunk REST API reference under the section on accessing
  Splunk resources, see:

  http://docs.splunk.com/Documentation/Splunk/latest/RESTAPI/RESTresources

* Add XML, JSON and CSV streaming results reader. This feature allows one to
  retrieve event data via an incremental streaming mechanism. Return data is in
  key/value pairs. The XML form uses built-in JDK XML parsing support. The JSON
  and CSV form requires third party JSON and CSV parsers which are included as
  ancillary jar files in the SDK. For clarity, the JSON and CSV streaming
  results reader, are contained in a separate splunk jar file named,
  splunk-external.jar.

* Add an Upload class. This class is available to query in-progress file
  uploads.

* Add class setter methods.

* Add missing getter methods.

### Minor Additions

* Add genevents example, to generate events and push into splunk using various
  methods.
* Add second time format when parsing time. A second time format is required to
  accommodate the `data/input/oneshot` endpoint that does not return a
  standard time-format and does not allow a time-format specifier.
* Add streaming reader to search examples. The main search example `search`,
  shows how to use all three result readers. Note that there are some build
  modifications in build.xml to include the ancillary jar files.
* Add a Input example to display splunk inputs and their attributes.

### Bug fixes

* Fix argument processing in tail example.
* Fix timing window during search job creation;
  add splunk exception JOB_NOT_READY.
* Fix index cleaning to require timeout value; add splunk exception `TIMEOUT`.
* Fix LicensePool creation to use string quota instead integer.
  Allows for "MAX" and <number>[M|G|T]
* Fix action to update settings.
* Fix user creation to force lowercase usernames.
* Fix ServiceInfo missing get methods.
* Fix a number of getter methods.
