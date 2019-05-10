package bupt.usermicroservice.dto;

import javax.validation.constraints.Pattern;

public class LoginDTO {
    @Pattern(regexp = "[a-zA-Z0-9]{1,16}", message = "username只能由数字和大小写字母组成")
    private String name;
    @Pattern(regexp = "[a-zA-Z0-9]{8,16}", message = "password只能由数字和大小写字母组成")
    private String passwordToken;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPasswordToken() {
        return passwordToken;
    }

    public void setPasswordToken(String passwordToken) {
        this.passwordToken = passwordToken;
    }
}
