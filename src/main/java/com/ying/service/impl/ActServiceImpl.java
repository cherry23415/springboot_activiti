package com.ying.service.impl;

import com.google.common.collect.Lists;
import com.ying.service.IActService;
import com.ying.service.IModelService;
import com.ying.service.impl.jump.JumpActivityCmd;
import com.ying.util.StringUtil;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipInputStream;

/**
 * 流程先关操作
 *
 * @author lyz
 */
@Service
public class ActServiceImpl implements IActService {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private IModelService modelService;

    @Autowired
    private ManagementService managementService;

    @Override
    public void deployZip(ZipInputStream zipInputStream, String fileName) throws Exception {
        // 创建发布配置对象
        DeploymentBuilder builder = repositoryService.createDeployment();
        fileName = fileName.substring(0, fileName.indexOf(".zip"));
        // 设置发布信息
        builder
                .name(fileName)// 添加部署规则的显示别名
                .addZipInputStream(zipInputStream);
        // 完成发布
        builder.deploy();
    }

    @Override
    public ProcessDefinition getProcessDefById(String deploymentId) throws Exception {
        return repositoryService.createProcessDefinitionQuery().deploymentId(deploymentId).singleResult();
    }

    @Override
    public ProcessDefinition getProcessDefByInstanceId(String piId) throws Exception {
        ProcessInstance pi = getProcessInstanceById(piId);
        if (pi == null) {
            HistoricProcessInstance historicProcessInstance =
                    historyService.createHistoricProcessInstanceQuery().processInstanceId(piId).singleResult();
            if (historicProcessInstance == null) {
                return null;
            }
            return getProcessDefById(historicProcessInstance.getDeploymentId());
        }
        return getProcessDefById(pi.getDeploymentId());
    }

    @Override
    public ProcessInstance startProcess(String processDefinitionId, Map<String, Object> variables) throws Exception {
        return runtimeService.startProcessInstanceById(processDefinitionId, variables);
    }

    @Override
    public void passProcess(String taskId) throws Exception {
        taskService.complete(taskId);
    }

    @Override
    public void passProcess(String taskId, Map variable) throws Exception {
        taskService.setVariables(taskId, variable);
        taskService.complete(taskId);
    }

    @Override
    public void suspendProcessInstanceById(String piId) throws Exception {
        runtimeService.suspendProcessInstanceById(piId);
    }

    @Override
    public boolean rejectProcess(String taskId, String destActivityId) throws Exception {
        // 取得当前任务.当前任务节点
        Task currTask = getParentTaskByTaskId(taskId);
        if (currTask == null) {
            return false;
        }
        // 取得流程定义
        ProcessDefinitionEntity definition = (ProcessDefinitionEntity) repositoryService
                .getProcessDefinition(currTask.getProcessDefinitionId());
        if (definition == null) {
            return false;
        }
        //取得当前活动节点
        ActivityImpl currActivity = definition.findActivity(currTask.getTaskDefinitionKey());

        // 清除当前活动的出口
        List<PvmTransition> oriPvmTransitionList = clearTransition(currActivity);

        //获取目标节点
        if (destActivityId == null) {
            return false;
        }
        ActivityImpl nextActivity = definition.findActivity(destActivityId);
        if (nextActivity == null) {
            return false;
        }

        //创建新出口
        TransitionImpl newTransition = currActivity.createOutgoingTransition();
        newTransition.setDestination(nextActivity);

        // 完成当前所有父任务,并删除当前任务的历史
        List<Task> tasks = getParentTasksByPiId(currTask.getProcessInstanceId());
        for (Task task : tasks) {
            taskService.complete(task.getId());
            historyService.deleteHistoricTaskInstance(task.getId());
        }

        // 删除目标节点新流入
        nextActivity.getIncomingTransitions().remove(newTransition);

        // 还原以前流向
        restoreTransition(currActivity, oriPvmTransitionList);
        return true;
    }

    /**
     * 清空指定活动节点流向
     *
     * @param activityImpl 活动节点
     * @return 节点流向集合
     */
    private List<PvmTransition> clearTransition(ActivityImpl activityImpl) {
        // 存储当前节点所有流向临时变量
        List<PvmTransition> oriPvmTransitionList = Lists.newArrayList();
        // 获取当前节点所有流向，存储到临时变量，然后清空
        List<PvmTransition> pvmTransitionList = activityImpl
                .getOutgoingTransitions();
        for (PvmTransition pvmTransition : pvmTransitionList) {
            oriPvmTransitionList.add(pvmTransition);
        }
        pvmTransitionList.clear();

        return oriPvmTransitionList;
    }

    /**
     * 还原指定活动节点流向
     *
     * @param activityImpl         活动节点
     * @param oriPvmTransitionList 原有节点流向集合
     */
    private void restoreTransition(ActivityImpl activityImpl,
                                   List<PvmTransition> oriPvmTransitionList) {
        // 清空现有流向
        List<PvmTransition> pvmTransitionList = activityImpl
                .getOutgoingTransitions();
        pvmTransitionList.clear();
        // 还原以前流向
        for (PvmTransition pvmTransition : oriPvmTransitionList) {
            pvmTransitionList.add(pvmTransition);
        }
    }

    @Override
    public boolean rejectProcessComm(String taskId, String destActivityId) throws Exception {
        // 取得当前任务.当前任务节点
        Task currTask = getParentTaskByTaskId(taskId);
        if (currTask == null) {
            return false;
        }
        // 取得流程定义
        ProcessDefinitionEntity definition = (ProcessDefinitionEntity) repositoryService
                .getProcessDefinition(currTask.getProcessDefinitionId());
        if (definition == null) {
            return false;
        }
        //取得当前活动节点
        ActivityImpl currActivity = definition.findActivity(currTask.getTaskDefinitionKey());
        if (currActivity == null) {
            return false;
        }

        //获取目标节点
        if (destActivityId == null) {
            return false;
        }
        ActivityImpl nextActivity = definition.findActivity(destActivityId);
        if (nextActivity == null) {
            return false;
        }
        managementService.executeCommand(new JumpActivityCmd(destActivityId, currTask.getProcessInstanceId()));
        return true;
    }

    /**
     * 获取已完成历史任务节点
     *
     * @param piId
     * @return
     * @throws Exception
     */
    private List<HistoricActivityInstance> getHistoryActivityList(String piId) throws Exception {
        return historyService.createHistoricActivityInstanceQuery().processInstanceId(piId).activityType("userTask")
                .orderByHistoricActivityInstanceStartTime().asc().list();
    }

    @Override
    public HistoricTaskInstance getHistoryTask(String taskId) throws Exception {
        return historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
    }

    @Override
    public ProcessInstance getProcessInstanceById(String piId) throws Exception {
        return runtimeService.createProcessInstanceQuery().processInstanceId(piId).singleResult();
    }

    @Override
    public boolean checkIsSubTaskByTask(String piId, Task task) throws Exception {
        if (task != null && !StringUtils.isEmpty(task.getParentTaskId())) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isSubTask(String piId, String taskId) throws Exception {
        Task task = getTaskByTaskId(taskId);
        if (task != null && !StringUtils.isEmpty(task.getParentTaskId())) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isLastSubTask(String piId, String taskId) throws Exception {
        long count = taskService.createTaskQuery().processInstanceId(piId).count();
        if (count > 1) {
            Task parentTask = getParentTaskByTaskId(taskId);
            List<Task> subTaskList = getSubTasks(parentTask.getId());
            if (subTaskList != null && subTaskList.size() == 1) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Task getFirstTaskByPiId(String piId) throws Exception {
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(piId).list();
        if (taskList != null && taskList.size() == 1) {
            return taskList.get(0); //唯一的一个任务
        }
        return null;
    }

    @Override
    public List<Task> getParentTasksByPiId(String piId) throws Exception {
        return taskService.createTaskQuery().processInstanceId(piId).excludeSubtasks().list();
    }

    @Override
    public List<Task> getTasksByPiIdAndUser(String piId, String userId) throws Exception {
        // 根据流程实例ID和用户ID获取当前待执行任务

        //该api存在bug,换一种方式获取任务列表
//        return taskService.createTaskQuery().processInstanceId(piId).taskCandidateOrAssigned(userId).list();

        List<Task> result = Lists.newArrayList();
        List<Task> taskInfoList1 = getTasksByPiId(piId);
        List<String> assList;
        for (Task s : taskInfoList1) {
            assList = StringUtil.str2List(getAssigneeWithoutSub(s));
            if (assList != null && assList.size() > 0 && assList.contains(userId)) {
                result.add(s);
            }
        }
        return result;
    }

    /**
     * 获取任务审批人，不包含子节点
     *
     * @param task
     * @return
     * @throws Exception
     */
    private String getAssigneeWithoutSub(Task task) throws Exception {
        String assignee = "";
        if (task != null) {
            List<String> list = getAllAssignee(task.getId());
            if (list != null && list.size() > 0) {
                assignee = StringUtil.list2Str(list);
            } else if (task.getAssignee() != null) {
                assignee = task.getAssignee();
            }
        }
        //审批人去重
        if (!StringUtils.isEmpty(assignee)) {
            assignee = StringUtil.strDistinct(assignee);
        }
        return assignee;
    }

    @Override
    public List<Task> getTasksByPiId(String piId) throws Exception {
        return taskService.createTaskQuery().processInstanceId(piId).list();
    }

    @Override
    public Task getParentTaskByTaskId(String taskId) throws Exception {
        Task task = getTaskByTaskId(taskId);
        if (task != null && task.getParentTaskId() != null) {
            task = getTaskByTaskId(task.getParentTaskId());
        }
        return task;
    }

    @Override
    public Task getTaskByTaskId(String taskId) throws Exception {
        return taskService.createTaskQuery().taskId(taskId).singleResult();
    }

    @Override
    public void setAssignee(String taskId, String assignee) throws Exception {
        taskService.setAssignee(taskId, assignee);
    }

    @Override
    public String getAssignee(Task task) throws Exception {
        String assignee = "";
        if (task != null) {
            List<String> list = getAllAssignee(task.getId());
            if (list != null && list.size() > 0) {
                assignee = StringUtil.list2Str(list);
            } else if (task.getAssignee() != null) {
                assignee = task.getAssignee();
            }
            // 含有子流程
            List<String> subAssignees = getSubTaskAssignees(task.getId());
            if (subAssignees != null && subAssignees.size() > 0) {
                if (!StringUtils.isEmpty(assignee)) {
                    assignee += "," + StringUtil.list2Str(subAssignees);
                } else {
                    assignee = StringUtil.list2Str(subAssignees);
                }
            }
        }
        //审批人去重
        if (!StringUtils.isEmpty(assignee)) {
            assignee = StringUtil.strDistinct(assignee);
        }
        return assignee;
    }

    @Override
    public boolean isAssignee(String taskId, String userId) throws Exception {
        Task task = getParentTaskByTaskId(taskId);
        if (task == null) {
            return false;
        }
        // 单人审批
        String taskAssignee = task.getAssignee();
        // 多人审批
        List<String> assinLists = getAllAssignee(task.getId());
        // 子流程
        List<String> subAssignees = getSubTaskAssignees(task.getId());
        if (subAssignees != null && subAssignees.size() > 0 && subAssignees.contains(userId)) {
            return true;
        } else if (assinLists != null && assinLists.size() > 0 && assinLists.contains(userId)) {
            return true;
        } else if (taskAssignee != null && taskAssignee.equals(userId)) {
            // 单人审批节点
            return true;
        }
        return false;
    }

    /**
     * 获取当前任务,所有参与人(多人审批)
     *
     * @param taskId
     * @return
     */
    private List<String> getAllAssignee(String taskId) throws Exception {
        List<String> assigneeList = Lists.newArrayList();
        List<IdentityLink> list = taskService.getIdentityLinksForTask(taskId);// 获取列表
        if (list != null && list.size() > 0) {
            for (IdentityLink il : list) {
                assigneeList.add(il.getUserId());
            }
        }
        return assigneeList;
    }

    /**
     * 获取子任务审批人
     *
     * @param taskId
     * @return
     * @throws Exception
     */
    public List<String> getSubTaskAssignees(String taskId) throws Exception {
        List<Task> subTaskList = getSubTasks(taskId);
        if (subTaskList != null && subTaskList.size() > 0) {
            //表示有子任务，为会签节点
            List<String> assigneeList = Lists.newArrayList();
            String assignee;
            for (Task task : subTaskList) {
                assignee = task.getAssignee();
                assigneeList.add(assignee);
            }
            return assigneeList;
        }
        return null;
    }

    @Override
    public List<Model> getModelList() throws Exception {
        return repositoryService.createModelQuery().list();
    }

    @Override
    public Model getModelById(String modelId) throws Exception {
        return repositoryService.createModelQuery().modelId(modelId).singleResult();
    }

    @Override
    public InputStream getModelImage(String modelId) throws Exception {
        byte[] b1 = repositoryService.getModelEditorSourceExtra(modelId);
        InputStream in = new ByteArrayInputStream(b1);
        return in;
    }

    @Override
    public List<ProcessDefinition> getUsedProcessList() throws Exception {
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery()
                .orderByProcessDefinitionVersion().asc().list();
        return list;
    }

    @Override
    public String getProcessModelSource(String pdId) throws Exception {
        InputStream in = repositoryService.getProcessModel(pdId);
        return IOUtils.toString(in, "utf-8");
    }

    @Override
    public List<String> getProcessModelParam(String pdId) throws Exception {
        List<String> list = null;
        String resourceStr = getProcessModelSource(pdId);
        if (!StringUtils.isEmpty(resourceStr)) {
            list = Lists.newArrayList();
            String param;
            String resource1 = resourceStr;
            while (!StringUtils.isEmpty(resource1)
                    && resource1.indexOf("${") > -1
                    && resource1.indexOf("}") > resource1.indexOf("${")) {
                param = resource1.substring(resource1.indexOf("${"), resource1.indexOf("}") + 1);
                resource1 = resource1.substring(resource1.indexOf("}") + 1);
                list.add(param);
            }
        }
        return list;
    }

    @Override
    public boolean jointProcess(Task parentTask, Long[] userIds, long currentUser) throws Exception {
        if (parentTask == null) {
            return false;
        }
        if (userIds == null || userIds.length == 0) {
            return false;
        }
        String parentTaskId = parentTask.getId();

        //给当前父任务审批人增加一个子任务;如果当前节点已经创建子任务，则不创建;
        List<Task> subTasks = getSubTasks(parentTaskId);
        boolean hasCurrentSubFlag = false; //当前操作人是否已经被创建子任务
        if (subTasks != null && subTasks.size() > 0) {
            List<String> assList;
            String ass;
            for (Task t : subTasks) {
                assList = getAllAssignee(t.getId());
                ass = t.getAssignee();
                if (assList != null && assList.contains(String.valueOf(currentUser))) {
                    hasCurrentSubFlag = true;
                } else if (ass != null && ass.equals(String.valueOf(currentUser))) {
                    hasCurrentSubFlag = true;
                }
            }
        }
        if (!hasCurrentSubFlag) {
            TaskEntity subTask1 = (TaskEntity) taskService.newTask(UUID.randomUUID().toString());
            subTask1.setAssignee(String.valueOf(currentUser)); //会签节点审批人只能为一人
            subTask1.setName(parentTask.getName() + "-会签1");
            subTask1.setProcessDefinitionId(parentTask.getProcessDefinitionId());
            subTask1.setProcessInstanceId(parentTask.getProcessInstanceId());
            subTask1.setParentTaskId(parentTaskId);
            subTask1.setDescription("jointProcess");
            taskService.saveTask(subTask1);
        }
        //清空父节点审批人；
        clearAllAssignee(parentTaskId);

        //给目标用户创建子任务
        for (int i = 0; i < userIds.length; i++) {
            TaskEntity subTask = (TaskEntity) taskService.newTask(UUID.randomUUID().toString());
            subTask.setAssignee(String.valueOf(userIds[i]));
            subTask.setName(parentTask.getName() + "-会签" + (i + 2));
            subTask.setProcessDefinitionId(parentTask.getProcessDefinitionId());
            subTask.setProcessInstanceId(parentTask.getProcessInstanceId());
            subTask.setParentTaskId(parentTaskId);
            subTask.setDescription("jointProcess");
            taskService.saveTask(subTask);
        }

        return true;
    }

    @Override
    public List<Task> getSubTasks(String parentTaskId) throws Exception {
        return taskService.getSubTasks(parentTaskId);
    }

    @Override
    public Task getSubTaskByUser(String userId, List<Task> subTasks) throws Exception {
        if (subTasks != null && subTasks.size() > 0) {
            List<String> assList;
            for (Task t : subTasks) {
                assList = StringUtil.str2List(getAssigneeWithoutSub(t));
                if (assList != null && assList.size() > 0 && assList.contains(userId)) {
                    return t;
                }
            }
        }
        return null;
    }

    @Override
    public void addCandidateUsers(String taskId, Long[] userIds) throws Exception {
        for (Long userId : userIds) {
            taskService.addCandidateUser(taskId, String.valueOf(userId));
        }
    }

    @Override
    public void clearCurrentUser(String taskId, String userId) throws Exception {
        //2017/11/1 清除当前登录的审批人
        taskService.deleteCandidateUser(taskId, userId);
        this.setAssignee(taskId, null);
    }

    @Override
    public void clearAllAssignee(String taskId) throws Exception {
        List<String> assList = getAllAssignee(taskId);
        if (assList != null && assList.size() > 0) {
            for (String s : assList) {
                taskService.deleteCandidateUser(taskId, s);
            }
        }
        this.setAssignee(taskId, null);
    }

    @Override
    public String getActivityIdByTask(Task task) throws Exception {
        Execution execution = runtimeService.createExecutionQuery().executionId(task.getExecutionId()).singleResult();
        if (execution != null) {
            return execution.getActivityId();
        }
        return null;
    }
}
