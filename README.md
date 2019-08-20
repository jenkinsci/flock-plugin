# Jenkins Plugin for Flock Notifications

This plugin will provide build status notifications to flock messenger.

## Installation
1. Go to 'Manage Jenkins' from the Jenkins homepage.
    1. Navigate to `Manage Plugins`,
    1. Change the tab to `Available`,
    1. Search for `flock`,
    1. Check the box next to install.

## Adding plugin to project
You can configure this plugin for each project on your Jenkins server.

1. Navigate to the project.
2. Click on `configure`.
3. Click on `Add Post Build Action`.
4. Select `Send Flock Notification` from the selection list.

## Usage
Please follow this guide to generate a Webhook URL to be used in the plugin.
*https://github.com/talk-to/janus/blob/f2b1cbc3e6ce746e497832ff1bdf96c4c1ed1454/README.md* (Private repository)

After generating the webhook, follow these steps.

1. Put the generated webhook url in `Webhook URL` field.
2. Check the types of build notifications you'll want to receive.

## Payload Structure

This is the payload structure that `PayloadManager` class generate.

````
{  
   "projectName": "<The name of your project>",
   "displayName": "<The display name of your project>",
   "status": "<Status of the jenkins build>", // Possible values : “start”/ “success”/ “aborted” / “unstable” / “failure” / “not built”/ “regression” / “back to normal”
   "duration": "<duration of the build in seconds>" // Integer
   "runURL": "<URL of the build>",
   "changes": {  
      "authors": ["name1", "name2"],
      "filesCount": <count of files changed> // Integer
   },
   "causeAction": {  
      "isSCM": false, //Boolean
      "other": "<short description for cause action>
   }
}
```` 

## Debugging
- To debug and build this plugin, you'll have to setup Maven and JDK on your system.
`Java version` used to build: `1.8.0_22`
- Please follow the guide on https://jenkins.io/doc/developer/tutorial/prepare/ to setup Maven and JDK
- You can confirm  the java version on your system by running `java -version` command in terminal.

Following is referenced from : https://wiki.jenkins.io/display/JENKINS/Plugin+tutorial
It may be helpful to add the following to your ~/.m2/settings.xml (Windows users will find them in %USERPROFILE%\.m2\settings.xml):
````
<settings>
  <pluginGroups>
    <pluginGroup>org.jenkins-ci.tools</pluginGroup>
  </pluginGroups>
 
  <profiles>
    <!-- Give access to Jenkins plugins -->
    <profile>
      <id>jenkins</id>
      <activation>
        <activeByDefault>true</activeByDefault> <!-- change this to false, if you don't like to have it on per default -->
      </activation>
      <repositories>
        <repository>
          <id>repo.jenkins-ci.org</id>
          <url>https://repo.jenkins-ci.org/public/</url>
        </repository>
      </repositories>
      <pluginRepositories>
        <pluginRepository>
          <id>repo.jenkins-ci.org</id>
          <url>https://repo.jenkins-ci.org/public/</url>
        </pluginRepository>
      </pluginRepositories>
    </profile>
  </profiles>
  <mirrors>
    <mirror>
      <id>repo.jenkins-ci.org</id>
      <url>https://repo.jenkins-ci.org/public/</url>
      <mirrorOf>m.g.o-public</mirrorOf>
    </mirror>
  </mirrors>
</settings>

````

*This will let you use short names for Jenkins Maven plugins (i.e. hpi:create instead of org.jenkins-ci.tools:maven-hpi-plugin:1.61:create),
though this is unnecessary once you are already working in a plugin project (only useful for initial hpi:create).
Note that adding the Jenkins repositories in a profile like this is not really necessary since most (all?) plugins already define these repositories.
And the mirror declaration is probably unnecessary.*

# Setting for Release
**Signup Required**

Jenkins plugins distributed via Jenkins project update sites need to be hosted in the jenkinsci GitHub organization, so you will need a user account on GitHub.
To actually release your plugin, you will need a Jenkins community account that will give you access to the issue tracker, wiki, and Maven repository.
Please refer https://jenkins.io/doc/developer/publishing/preparation/ for updated links.

Get your Artifactory account name added here: https://github.com/jenkins-infra/repository-permissions-updater/blob/e41bc47f237c2228d44726422e38943242c2c21f/permissions/plugin-flock.yml by opening a PR.

A full comprehensive guide is available here: https://github.com/jenkins-infra/repository-permissions-updater/blob/master/README.md

# Release

To release a plugin, follow the steps on the following referenced page
https://jenkins.io/doc/developer/publishing/releasing/