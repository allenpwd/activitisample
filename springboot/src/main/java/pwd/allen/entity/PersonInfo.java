package pwd.allen.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 门那粒沙
 * @create 2019-08-17 23:22
 **/
@Data
public class PersonInfo implements Serializable {

    private String id;
    private String name;
    private Integer age;

}
