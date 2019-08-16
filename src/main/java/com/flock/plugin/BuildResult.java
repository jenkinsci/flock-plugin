package com.flock.plugin;

public enum BuildResult {

    START, SUCCESS, FAILURE, ABORTED, NOT_BUILT, REGRESSION, UNSTABLE, BACK_TO_NORMAL;

    public String stringValue() {
        switch (this) {
            case START:
                return "start";
            case SUCCESS:
                return "success";
            case FAILURE:
                return "failure";
            case ABORTED:
                return "aborted";
            case NOT_BUILT:
                return "not built";
            case REGRESSION:
                return "regression";
            case UNSTABLE:
                return "unstable";
            case BACK_TO_NORMAL:
                return "back to normal";
        }
        return null;
    }
}
