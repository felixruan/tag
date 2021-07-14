package org.etocrm.dynamicDataSource.util;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @Author chengrong.yang
 * @date 2020/9/1 10:03
 */
@Data
public class BasePage <T> implements Serializable {

    //当前页
    private long current;
    //每页的数量
    private long size;
    //总记录数
    private long total;
    //总页数
    private long pages;
    //结果集
    private List<T> records;

    public BasePage(List<T> list) {
        if (list instanceof IPage) {
            IPage page = (IPage) list;
            this.current = page.getCurrent();
            this.size = page.getSize();

            this.pages = page.getPages();
            this.records = page.getRecords();
            this.total = page.getTotal();
        } else if (list instanceof Collection) {
            this.current = 1;
            this.size = list.size();

            this.pages = 1;
            this.records = list;
            this.total = list.size();
        }
    }

    public BasePage(IPage iPage) {
            this.current = iPage.getCurrent();
            this.size = iPage.getSize();
            this.pages = iPage.getPages();
            this.records = iPage.getRecords();
            this.total = iPage.getTotal();
        }
}
