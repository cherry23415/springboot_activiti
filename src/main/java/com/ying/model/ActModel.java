package com.ying.model;

import java.io.Serializable;

/**
 * Created by lyz on 2017/6/9.
 */
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getProcessDefinitionId() {
        return processDefinitionId;
    }

    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }
}
