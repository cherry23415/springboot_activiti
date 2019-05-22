package com.ying.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.ying.converter.MyBpmnJsonConverter;
import com.ying.model.ActModel;
import com.ying.model.ActivityInfo;
import com.ying.service.IActService;
import com.ying.service.IModelService;
import com.ying.util.PinYinUtil;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.UserTask;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.List;

/**
 * @author lyz
 */
@Service
public class ModelServiceImpl implements IModelService {

    private static final Logger logger = LogManager.getLogger(ModelServiceImpl.class);

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private IActService actService;

    @Override
    @Transactional
    public String createModel(ActModel actModel) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode editorNode = objectMapper.createObjectNode();
        ObjectNode stencilSetNode = objectMapper.createObjectNode();
        stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
        Model modelData = repositoryService.newModel();

        ObjectNode modelObjectNode = objectMapper.createObjectNode();
        modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME, actModel.getName());
        modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, 1);
        modelObjectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, actModel.getDescription());
        modelData.setMetaInfo(modelObjectNode.toString());
        modelData.setName(actModel.getName());
        modelData.setKey(actModel.getKey());
        modelData.setCategory(actModel.getCategory());

        //保存模型
        repositoryService.saveModel(modelData);
        if (modelData == null) {
            return null;
        }
        String modelId = modelData.getId();
        editorNode.put("resourceId", modelId);
        editorNode.set("stencilset", stencilSetNode);
        repositoryService.addModelEditorSource(modelId, editorNode.toString().getBytes("utf-8"));
        return modelId;
    }

    @Override
    public String convertToModel(String processDefinitionId) throws Exception {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId).singleResult();
        InputStream bpmnStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(),
                processDefinition.getResourceName());
        XMLInputFactory xif = XMLInputFactory.newInstance();
        InputStreamReader in = new InputStreamReader(bpmnStream, "UTF-8");
        XMLStreamReader xtr = xif.createXMLStreamReader(in);
        BpmnModel bpmnModel = new BpmnXMLConverter().convertToBpmnModel(xtr);

        MyBpmnJsonConverter converter = new MyBpmnJsonConverter();
        ObjectNode modelNode = converter.convertToJson(bpmnModel);
        Model modelData = repositoryService.newModel();
        modelData.setKey(processDefinition.getKey());
        modelData.setName(processDefinition.getResourceName());
        modelData.setDeploymentId(processDefinition.getDeploymentId());
        modelData.setCategory(processDefinition.getDeploymentId());

        ObjectNode modelObjectNode = new ObjectMapper().createObjectNode();
        modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME, processDefinition.getName());
        modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, 1);
        modelObjectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, processDefinition.getDescription());
        modelData.setMetaInfo(modelObjectNode.toString());

        repositoryService.saveModel(modelData);

        repositoryService.addModelEditorSource(modelData.getId(), modelNode.toString().getBytes("utf-8"));
        return modelData.getId();
    }

    @Override
    public String generateModelKey(String modelName) throws Exception {
        String key = PinYinUtil.converterToSpell(modelName);
        if (key.length() >= 50) {
            key = PinYinUtil.converterToInitials(modelName); //超长则取首字母
        }
        return key;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deployModel(String modelId) throws Exception {
        Model modelData = getModelById(modelId);
        if (modelData == null) {
            return 1;
        }
        byte[] bpmnBytes;
        BpmnModel model = getBpmnModelByModelId(modelId);
        if (model == null) {
            return 1;
        }
        List<Process> processList = model.getProcesses();
        if (processList == null || processList.size() == 0 || processList.get(0) == null) {
            return 1;
        }
        Collection<FlowElement> collection = processList.get(0).getFlowElements();
        bpmnBytes = new BpmnXMLConverter().convertToXML(model);
        String processName = modelData.getName() + ".bpmn20.xml";
        Deployment deployment = repositoryService.createDeployment().name(modelData.getName()).addString(processName, new String(bpmnBytes)).deploy();
        return 0;
    }

    @Override
    public InputStream getModelImage(String modelId) throws Exception {
        return actService.getModelImage(modelId);
    }

    @Override
    public boolean deleteModel(String modelId) throws Exception {
        if (repositoryService.getModel(modelId) != null) {
            repositoryService.deleteModel(modelId);
            logger.info("deleteModel success,modelId:" + modelId);
        }
        logger.warn("deleteModel fail,modelId:" + modelId);
        return true;
    }

    @Override
    public List<ActivityInfo> getActivityList(String modelId) throws Exception {
        Collection<FlowElement> collection = getFlowElementsByModelId(modelId);
        if (collection == null || collection.size() == 0) {
            return null;
        }
        List<ActivityInfo> activityInfoList = Lists.newArrayList();
        ActivityInfo activityInfo;
        for (FlowElement f : collection) {
            if (f instanceof UserTask) {
                activityInfo = new ActivityInfo();
                activityInfo.setActivityId(f.getId());
                activityInfo.setActivityName(f.getName());
                activityInfoList.add(activityInfo);
            } else {
                continue;
            }
        }
        return activityInfoList;
    }

    @Override
    public Model getModelById(String modelId) throws Exception {
        return repositoryService.getModel(modelId);
    }

    /**
     * 根据modelId生成BpmnModel
     *
     * @param modelId
     * @return
     * @throws Exception
     */
    private BpmnModel getBpmnModelByModelId(String modelId) throws Exception {
        Model modelData = getModelById(modelId);
        if (modelData == null) {
            return null;
        }
        ObjectNode modelNode = (ObjectNode) new ObjectMapper().readTree(repositoryService.getModelEditorSource(modelData.getId()));
        BpmnModel bpmnModel = new MyBpmnJsonConverter().convertToBpmnModel(modelNode);
        return bpmnModel;
    }

    /**
     * 根据modelId获取所有流程任务
     *
     * @param modelId
     * @return
     * @throws Exception
     */
    private Collection<FlowElement> getFlowElementsByModelId(String modelId) throws Exception {
        BpmnModel bpmnModel = getBpmnModelByModelId(modelId);
        if (bpmnModel == null) {
            return null;
        }
        List<Process> processList = bpmnModel.getProcesses();
        if (processList == null || processList.size() == 0 || processList.get(0) == null) {
            return null;
        }
        return processList.get(0).getFlowElements();
    }
}
