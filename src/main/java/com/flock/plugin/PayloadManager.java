package com.flock.plugin;

import jenkins.model.Jenkins;
import net.sf.json.JSONObject;

public class PayloadManager {

    public static JSONObject createPayload(BuildWrapper buildWrapper, JenkinsWrapper jenkinsWrapper) {
        JSONObject jsonObject= new JSONObject();
        if (buildWrapper.getStatus() == BuildResult.START) {
            jsonObject.put("status", buildWrapper.getStatus().stringValue());
        } else {
            jsonObject.put("status", buildWrapper.getStatus().stringValue());
            jsonObject.put("duration", buildWrapper.getDuration());
        }
        jsonObject.put("projectName", buildWrapper.getProjectName());
        jsonObject.put("displayName", buildWrapper.getDisplayName());
        jsonObject.put("runURL", buildWrapper.getRunURL());
        jsonObject.put("changes", buildWrapper.getChanges());
        jsonObject.put("causeAction", buildWrapper.getCauseAction());
        if (null != getVersionNumber()) {
            jsonObject.put("pluginVersion", getVersionNumber());
        } else {
            jsonObject.put("pluginVersion", Jenkins.get().pluginManager.getPlugin("flock").getVersionNumber().toString());
        }

        return jsonObject;
    }

    private static String getVersionNumber() {
        String fullVersion = Jenkins.get().pluginManager.getPlugin("flock").getVersionNumber().toString();
        String[] splits = fullVersion.split("-SNAPSHOT");
        String justNumber = splits[0];
        return justNumber;
    }

}
