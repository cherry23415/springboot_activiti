//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.ying.controller.diagram;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * 解决流程图部分线不展示，以及结束流程不显示轨迹的问题
 *
 * @author lyz
 */
@RestController
public class MyProcessInstanceHighlightsResource {
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private HistoryService historyService;
    protected ObjectMapper objectMapper = new ObjectMapper();

    public MyProcessInstanceHighlightsResource() {
    }

    @RequestMapping(
            value = {"/design/process-instance/{processInstanceId}/highlights"},
            method = {RequestMethod.GET},
            produces = {"application/json"}
    )
    public ObjectNode getHighlighted(@PathVariable String processInstanceId) {
        ObjectNode responseJSON = this.objectMapper.createObjectNode();
        responseJSON.put("processInstanceId", processInstanceId);
        ArrayNode activitiesArray = this.objectMapper.createArrayNode();
        ArrayNode flowsArray = this.objectMapper.createArrayNode();

        try {
            ProcessInstance processInstance = this.runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();

            ProcessDefinitionEntity processDefinition = null;

            List<String> highLightedActivities = Lists.newArrayList();
            if (processInstance != null) {
                processDefinition = (ProcessDefinitionEntity) this.repositoryService.getProcessDefinition(processInstance.getProcessDefinitionId());
                highLightedActivities = this.runtimeService.getActiveActivityIds(processInstanceId);
            } else {
                HistoricProcessInstance historicProcessInstance =
                        historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
                processDefinition = (ProcessDefinitionEntity) this.repositoryService.getProcessDefinition(historicProcessInstance.getProcessDefinitionId());
                highLightedActivities.add(historyService
                        .createHistoricActivityInstanceQuery()
                        .processInstanceId(processInstanceId).activityType("endEvent")
                        .singleResult().getActivityId());
            }
            responseJSON.put("processDefinitionId", processDefinition.getId());
            List<String> highLightedFlows = this.getHighLightedFlows(processDefinition, processInstanceId);
            Iterator var9 = highLightedActivities.iterator();

            String flow;
            while (var9.hasNext()) {
                flow = (String) var9.next();
                activitiesArray.add(flow);
            }

            var9 = highLightedFlows.iterator();

            while (var9.hasNext()) {
                flow = (String) var9.next();
                flowsArray.add(flow);
            }
        } catch (Exception var11) {
            var11.printStackTrace();
        }

        responseJSON.set("activities", activitiesArray);
        responseJSON.set("flows", flowsArray);
        return responseJSON;
    }

    private List<String> getHighLightedFlows(ProcessDefinitionEntity processDefinition, String processInstanceId) {
        List<String> highLightedFlows = new ArrayList();
        List<HistoricActivityInstance> historicActivityInstances = this.historyService
                .createHistoricActivityInstanceQuery().processInstanceId(processInstanceId)
                .orderByHistoricActivityInstanceStartTime().asc().list();
        LinkedList<HistoricActivityInstance> hisActInstList = new LinkedList();
        hisActInstList.addAll(historicActivityInstances);
        this.getHighlightedFlows(processDefinition.getActivities(), hisActInstList, highLightedFlows);
        return highLightedFlows;
    }

    private void getHighlightedFlows(List<ActivityImpl> activityList, LinkedList<HistoricActivityInstance> hisActInstList, List<String> highLightedFlows) {
        List<ActivityImpl> startEventActList = new ArrayList();
        Map<String, ActivityImpl> activityMap = new HashMap(activityList.size());
        Iterator var6 = activityList.iterator();

        while (var6.hasNext()) {
            ActivityImpl activity = (ActivityImpl) var6.next();
            activityMap.put(activity.getId(), activity);
            String actType = (String) activity.getProperty("type");
            if (actType != null && actType.toLowerCase().indexOf("startEvent") >= 0) {
                startEventActList.add(activity);
            }
        }

        HistoricActivityInstance firstHistActInst = (HistoricActivityInstance) hisActInstList.getFirst();
        String firstActType = firstHistActInst.getActivityType();
        if (firstActType != null && firstActType.toLowerCase().indexOf("startEvent") < 0) {
            PvmTransition startTrans = this.getStartTransaction(startEventActList, firstHistActInst);
            if (startTrans != null) {
                highLightedFlows.add(startTrans.getId());
            }
        }

        while (true) {
            ActivityImpl activity;
            HistoricActivityInstance histActInst;
            do {
                if (hisActInstList.isEmpty()) {
                    return;
                }

                histActInst = (HistoricActivityInstance) hisActInstList.removeFirst();
                activity = (ActivityImpl) activityMap.get(histActInst.getActivityId());
            } while (activity == null);

            boolean isParallel = false;
            String type = histActInst.getActivityType();
            if (!"parallelGateway".equals(type) && !"inclusiveGateway".equals(type)) {
                if ("subProcess".equals(histActInst.getActivityType())) {
                    this.getHighlightedFlows(activity.getActivities(), hisActInstList, highLightedFlows);
                }
            } else {
                isParallel = true;
            }

            List<PvmTransition> allOutgoingTrans = new ArrayList();
            allOutgoingTrans.addAll(activity.getOutgoingTransitions());
            allOutgoingTrans.addAll(this.getBoundaryEventOutgoingTransitions(activity));
            List<String> activityHighLightedFlowIds = this.getHighlightedFlows(allOutgoingTrans, hisActInstList, isParallel);
            highLightedFlows.addAll(activityHighLightedFlowIds);
        }
    }

    private PvmTransition getStartTransaction(List<ActivityImpl> startEventActList, HistoricActivityInstance firstActInst) {
        Iterator var3 = startEventActList.iterator();

        while (var3.hasNext()) {
            ActivityImpl startEventAct = (ActivityImpl) var3.next();
            Iterator var5 = startEventAct.getOutgoingTransitions().iterator();

            while (var5.hasNext()) {
                PvmTransition trans = (PvmTransition) var5.next();
                if (trans.getDestination().getId().equals(firstActInst.getActivityId())) {
                    return trans;
                }
            }
        }

        return null;
    }

    private List<PvmTransition> getBoundaryEventOutgoingTransitions(ActivityImpl activity) {
        List<PvmTransition> boundaryTrans = new ArrayList();
        Iterator var3 = activity.getActivities().iterator();

        while (var3.hasNext()) {
            ActivityImpl subActivity = (ActivityImpl) var3.next();
            String type = (String) subActivity.getProperty("type");
            if (type != null && type.toLowerCase().indexOf("boundary") >= 0) {
                boundaryTrans.addAll(subActivity.getOutgoingTransitions());
            }
        }

        return boundaryTrans;
    }

    private List<String> getHighlightedFlows(List<PvmTransition> pvmTransitionList, LinkedList<HistoricActivityInstance> hisActInstList, boolean isParallel) {
        List<String> highLightedFlowIds = new ArrayList();
        PvmTransition earliestTrans = null;
        HistoricActivityInstance earliestHisActInst = null;
        Iterator var7 = pvmTransitionList.iterator();

        while (true) {
            while (true) {
                PvmTransition pvmTransition;
                HistoricActivityInstance destHisActInst;
                do {
                    if (!var7.hasNext()) {
                        if (!isParallel && earliestTrans != null) {
                            highLightedFlowIds.add(earliestTrans.getId());
                        }

                        return highLightedFlowIds;
                    }

                    pvmTransition = (PvmTransition) var7.next();
                    String destActId = pvmTransition.getDestination().getId();
                    destHisActInst = this.findHisActInst(hisActInstList, destActId);
                } while (destHisActInst == null);

                if (isParallel) {
                    highLightedFlowIds.add(pvmTransition.getId());
                } else if (earliestHisActInst == null || earliestHisActInst.getId().compareTo(destHisActInst.getId()) > 0) {
                    earliestTrans = pvmTransition;
                    earliestHisActInst = destHisActInst;
                }
            }
        }
    }

    private HistoricActivityInstance findHisActInst(LinkedList<HistoricActivityInstance> hisActInstList, String actId) {
        Iterator var3 = hisActInstList.iterator();

        HistoricActivityInstance hisActInst;
        do {
            if (!var3.hasNext()) {
                return null;
            }

            hisActInst = (HistoricActivityInstance) var3.next();
        } while (!hisActInst.getActivityId().equals(actId));

        return hisActInst;
    }
}
