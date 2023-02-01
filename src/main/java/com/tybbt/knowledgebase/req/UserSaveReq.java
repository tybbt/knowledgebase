package com.tybbt.knowledgebase.req;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class UserSaveReq {

    private Long id;
    @NotNull(message = "[登录名不能为空]")
    private String loginName;
    @NotNull(message = "[昵称不能为空]")
    private String name;
    @NotNull(message = "[密码不能为空]")
    @Pattern(regexp = "^(?![0-9]+$)[0-9A-Za-z]{6,32}$", message = "[密码]至少包含数字和英文，长度6-32")
    private String password;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", loginName=").append(loginName);
        sb.append(", name=").append(name);
        sb.append(", password=").append(password);
        sb.append("]");
        return sb.toString();
    }
}