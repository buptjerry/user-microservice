package bupt.usermicroservice;

import bupt.usermicroservice.entity.Group;
import bupt.usermicroservice.entity.User;
import bupt.usermicroservice.repository.GroupRepository;
import bupt.usermicroservice.repository.UserRepository;
import bupt.usermicroservice.util.SHA256;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;

@SpringBootApplication
public class UsermicroserviceApplication {


    @Bean
    @Profile("reInitData")
    CommandLineRunner init(UserRepository userRepository, GroupRepository groupRepository) {
        String password = "123456789";
        return (args) -> {
            User user1 = User.createUser("Uzi", password, new ArrayList<>());
            User user2 = User.createUser("TheShy", password, new ArrayList<>());
            User user3 = User.createUser("Doinb", password, new ArrayList<>());
            User user4 = User.createUser("Facker", password, new ArrayList<>());
            User user5 = User.createUser("Rookie", password, new ArrayList<>());
            userRepository.save(user1);
            userRepository.save(user2);
            userRepository.save(user3);
            userRepository.save(user4);
            userRepository.save(user5);
            userRepository.flush();
            Group group1 = new Group("manager", "管理员", new ArrayList<>());
            Group group2 = new Group("teacher", "教师", new ArrayList<>());
            Group group3 = new Group("student", "学生", new ArrayList<>());
            groupRepository.save(group1);
            groupRepository.save(group2);
            groupRepository.save(group3);
            groupRepository.flush();
            group1.getUserList().add(user1);
            group2.getUserList().add(user1);
            group2.getUserList().add(user2);
            group3.getUserList().add(user3);
            group3.getUserList().add(user4);
            group3.getUserList().add(user5);
            groupRepository.save(group1);
            groupRepository.save(group2);
            groupRepository.save(group3);
            groupRepository.flush();
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(UsermicroserviceApplication.class, args);
    }

}
