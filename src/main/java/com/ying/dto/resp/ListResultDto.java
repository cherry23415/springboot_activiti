package com.ying.dto.resp;

import java.util.List;

/**
 * 列表对象
 * Created by lyz on 2017/6/13.
 */
public class ListResultDto<T> {
    private long count;
    private List<T> datas;
    private int currPage;
    private int pageSize;

    public ListResultDto(long count, List<T> datas, int currPage, int pageSize) {
        this.count = count;
        this.datas = datas;
        this.currPage = currPage;
        this.pageSize = pageSize;
    }

    public ListResultDto() {
    }

    public int getCurrPage() {
        return currPage;
    }

    public void setCurrPage(int currPage) {
        this.currPage = currPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public List<T> getDatas() {
        return datas;
    }

    public void setDatas(List<T> datas) {
        this.datas = datas;
    }
}
