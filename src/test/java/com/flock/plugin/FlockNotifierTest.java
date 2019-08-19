package com.flock.plugin;

import net.sf.json.JSONObject;
import org.junit.Test;
import org.mockito.Mockito;


public class FlockNotifierTest {

    private BuildWrapper buildWrapper = Mockito.mock(BuildWrapper.class);


    @Test
    public void testDisplayName() {
        Mockito.when(buildWrapper.getDisplayName()).thenReturn("Test Display Name");
        assert(buildWrapper.getDisplayName()).equals("Test Display Name");
    }

    @Test
    public void testBuildResult() {
        Mockito.when(buildWrapper.getStatus()).thenReturn(BuildResult.SUCCESS);
        assert(buildWrapper.getStatus()).equals(BuildResult.SUCCESS);
    }

    @Test
    public void testBuildDuration() {
        long duration = 1233213131;
        Mockito.when(buildWrapper.getDuration()).thenReturn(duration);
        assert(buildWrapper.getDuration()) == duration;
    }

    @Test
    public void testProjectName() {
        Mockito.when(buildWrapper.getProjectName()).thenReturn("Project 1");
        assert(buildWrapper.getProjectName()).equals("Project 1");
    }


    @Test
    public void testRunURL() {
        Mockito.when(buildWrapper.getRunURL()).thenReturn("https://www.someurl.com");
        assert(buildWrapper.getRunURL()).equals("https://www.someurl.com");
    }

    @Test
    public void testChanges() {
        JSONObject sampleJson = new JSONObject();
        sampleJson.put("changes", "change 1");
        Mockito.when(buildWrapper.getChanges()).thenReturn(sampleJson);
        assert(buildWrapper.getChanges()).equals(sampleJson);
    }

    @Test
    public void testCauseAction() {
        JSONObject sampleJson = new JSONObject();
        sampleJson.put("cause", "cause 1");
        Mockito.when(buildWrapper.getCauseAction()).thenReturn(sampleJson);
        assert(buildWrapper.getCauseAction()).equals(sampleJson);
    }

    @Test
    public void testBuildStarted() {
        Mockito.when(buildWrapper.getStatus()).thenReturn(BuildResult.START);
        JSONObject payload = PayloadManager.createPayload(buildWrapper);
        assert(payload.getString("status")).equals("start");
        assert(!payload.containsKey("duration"));
    }

    @Test
    public void testJsonContainsDuration() {
        Mockito.when(buildWrapper.getStatus()).thenReturn(BuildResult.SUCCESS);
        JSONObject payload = PayloadManager.createPayload(buildWrapper);
        assert(payload.getString("status")).equals(BuildResult.SUCCESS.stringValue());
        assert(payload.containsKey("duration"));
    }

}