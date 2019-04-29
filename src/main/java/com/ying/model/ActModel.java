package com.ying.model;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by lyz on 2017/6/9.
 */
@Data
public class ActModel implements Serializable {
    /**
     * model名称
     */
    private String name;

    /**
     * model Key
     */
    private String key;

    /**
     * model类别
     */
    private String category;

    /**
     * model描述
     */
    private String description;

    /**
     * modelId
     */
    private String modelId;

    /**
     * 流程processDefinitionId
     */
    private String processDefinitionId;
}
