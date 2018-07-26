package com.letv.woodpecker.wpwebapp.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by meijunjie on 2018/7/24.
 */
@Getter
@Setter
public class TableData implements Serializable {

    private static final long serialVersionUID = -7669667810560081499L;
    private Integer total;
    private List<Map<String,String>> rows;
}
