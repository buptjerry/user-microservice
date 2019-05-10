package bupt.usermicroservice.controller;

import bupt.usermicroservice.dto.LoginDTO;
import bupt.usermicroservice.dto.UserDTO;
import bupt.usermicroservice.entity.Group;
import bupt.usermicroservice.entity.User;
import bupt.usermicroservice.repository.GroupRepository;
import bupt.usermicroservice.repository.UserRepository;
import bupt.usermicroservice.util.KeyGenerator;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Key;
import java.util.*;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("/User")
public class UserController {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Value("#{T(java.net.InetAddress).getLocalHost().getHostName()}")
    private String hostName;

    @Value("#{T(java.net.InetAddress).getLocalHost().getHostAddress()}")
    private String hostAddress;

    @PostMapping("/login")
    @ApiOperation("登陆")
    @ApiResponses({@ApiResponse(code = 200, message = "登陆成功"),
            @ApiResponse(code = 400, message = "账号密码错误")})
    private String login(@RequestBody @Validated LoginDTO loginDTO, HttpServletResponse response) {
        User user = userRepository.findByName(loginDTO.getName());
        if (user == null || !user.verifyPassword(loginDTO.getPasswordToken())) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return "账号或密码不正确";
        }
//            System.out.println("Authenticated user: " + sctx.getUserPrincipal().getName());
//            String authenticatedUser = sctx.getUserPrincipal().getName();
//编辑JWT(Json web token)的payload数据
//        {
//          "hostName": "localhost.hostName",
//          "hostAddress": "192.168.0.1",
//          "sub": "2015212101",
//          "iss": "http://localhost:8080/RESTful-User-Authorization/api/user/login",
//          "iat": 1531308470089,
//          "exp": 1531310270089,
//          "manager": false,
//          "teacher": true,
//          "student": false
//        }
        Map<String, Boolean> role = new HashMap<>();
        user.getGroupList().forEach(group -> {
            if (group.getName().equals("manager"))
                role.put("manager", true);
            else if (group.getName().equals("student"))
                role.put("student", true);
            else if (group.getName().equals("teacher"))
                role.put("teacher", true);
        });
        Date date = new Date();
        Key key = new KeyGenerator().generateKey();
        String jwtToken = Jwts.builder()
                .claim("hostName", hostName)
                .claim("hostAddress", hostAddress)
                .setSubject(user.getId() + "")
                .setIssuer(request.getRequestURI())
                .setIssuedAt(date)
                .setExpiration(new Date(date.getTime() + 30 * 60 * 1000))
                .claim("manager", role.get("manager"))
                .claim("teacher", role.get("teacher"))
                .claim("student", role.get("student"))
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();

        System.out.println("#### generating token for a key :" + jwtToken + "---" + key);
        response.setHeader(AUTHORIZATION, jwtToken);
        return jwtToken;
    }


    @PutMapping("/")
    @ApiOperation("增")
    @ApiResponses({@ApiResponse(code = 200, message = "注册成功"),
            @ApiResponse(code = 400, message = "注册失败，用户名已经存在")})
    private String register(@RequestBody UserDTO userDTO, HttpServletResponse response) {
        if (userRepository.existsByName(userDTO.getUsername())) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return "注册失败，用户名已经存在";
        }
        List<Group> groupList = convertToList(userDTO.isManager(), userDTO.isTeacher(), userDTO.isStudent());
        User user = User.createUser(userDTO.getUsername(), userDTO.getPasswordToken(), groupList);
        userRepository.save(user);
        return "注册成功";
    }


    @DeleteMapping("/{username}")
    @ApiOperation("删")
    @ApiResponses({@ApiResponse(code = 200, message = "删除成功"),
            @ApiResponse(code = 400, message = "删除失败,用户名本来就不存在")})
    private String delete(@PathVariable String username, HttpServletResponse response) {
        User user = userRepository.findByName(username);
        if (user == null) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return "删除失败,用户名本来就不存在";
        }
        userRepository.delete(user);
        return "删除成功";
    }


    @PostMapping("/{username}")
    @ApiOperation("改")
    @ApiResponses({@ApiResponse(code = 200, message = "更新成功"),
            @ApiResponse(code = 400, message = "更新失败，不存在该用户名")})
    private String update(@PathVariable String username, @RequestBody @Validated UserDTO userDTO, HttpServletResponse response) {
        User user = userRepository.findByName(username);
        if (user == null) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return "更新失败，不存在该用户名";
        }
        List<Group> groupList = convertToList(userDTO.isManager(), userDTO.isTeacher(), userDTO.isStudent());
        user.setName(userDTO.getUsername());
        user.setPassword(userDTO.getPasswordToken());
        user.setGroupList(groupList);
        userRepository.save(user);
        return "更新成功";
    }


    @GetMapping("/{username}")
    @ApiOperation("查")
    @ApiResponses({@ApiResponse(code = 200, message = "查询成功，返回User的所有数据"),
            @ApiResponse(code = 400, message = "查询失败，没有相关用户")})
    private UserDTO find(@PathVariable String username, HttpServletResponse response) {
        User user = userRepository.findByName(username);
        if (user == null) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return null;
        }
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(user.getName());
        userDTO.setPasswordToken(user.getPassword());
        user.getGroupList().forEach(group -> {
            if (group.getName().equals("manager"))
                userDTO.setManager(true);
            else if (group.getName().equals("student"))
                userDTO.setStudent(true);
            else if (group.getName().equals("teacher"))
                userDTO.setTeacher(true);
        });
        return userDTO;
    }

    @GetMapping("/")
    @ApiOperation("查所有")
    @ApiResponses(@ApiResponse(code = 200, message = "查询成功，返回所有User的所有数据"))
    private List<UserDTO> fingAll() {
        List<UserDTO> userList = new ArrayList<>();
        userRepository.findAll().forEach(user -> {
            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(user.getName());
            userDTO.setPasswordToken(user.getPassword());
            user.getGroupList().forEach(group -> {
                if (group.getName().equals("manager"))
                    userDTO.setManager(true);
                else if (group.getName().equals("student"))
                    userDTO.setStudent(true);
                else if (group.getName().equals("teacher"))
                    userDTO.setTeacher(true);
            });
            userList.add(userDTO);
        });
        return userList;
    }


    private List<Group> convertToList(boolean manager, boolean teacher, boolean student) {
        List<Group> groupList = new ArrayList<>();
        if (manager) {
            Group group = groupRepository.findByName("manager");
            groupList.add(group);
        }
        if (teacher) {
            Group group = groupRepository.findByName("teacher");
            groupList.add(group);
        }
        if (student) {
            Group group = groupRepository.findByName("student");
            groupList.add(group);
        }
        return groupList;
    }


}

