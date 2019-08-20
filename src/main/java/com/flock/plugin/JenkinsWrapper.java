package com.flock.plugin;

import jenkins.model.Jenkins;

public class JenkinsWrapper {

    Jenkins jenkins;

    public JenkinsWrapper(Jenkins jenkins) {
        this.jenkins = jenkins;
    }

    public String getPluginVersion() {
        String versionNumber = jenkins.pluginManager.getPlugin("flock").getVersionNumber().toString();
        String[] splits = versionNumber.split("-SNAPSHOT");
        String justNumber = splits[0];
        if (justNumber == null) {
            return jenkins.pluginManager.getPlugin("flock").getVersionNumber().toString();
        } else {
            return justNumber;
        }

    }
}
