package com.flock.plugin;

import jenkins.model.Jenkins;

public class JenkinsWrapper {

    private Jenkins jenkins;

    public JenkinsWrapper(Jenkins jenkins) {
        this.jenkins = jenkins;
    }

    public String getPluginVersion() {
        String versionNumber = jenkins.pluginManager.getPlugin("flock").getVersionNumber().toString();
        String[] splits = versionNumber.split("-SNAPSHOT");
        return splits[0];
    }

    public String getJenkinsVersion() {
        return jenkins.getVersion().toString();
    }

}
