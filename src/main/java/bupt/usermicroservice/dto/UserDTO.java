package bupt.usermicroservice.dto;

import javax.validation.constraints.Pattern;

public class UserDTO {
    @Pattern(regexp = "[a-zA-Z0-9]{1,16}", message = "username只能由数字和大小写字母组成")
    private String username;
    @Pattern(regexp = "[a-zA-Z0-9]{8,16}", message = "password只能由数字和大小写字母组成")
    private String passwordToken;
    private boolean manager = false;
    private boolean teacher = false;
    private boolean student = false;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordToken() {
        return passwordToken;
    }

    public void setPasswordToken(String passwordToken) {
        this.passwordToken = passwordToken;
    }

    public boolean isManager() {
        return manager;
    }

    public void setManager(boolean manager) {
        this.manager = manager;
    }

    public boolean isTeacher() {
        return teacher;
    }

    public void setTeacher(boolean teacher) {
        this.teacher = teacher;
    }

    public boolean isStudent() {
        return student;
    }

    public void setStudent(boolean student) {
        this.student = student;
    }
}
