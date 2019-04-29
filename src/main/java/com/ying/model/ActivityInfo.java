package com.ying.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 流程节点信息
 *
 * @author lyz
 */
@Data
public class ActivityInfo implements Serializable {

    private String activityId;

    private String activityName;
}
