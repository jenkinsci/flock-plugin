package com.flock.plugin;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import net.sf.json.JSONObject;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import java.io.IOException;
import java.util.Arrays;

public class FlockNotifier extends hudson.tasks.Recorder {

    private String webhookUrl;
    private boolean notifyOnStart;
    private boolean notifyOnSuccess;
    private boolean notifyOnUnstable;
    private boolean notifyOnAborted;
    private boolean notifyOnFailure;
    private boolean notifyOnNotBuilt;
    private boolean notifyOnRegression;
    private boolean notifyOnBackToNormal;

    @DataBoundConstructor
    public FlockNotifier(String webhookUrl, boolean notifyOnStart, boolean notifyOnSuccess, boolean notifyOnUnstable, boolean notifyOnAborted, boolean notifyOnFailure, boolean notifyOnNotBuilt, boolean notifyOnRegression, boolean notifyOnBackToNormal) {
        this.webhookUrl = webhookUrl;
        this.notifyOnStart = notifyOnStart;
        this.notifyOnSuccess = notifyOnSuccess;
        this.notifyOnUnstable = notifyOnUnstable;
        this.notifyOnAborted = notifyOnAborted;
        this.notifyOnFailure = notifyOnFailure;
        this.notifyOnNotBuilt = notifyOnNotBuilt;
        this.notifyOnRegression = notifyOnRegression;
        this.notifyOnBackToNormal = notifyOnBackToNormal;
    }

    // Getter methods below need to public for Config.jelly to fetch values if persisted.

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public boolean isNotifyOnStart() { return notifyOnStart; }

    public boolean isNotifyOnSuccess() { return notifyOnSuccess; }

    public boolean isNotifyOnUnstable() { return notifyOnUnstable; }

    public boolean isNotifyOnAborted() { return notifyOnAborted; }

    public boolean isNotifyOnFailure() { return notifyOnFailure; }

    public boolean isNotifyOnNotBuilt() { return notifyOnNotBuilt; }

    public boolean isNotifyOnRegression() { return notifyOnRegression; }

    public boolean isNotifyOnBackToNormal() { return notifyOnBackToNormal; }


    @Override
    public boolean prebuild(AbstractBuild<?, ?> build, BuildListener listener) {
        if (isNotifyOnStart()) {
            sendNotification(build, listener,  BuildResult.START);
        }
        return super.prebuild(build, listener);
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        BuildResult buildResult = getBuildResult(build);
        if ((isNotifyOnSuccess() && buildResult == BuildResult.SUCCESS)
                || (isNotifyOnAborted() && buildResult == BuildResult.ABORTED)
                || (isNotifyOnFailure() && buildResult == BuildResult.FAILURE)
                || (isNotifyOnNotBuilt() && buildResult == BuildResult.NOT_BUILT)
                || (isNotifyOnUnstable() && buildResult == BuildResult.UNSTABLE)
                || (isNotifyOnBackToNormal() && buildResult == BuildResult.BACK_TO_NORMAL)
                || (isNotifyOnRegression() && buildResult == BuildResult.REGRESSION)) {
            sendNotification(build, listener, buildResult);
        }
        return true;
    }

    private BuildResult getBuildResult(AbstractBuild build) {
        Result result = build.getResult();
        Result lastResult;
        if (result != null) {
            AbstractBuild lastBuild = build.getProject().getLastBuild();
            if (lastBuild != null) {
                Run previousBuild = lastBuild.getPreviousBuild();
                Run previousSuccessfulBuild = build.getPreviousSuccessfulBuild();
                boolean buildHasEverSucceeded = previousSuccessfulBuild != null;

                Run lastNonAbortedBuild = previousBuild;
                while (lastNonAbortedBuild != null && lastNonAbortedBuild.getResult() == Result.ABORTED) {
                    lastNonAbortedBuild = lastNonAbortedBuild.getPreviousBuild();
                }

                if (lastNonAbortedBuild == null) {
                    lastResult = Result.SUCCESS;
                } else {
                    lastResult = lastNonAbortedBuild.getResult();
                }

                if (result == Result.SUCCESS
                        && (lastResult == Result.FAILURE || lastResult == Result.UNSTABLE)
                        && buildHasEverSucceeded && isNotifyOnBackToNormal()) {
                    return BuildResult.BACK_TO_NORMAL;
                }

                if (result == Result.SUCCESS) {
                    return BuildResult.SUCCESS;
                } else if (result == Result.FAILURE) {
                    return BuildResult.FAILURE;
                } else if (result == Result.ABORTED) {
                    return BuildResult.ABORTED;
                } else if (result == Result.NOT_BUILT) {
                    return BuildResult.NOT_BUILT;
                } else if (result == Result.UNSTABLE) {
                    return BuildResult.UNSTABLE;
                }
            }
        }
        return null;
    }

    private void sendNotification(AbstractBuild build, BuildListener listener, BuildResult buildResult) {
        FlockLogger logger = new FlockLogger(listener.getLogger());
        BuildWrapper buildWrapper = new BuildWrapper(build, buildResult);
        JSONObject payload = PayloadManager.createPayload(buildWrapper);
        logger.log(payload);
        try {
            RequestsManager.sendNotification(webhookUrl, payload, logger);
        } catch (IOException e) {
            logger.log("Ran into an IOException" + Arrays.toString(e.getStackTrace()));
        }
    }

    // Overridden for better type safety.
    // If your plugin doesn't really define any property on Descriptor,
    // you don't have to do this.
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }

    @Symbol("flockNotify")
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Send Flock Notification";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
            req.bindJSON(this, json);
            save();
            return super.configure(req, json);
        }
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

}
