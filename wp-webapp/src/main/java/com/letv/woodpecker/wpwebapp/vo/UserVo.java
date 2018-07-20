package com.letv.woodpecker.wpwebapp.vo;

import com.letv.woodpecker.wpwebapp.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 *
 * @author meijunjie
 * @date 2018/7/8
 */

public class UserVo extends User {
    private static final long serialVersionUID = -6087897087676167689L;

    @Getter
    @Setter
    private String roleName;

    /** 名下应用*/
    private Set<String> applications;
}
