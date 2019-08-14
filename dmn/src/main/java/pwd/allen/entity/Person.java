package pwd.allen.entity;

import org.springframework.util.DigestUtils;

import java.util.Date;

/**
 * @author lenovo
 * @create 2019-08-13 9:07
 **/
public class Person {

    private String name;
    private Integer age;
    private Date birth;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Date getBirth() {
        return birth;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }

    public static String getMd5(String str) {
        return DigestUtils.md5DigestAsHex(str.getBytes());
    }

    public static boolean isChild(Person person) {
        if (person.getAge() <= 18) return true;
        else return false;
    }
}
