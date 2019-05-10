package bupt.usermicroservice.entity;

import bupt.usermicroservice.util.SHA256;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String password;
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "user_group",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "group_id", referencedColumnName = "id"))
    private List<Group> groupList;

    // jackson 构建 entity 必须调用默认构造函数
    private User() {
    }

    private User(String name, String password, List<Group> groupList) {
        this.groupList = groupList;
        this.name = name;
        this.password = password;
    }

    // 使用类静态函数构建类主要是为了隐藏密码加密的过程
    public static User createUser(String name, String passwordToken, List<Group> groupList) {
        String encryptedPassword = new SHA256().getSHA256StrJava(passwordToken);
        return new User(name, encryptedPassword, groupList);
    }


    public boolean verifyPassword(String passwordToken) {
        String encryptedToken = new SHA256().getSHA256StrJava(passwordToken);
        return encryptedToken.equals(this.password);
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
        this.password = new SHA256().getSHA256StrJava(password);
    }

    public List<Group> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<Group> groupList) {
        this.groupList = groupList;
    }
}
