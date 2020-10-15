package pwd.allen.entity;


import lombok.Data;

import java.io.Serializable;

@Data
public class SYSUsers implements Serializable {

    private static final long serialVersionUID = -1806263268350977356L;

    private String id;
    private String userName;
    private String account;
    private String password;
    private String pinyin;
    private String gender;
    private String email;
    private String mobile;
    private String cardNum;
    private String orgName;
    private String orgId;
    private String orgPath;
    private String orgType;
    private String orgPathName;
    private String roleId;
    private String roleName;

    /**
     * 角色编码
     */
    private String roleCode;

}
