# Splunk Java SDK Changelog

## develop

### Added Features

* Add support for index-less (default index) and allow optional parameters for 
  streaming connections. This includes an added Receiver class that the Index
  class now uses.

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
  retreive event data via an incrmental streaming mechanism. Return data is in
  key/value pairs. The XML form uses built-in JDK XML parsing support. The JSON 
  and CSV form requires third party JSON and CSV parsers which are included as
  ancilliary jar files in the SDK. For clarity, the JSON and CSV streaming 
  results reader, are contained in a separate splunk jar file named,
  splunk-external.jar.

* Add an Upload class. This class is availabe to query in-progress file uploads.

### Minor Additions

* Add genevents example.
* Add second time format when parsing time.
* Add streaming reader to search examples.

### Bug fixes

* Fix argument processing in tail example.
* Fix timing window during search job creation; 
  add splunk exception JOB_NOT_READY.
* Fix index cleaning to require timeout value; add splunk exception TIMEOUT.
* Fix LicensePool creation to use string quota instead integer. 
  Allows for "MAX" and <number>[M|G|T]
* Fix action to update settings.
* Fix user creation to force lowercase usernames.
* Fix ServiceInfo missing get methods.

