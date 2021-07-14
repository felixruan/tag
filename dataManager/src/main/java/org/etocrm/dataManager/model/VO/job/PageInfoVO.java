package org.etocrm.dataManager.model.VO.job;

import java.io.Serializable;

/**
 * @author shengjie.ding
 * @create 2020/9/2 17:26
 */
public class PageInfoVO<T> implements Serializable {
    //当前页
    private long current = 1;
    //每页的数量
    private long pageSize =10;

    public long getPageSize() {
        return pageSize;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }

    public long getCurrent() {
        current=current*pageSize-pageSize;
        return current;
    }

    public void setCurrent(long current) {
        this.current = current;
    }
}
