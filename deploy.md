##NAME

deploy - Transmit splunk-sdk-java artifacts to the local, staging or production 
maven artifact repository

##SYNOPSIS

deploy \<repository-name>

##DESCRIPTION

Deploy transmits **dist/splunk-1.2.1.jar**, **dist/splunk-1.2.1-javadoc.jar**, and 
**dist/splunk-1.2.1-sources.jar** to the **local**, **staging**, or **production**
maven repository. Repository names are mapped to locations as follows.

| repository-name | location                                                       |
|-----------------|----------------------------------------------------------------|
| local           | file:///${HOME}/.m2/repository/                                |
| staging         | http://stg-artifactory:8081/artifactory/devplat-staging/       |                                             |
| production      | http://splunk.artifactoryonline.com/splunk/ext-releases-local/ |

After deployment you should find this tree structure at the location of your repository

    com/splunk/splunk/1.2.1/
    ├── splunk-1.2.1-javadoc.jar
    ├── splunk-1.2.1-javadoc.jar.md5
    ├── splunk-1.2.1-javadoc.jar.sha1
    ├── splunk-1.2.1-sources.jar
    ├── splunk-1.2.1-sources.jar.md5
    ├── splunk-1.2.1-sources.jar.sha1
    ├── splunk-1.2.1.jar
    ├── splunk-1.2.1.jar.md5
    ├── splunk-1.2.1.jar.sha1
    ├── splunk-1.2.1.pom
    ├── splunk-1.2.1.pom.md5
    └── splunk-1.2.1.pom.sha1

Verify this structure prior to release.
