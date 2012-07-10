# Splunk Java SDK Changelog, since 0.5.0

## Version [TBD]

### Behavior changes

********************************************************************************

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

* Added isReady() method to the Job class. This method detects whether or not 
  the job is ready to return data (i.e. be queried). It also implicitly invokes
  job.refresh(). This allows for jobs with previews, but not necessarily
  completed can be accessed: (with a 500 millisecond polling interval)
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
