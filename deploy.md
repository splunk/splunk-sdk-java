##NAME

deploy - Transmit splunk-sdk-java artifacts to the local, staging or production
maven artifact repository

##SYNOPSIS

deploy \<repository-name>

##DESCRIPTION

Deploy transmits **target/splunk-1.9.1.jar**, **target/splunk-1.9.1-javadoc.jar**, and
**target/splunk-1.9.1-sources.jar** to the **local**, **staging**, or **production**
maven repository. Repository names are mapped to locations as follows.

| repository-name | location                                                       |
|-----------------|----------------------------------------------------------------|
| local           | file:///${HOME}/.m2/repository/                                |
| staging         | http://stg-artifactory:8081/artifactory/devplat-staging/       |                                             |
| production      | https://splunk.jfrog.io/artifactory/ext-releases-local/ |

After deployment you should find this tree structure at the location of your repository

    com/splunk/splunk/1.9.1/
    ├── splunk-1.9.1-javadoc.jar
    ├── splunk-1.9.1-javadoc.jar.md5
    ├── splunk-1.9.1-javadoc.jar.sha1
    ├── splunk-1.9.1-sources.jar
    ├── splunk-1.9.1-sources.jar.md5
    ├── splunk-1.9.1-sources.jar.sha1
    ├── splunk-1.9.1.jar
    ├── splunk-1.9.1.jar.md5
    ├── splunk-1.9.1.jar.sha1
    ├── splunk-1.9.1.pom
    ├── splunk-1.9.1.pom.md5
    └── splunk-1.9.1.pom.sha1

Verify this structure prior to release.
