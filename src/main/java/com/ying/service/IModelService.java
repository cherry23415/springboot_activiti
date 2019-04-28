package com.ying.service;

import com.ying.model.ActModel;
import com.ying.model.ActivityInfo;
import org.activiti.engine.repository.Model;

import java.io.InputStream;
import java.util.List;

/**
 * Created by lyz on 2017/6/13.
 */
public interface IModelService {

    /**
     * 创建model
     *
     * @param actModel
     * @return
     * @throws Exception
     */
    String createModel(ActModel actModel) throws Exception;

    /**
     * 已经发布的流程转为model
     *
     * @param processDefinitionId
     * @return
     * @throws Exception
     */
    String convertToModel(String processDefinitionId) throws Exception;

    /**
     * 生成key
     *
     * @param modelName
     * @return
     * @throws Exception
     */
    String generateModelKey(String modelName) throws Exception;

    /**
     * 根据model发布流程
     *
     * @param modelId
     */
    int deployModel(String modelId) throws Exception;


    /**
     * 查看流程图
     *
     * @param modelId 模型ID
     * @return
     */
    InputStream getModelImage(String modelId) throws Exception;

    /**
     * 删除流程
     *
     * @param modelId
     * @throws Exception
     */
    boolean deleteModel(String modelId) throws Exception;

    /**
     * 获取流程节点列表(用户任务)
     *
     * @param modelId
     * @return
     * @throws Exception
     */
    List<ActivityInfo> getActivityList(String modelId) throws Exception;

    /**
     * 根据modelId获取model
     *
     * @param modelId
     * @return
     * @throws Exception
     */
    Model getModelById(String modelId) throws Exception;
}
