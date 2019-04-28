package com.ying.controller;

import com.ying.config.Config;
import com.ying.constant.BaseResultEnum;
import com.ying.dto.resp.BaseRespDto;
import com.ying.service.IActService;
import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lyz
 */
@RestController
@RequestMapping("proc")
public class ProcessController {

    @Autowired
    private IActService actService;

    @Autowired
    private Config config;

    /**
     * 根据流程实例ID获取流程图url（实时跟踪流程图,路线高亮展示）
     *
     * @param piId
     * @throws Exception
     */
    @GetMapping(value = "view/{processInstanceId}")
    public BaseRespDto getProcessImage(@PathVariable("processInstanceId") String piId) {
        try {
            if (StringUtils.isEmpty(piId)) {
                return new BaseRespDto(BaseResultEnum.PARAMETER_ERROR);
            }
            ProcessDefinition pd = actService.getProcessDefByInstanceId(piId);
            if (pd == null) {
                return new BaseRespDto(BaseResultEnum.PARAMETER_ERROR);
            }
            StringBuffer sb = new StringBuffer();
            sb.append(config.domain);
            sb.append("/diagram-viewer/index.html?processDefinitionId=").append(pd.getId());
            sb.append("&processInstanceId=").append(piId);
            return new BaseRespDto(BaseResultEnum.SUCCESS, sb.toString());
        } catch (Exception e) {
            return new BaseRespDto(BaseResultEnum.SERVER_ERROR);
        }
    }
}
