package com.flock.plugin;

import jenkins.model.Jenkins;

public class JenkinsWrapper {

    Jenkins jenkins;

    public JenkinsWrapper(Jenkins jenkins) {
        this.jenkins = jenkins;
    }

    public String getPluginVersion() {
        String versionNumber = jenkins.pluginManager.getPlugin("flock").getVersionNumber().toString();
        return extractPluginVersionFrom(versionNumber);
    }

    public String extractPluginVersionFrom(String versionNumber) {
        String[] splits = versionNumber.split("-SNAPSHOT");
        String justNumber = splits[0];
        if (justNumber == null) {
            return versionNumber;
        } else {
            return justNumber;
        }
    }
}
