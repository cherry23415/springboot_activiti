package com.ying.service.impl.jump;

import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;

/**
 * 流程强制跳转
 * Created by lyz on 2018/1/15.
 */
public class JumpActivityCmd implements Command {
    private String activityId;
    private String processInstanceId;
    private String jumpOrigin;


    public JumpActivityCmd(String activityId, String processInstanceId, String jumpOrigin) {
        this.activityId = activityId;
        this.processInstanceId = processInstanceId;
        this.jumpOrigin = jumpOrigin;
    }

    public JumpActivityCmd(String activityId, String processInstanceId) {
        this(activityId, processInstanceId, "jump");
    }

    public Object execute(CommandContext commandContext) {
        ExecutionEntity executionEntity = commandContext.getExecutionEntityManager().findExecutionById(processInstanceId);
        executionEntity.destroyScope(jumpOrigin);
        ProcessDefinitionImpl processDefinition = executionEntity.getProcessDefinition();
        ActivityImpl activity = processDefinition.findActivity(activityId);
        executionEntity.executeActivity(activity);
        return executionEntity;
    }
}