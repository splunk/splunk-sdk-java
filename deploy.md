##NAME

deploy - Transmit splunk-sdk-java artifacts to the local, staging or production 
maven artifact repository

##SYNOPSIS

deploy \<repository-name>

##DESCRIPTION

Deploy transmits **dist/splunk-1.6.4.jar**, **dist/splunk-1.6.4-javadoc.jar**, and 
**dist/splunk-1.6.4-sources.jar** to the **local**, **staging**, or **production**
maven repository. Repository names are mapped to locations as follows.

| repository-name | location                                                       |
|-----------------|----------------------------------------------------------------|
| local           | file:///${HOME}/.m2/repository/                                |
| staging         | http://stg-artifactory:8081/artifactory/devplat-staging/       |                                             |
| production      | https://splunk.jfrog.io/artifactory/ext-releases-local/ |

After deployment you should find this tree structure at the location of your repository

    com/splunk/splunk/1.6.4/
    ├── splunk-1.6.4-javadoc.jar
    ├── splunk-1.6.4-javadoc.jar.md5
    ├── splunk-1.6.4-javadoc.jar.sha1
    ├── splunk-1.6.4-sources.jar
    ├── splunk-1.6.4-sources.jar.md5
    ├── splunk-1.6.4-sources.jar.sha1
    ├── splunk-1.6.4.jar
    ├── splunk-1.6.4.jar.md5
    ├── splunk-1.6.4.jar.sha1
    ├── splunk-1.6.4.pom
    ├── splunk-1.6.4.pom.md5
    └── splunk-1.6.4.pom.sha1

Verify this structure prior to release.
