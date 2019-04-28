package com.ying.service;

import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

/**
 * activiti接口封装
 * <p>
 * @author lyz
 */
public interface IActService {

    /**
     * 发根据zip布流程
     * 会在三张表中产生数据：
     * act_ge_bytearray 产生两条数据
     * act_re_deployment 产生一条数据
     * act_re_procdef 产生一条数据
     *
     * @param zipInputStream
     * @param fileName
     */
    void deployZip(ZipInputStream zipInputStream, String fileName) throws Exception;

    /**
     * 根据deploymentId获取流程定义ProcessDefinition
     *
     * @param deploymentId
     * @return
     */
    ProcessDefinition getProcessDefById(String deploymentId) throws Exception;

    /**
     * 根据piId获取流程定义ProcessDefinition
     *
     * @param piId
     * @return
     */
    ProcessDefinition getProcessDefByInstanceId(String piId) throws Exception;

    /**
     * 启动流程
     *
     * @param processDefinitionId 流程定义Id
     * @param variables           审批参数
     * @return 返回流程实例
     */
    ProcessInstance startProcess(String processDefinitionId, Map<String, Object> variables) throws Exception;

    /**
     * 审批通过
     *
     * @param taskId 当前任务ID
     * @throws Exception
     */
    void passProcess(String taskId) throws Exception;

    /**
     * 审批通过
     *
     * @param taskId   当前任务ID
     * @param variable 流程参数
     * @throws Exception
     */
    void passProcess(String taskId, Map variable) throws Exception;

    /**
     * 流程挂起
     *
     * @param piId
     * @throws Exception
     */
    void suspendProcessInstanceById(String piId) throws Exception;

    /**
     * 审批退回(该退回操作，并行网关存在问题)
     *
     * @param taskId         任务ID
     * @param destActivityId 目标节点
     * @return
     */
    boolean rejectProcess(String taskId, String destActivityId) throws Exception;

    /**
     * 审批退回（强制）
     *
     * @param taskId         任务ID
     * @param destActivityId 目标节点
     * @return
     */
    boolean rejectProcessComm(String taskId, String destActivityId) throws Exception;

    /**
     * 获取历史任务
     *
     * @param taskId
     * @return
     * @throws Exception
     */
    HistoricTaskInstance getHistoryTask(String taskId) throws Exception;

    /**
     * 根据流程实例ID获取实例
     *
     * @param piId
     * @return
     */
    ProcessInstance getProcessInstanceById(String piId) throws Exception;

    /**
     * 是否为子任务(转签时需要校验，子任务只能转签给一个人)
     *
     * @param piId
     * @param task
     * @return
     * @throws Exception
     */
    boolean checkIsSubTaskByTask(String piId, Task task) throws Exception;

    /**
     * 是否为子任务(转签时需要校验，子任务只能转签给一个人)
     *
     * @param piId
     * @param taskId
     * @return
     * @throws Exception
     */
    boolean isSubTask(String piId, String taskId) throws Exception;

    /**
     * 是否为最后一个子任务
     *
     * @param piId
     * @param taskId
     * @return 是为true, 否位false
     * @throws Exception
     */
    boolean isLastSubTask(String piId, String taskId) throws Exception;

    /**
     * 获取第一个任务（即流程提交申请的任务）
     *
     * @param piId 流程实例ID
     * @return
     */
    Task getFirstTaskByPiId(String piId) throws Exception;

    /**
     * 根据流程实例ID获取当前父任务（多个）
     *
     * @param piId
     * @return
     */
    List<Task> getParentTasksByPiId(String piId) throws Exception;

    /**
     * 根据流程实例ID和用户ID获取当前待执行任务
     *
     * @param piId
     * @return
     */
    List<Task> getTasksByPiIdAndUser(String piId, String userId) throws Exception;

    /**
     * 根据流程实例ID获取当前待执行任务
     *
     * @param piId
     * @return
     */
    List<Task> getTasksByPiId(String piId) throws Exception;

    /**
     * 获取当前父任务
     *
     * @param taskId
     * @return
     */
    Task getParentTaskByTaskId(String taskId) throws Exception;

    /**
     * 根据taskId获取当前任务
     *
     * @param taskId
     * @return
     * @throws Exception
     */
    Task getTaskByTaskId(String taskId) throws Exception;

    /**
     * 设置审批人
     *
     * @param taskId
     * @param assignee
     */
    void setAssignee(String taskId, String assignee) throws Exception;

    /**
     * 获取当前任务人
     *
     * @param task
     * @return
     */
    String getAssignee(Task task) throws Exception;

    /**
     * 判断是否为当前审批人
     *
     * @param taskId
     * @param userId
     * @return
     */
    boolean isAssignee(String taskId, String userId) throws Exception;

    /**
     * 获取自定义model列表
     *
     * @return
     */
    List<Model> getModelList() throws Exception;

    /**
     * 根据ID获取model
     *
     * @param modelId
     * @return
     * @throws Exception
     */
    Model getModelById(String modelId) throws Exception;

    /**
     * 查看流程图
     *
     * @param modelId 模型ID
     * @return
     */
    InputStream getModelImage(String modelId) throws Exception;

    /**
     * 查询流程定义
     *
     * @return
     * @throws Exception
     */
    List<ProcessDefinition> getUsedProcessList() throws Exception;

    /**
     * 获取流程文件
     *
     * @param pdId
     * @return
     * @throws Exception
     */
    String getProcessModelSource(String pdId) throws Exception;

    /**
     * 获取流程文件中的配置参数
     *
     * @param pdId
     * @return
     * @throws Exception
     */
    List<String> getProcessModelParam(String pdId) throws Exception;

    /**
     * 加签
     *
     * @param parentTask  任务
     * @param userIds     加签审批人
     * @param currentUser 当前操作人
     * @throws Exception
     */
    boolean jointProcess(Task parentTask, Long[] userIds, long currentUser) throws Exception;

    /**
     * 获取子任务列表
     *
     * @param parentTaskId
     * @return
     * @throws Exception
     */
    List<Task> getSubTasks(String parentTaskId) throws Exception;

    /**
     * 获取用户所属子任务
     *
     * @param userId
     * @param subTasks
     * @return
     * @throws Exception
     */
    Task getSubTaskByUser(String userId, List<Task> subTasks) throws Exception;

    /**
     * 增加多个审批对象
     *
     * @param taskId
     * @param userIds
     * @throws Exception
     */
    void addCandidateUsers(String taskId, Long[] userIds) throws Exception;

    /**
     * 清除当前审批人
     *
     * @param taskId
     * @param userId
     * @throws Exception
     */
    void clearCurrentUser(String taskId, String userId) throws Exception;

    /**
     * 清除节点所有审批人
     *
     * @param taskId
     * @throws Exception
     */
    void clearAllAssignee(String taskId) throws Exception;

    /**
     * 获取当前活动节点ID
     *
     * @param task
     * @return
     * @throws Exception
     */
    String getActivityIdByTask(Task task) throws Exception;
}
