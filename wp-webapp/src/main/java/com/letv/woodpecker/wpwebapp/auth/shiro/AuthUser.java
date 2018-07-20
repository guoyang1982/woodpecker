package com.letv.woodpecker.wpwebapp.auth.shiro;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;

/**
 * 自定义Authentication对象
 * @author meijunjie @date 2018/7/5
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthUser implements Serializable {
    private static final long serialVersionUID = 6546861038705490759L;

    private Long id;
    private String loginName;
    private String name;
    private Set<String> urls;
    private Integer role;


    public AuthUser(String loginName) {
        this.loginName = loginName;
    }

    @Override
    public String toString() {
        return loginName;
    }
}
