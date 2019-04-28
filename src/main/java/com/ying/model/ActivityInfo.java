package com.ying.model;

import java.io.Serializable;

/**
 * 流程节点信息
 *
 * @author lyz
 */
public class ActivityInfo implements Serializable {

    private String activityId;

    private String activityName;

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }
}
