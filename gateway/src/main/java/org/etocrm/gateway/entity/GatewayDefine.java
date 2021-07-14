package org.etocrm.gateway.entity;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.etocrm.dynamicDataSource.util.BasePojo;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @Author chengrong.yang
 * @Date 2020/11/10 9:42
 */
@Data
@JsonInclude(value = Include.NON_NULL)
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class GatewayDefine extends BasePojo implements Serializable {

    private static final long serialVersionUID = -1637736030815034085L;

    private Integer id;

    private String name;

    private String uri;

    private String predicates;

    private String filters;

    public List<PredicateDefinition> getPredicateDefinition() {
        if (this.predicates != null) {
            return JSON.parseArray(this.predicates, PredicateDefinition.class);
        } else {
            return Collections.emptyList();
        }
    }

    public List<FilterDefinition> getFilterDefinition() {
        if (this.filters != null &&!"".equals(this.filters.trim())) {
            return JSON.parseArray(this.filters, FilterDefinition.class);
        } else {
            return Collections.emptyList();
        }
    }
}
