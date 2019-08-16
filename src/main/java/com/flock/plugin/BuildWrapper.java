package com.flock.plugin;

import hudson.model.AbstractBuild;
import hudson.model.Cause;
import hudson.model.CauseAction;
import hudson.scm.ChangeLogSet;
import hudson.triggers.SCMTrigger;
import net.sf.json.JSONObject;

import java.util.HashSet;

public class BuildWrapper {

    private BuildResult status;
    private long duration;
    private String projectName;
    private String displayName;
    private String runURL;
    private JSONObject changes;
    private JSONObject causeAction;

    public BuildResult  getStatus() {
        return status;
    }

    public long getDuration() {
        return duration;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getRunURL() {
        return runURL;
    }

    public JSONObject getChanges() {
        return changes;
    }

    public JSONObject getCauseAction() {
        return causeAction;
    }

    public BuildWrapper(AbstractBuild build, BuildResult result) {
        this.status = result;
        this.duration = getDuration(build);
        this.projectName = build.getProject().getDisplayName();
        this.displayName = build.getDisplayName();
        this.runURL = build.getAbsoluteUrl();

        this.changes = getChanges(build);
        this.causeAction = getCauses(build);
    }

    private static long getDuration(AbstractBuild build) {
        long buildStartTime = build.getStartTimeInMillis();
        long currentTimeMillis = System.currentTimeMillis();

        return (currentTimeMillis - buildStartTime)/1000;
    }

    private static JSONObject getChanges(AbstractBuild build) {
        HashSet<String> authors = new HashSet();
        HashSet<String> affectedPaths = new HashSet();
        for (Object item : build.getChangeSet().getItems()) {
            ChangeLogSet.Entry entry = (ChangeLogSet.Entry) item;
            authors.add(entry.getAuthor().getDisplayName());
            affectedPaths.addAll(entry.getAffectedPaths());
        }

        JSONObject json = new JSONObject();
        json.put("authors", authors);
        json.put("filesCount", affectedPaths.size());
        return json;
    }

    private static JSONObject getCauses(AbstractBuild b) {
        JSONObject jsonObject = new JSONObject();
        CauseAction causeAction = b.getAction(CauseAction.class);
        if (causeAction != null) {
            Cause scmCause = causeAction.findCause(SCMTrigger.SCMTriggerCause.class);
            StringBuilder causeActionStringBuilder = new StringBuilder();
            causeAction.getCauses().forEach( (cause) -> {
                causeActionStringBuilder.append(cause.getShortDescription());
            });
            String causeActionString = causeActionStringBuilder.toString();
            if (scmCause == null) {
                jsonObject.put("isSCM", false);
            } else {
                jsonObject.put("isSCM", true);
            }
            jsonObject.put("other", causeActionString);
        }
        return jsonObject;
    }



}
