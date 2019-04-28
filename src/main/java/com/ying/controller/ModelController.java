package com.ying.controller;

import com.ying.config.Config;
import com.ying.constant.BaseResultEnum;
import com.ying.dto.resp.BaseRespDto;
import com.ying.model.ActModel;
import com.ying.service.IModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

/**
 * 自定义流程控制类
 *
 * @author lyz
 */
@RestController
@RequestMapping("/model")
public class ModelController {

    @Autowired
    private IModelService modelService;

    @Autowired
    private Config config;

    /**
     * 获取项目访问路径
     *
     * @return
     */
    protected String getProjectUrl() {
        return config.domain;
    }

    /**
     * 创建模型
     *
     * @param actModel
     */
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public BaseRespDto createModel(@RequestBody ActModel actModel) {
        try {
            if (actModel == null
                    || StringUtils.isEmpty(actModel.getName())
                    || StringUtils.isEmpty(actModel.getCategory())
            ) {
                return new BaseRespDto(BaseResultEnum.PARAMETER_ERROR);
            }

            //流程分类参数校验
            try {
                if (Integer.parseInt(actModel.getCategory()) <= 0) {
                    return new BaseRespDto(BaseResultEnum.PARAMETER_ERROR);
                }
            } catch (Exception e) {
                return new BaseRespDto(BaseResultEnum.PARAMETER_ERROR);
            }
            actModel.setKey(modelService.generateModelKey(actModel.getName()));
            String modelId = modelService.createModel(actModel);
            StringBuffer sb = new StringBuffer();
            sb.append(getProjectUrl());
            sb.append("/modeler.html?modelId=").append(modelId);
            return new BaseRespDto(BaseResultEnum.SUCCESS, sb.toString());
        } catch (Exception e) {
            return new BaseRespDto(BaseResultEnum.SERVER_ERROR);
        }
    }

    /**
     * 已发布的流程转为model编辑
     *
     * @param processDefinitionId
     */
    @RequestMapping(value = "convert/{processDefinitionId}", method = RequestMethod.GET)
    public BaseRespDto convertToModel(@PathVariable String processDefinitionId) {
        try {
            if (StringUtils.isEmpty(processDefinitionId)) {
                return new BaseRespDto(BaseResultEnum.PARAMETER_ERROR);
            }

            String modelId = modelService.convertToModel(processDefinitionId);
            StringBuffer sb = new StringBuffer();
            sb.append(getProjectUrl());
            sb.append("/modeler.html?modelId=").append(modelId);
            return new BaseRespDto(BaseResultEnum.SUCCESS, sb.toString());
        } catch (Exception e) {
            return new BaseRespDto(BaseResultEnum.SERVER_ERROR);
        }
    }

    /**
     * 跳转到编辑流程图页面
     *
     * @param modelId
     */
    @RequestMapping(value = "get/{modelId}", method = RequestMethod.GET)
    public BaseRespDto getModel(@PathVariable String modelId) {
        try {
            if (StringUtils.isEmpty(modelId)) {
                return new BaseRespDto(BaseResultEnum.PARAMETER_ERROR);
            }
            StringBuffer sb = new StringBuffer();
            sb.append(getProjectUrl());
            sb.append("/modeler.html?modelId=").append(modelId);
            return new BaseRespDto(BaseResultEnum.SUCCESS, sb.toString());
        } catch (Exception e) {
            return new BaseRespDto(BaseResultEnum.SERVER_ERROR);
        }
    }

    /**
     * 根据model发布流程，在线发布自定义流程
     *
     * @param modelId
     * @return
     */
    @RequestMapping(value = "deploy/{modelId}", method = RequestMethod.GET)
    public BaseRespDto deployModel(@PathVariable String modelId) {
        try {
            if (StringUtils.isEmpty(modelId)) {
                return new BaseRespDto(BaseResultEnum.PARAMETER_ERROR);
            }
            int flag = modelService.deployModel(modelId);
            return new BaseRespDto(BaseResultEnum.SUCCESS);
        } catch (Exception e) {
            return new BaseRespDto(BaseResultEnum.SERVER_ERROR);
        }
    }

    /**
     * 根据模型ID获取流程图，流程图预览
     *
     * @param modelId
     * @throws Exception
     */
    @RequestMapping(value = "/view/{modelId}")
    public BaseRespDto image(@PathVariable("modelId") String modelId) throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append(getProjectUrl());
        sb.append("/design/model/image/").append(modelId);
        return new BaseRespDto(BaseResultEnum.SUCCESS, sb.toString());
    }

    @RequestMapping(value = "/image/{modelId}")
    public void downloadProcessImage(@PathVariable("modelId") String modelId, HttpServletResponse response) throws Exception {
        InputStream in = modelService.getModelImage(modelId);
        responseOut(response, in);
    }

    /**
     * 流程删除
     *
     * @param modelId
     */
    @RequestMapping(value = "del/{modelId}", method = RequestMethod.GET)
    public BaseRespDto delModel(@PathVariable String modelId) {
        try {
            if (StringUtils.isEmpty(modelId)) {
                return new BaseRespDto(BaseResultEnum.PARAMETER_ERROR);
            }
            return new BaseRespDto(BaseResultEnum.SUCCESS);
        } catch (Exception e) {
            return new BaseRespDto(BaseResultEnum.SERVER_ERROR);
        }
    }

    /**
     * @param response
     * @param in
     */
    protected void responseOut(HttpServletResponse response, InputStream in) {
        byte[] b = new byte[256];
        int len = -1;
        try {
            while ((len = in.read(b)) > 0) {
                response.getOutputStream().write(b, 0, len);
            }
            in.close();
            response.getOutputStream().flush();
            response.getOutputStream().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取流程节点列表
     *
     * @return
     */
    @RequestMapping(value = "activity/list/{modelId}", method = RequestMethod.GET)
    public BaseRespDto activityList(@PathVariable String modelId) {
        try {
            if (StringUtils.isEmpty(modelId)) {
                return new BaseRespDto(BaseResultEnum.PARAMETER_ERROR);
            }
            return new BaseRespDto(BaseResultEnum.SUCCESS, modelService.getActivityList(modelId));
        } catch (Exception e) {
            return new BaseRespDto(BaseResultEnum.SERVER_ERROR);
        }
    }
}
