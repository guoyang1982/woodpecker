package com.letv.woodpecker.wpwebapp.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 *
 * @author meijunjie
 * @date 2018/7/24
 */
@Getter
@Setter
@AllArgsConstructor
public class TableHeader implements Serializable {
    private static final long serialVersionUID = 712808972874217143L;
    private String title;
    private Integer width;
    private String align;
    private String field;
}
