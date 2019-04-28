package com.ying.constant;

/**
 * 返回枚举值
 * @author lyz .
 */
public enum BaseResultEnum {

    SUCCESS(0, "ok", "成功"),
    PARAMETER_ERROR(1001, "parameter error", "参数错误"),
    OPERATE_FAIL(1002, "operate fail", "操作失败"),
    STATUS_ERROR(1003, "status error", "状态错误"),
    ASSIGNEE_ERROR(1004, "assignee error", "非当前审批人或当前节点已处理"),
    DUPLICATE_KEY(1005, "duplicate key", "key重复"),
    DUPLICATE_NAME(1006, "duplicate name", "name重复"),
    SERVER_ERROR(-1, "server error", "服务端错误");

    private int status;
    private String msg;
    private String detail;

    BaseResultEnum(int status, String msg, String detail) {
        this.status = status;
        this.msg = msg;
        this.detail = detail;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static Integer getState(int index) {
        for (BaseResultEnum c : BaseResultEnum.values()) {
            if (c.getStatus() == index) {
                return c.getStatus();
            }
        }
        return null;
    }

    public static String getMsg(int index) {
        for (BaseResultEnum c : BaseResultEnum.values()) {
            if (c.getStatus() == index) {
                return c.getMsg();
            }
        }
        return null;
    }
}
