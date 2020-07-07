package financialadvisor.controller;

import financialadvisor.model.User;
import financialadvisor.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class UserRunner implements CommandLineRunner {

    private UserRepository userRepository;

    public UserRunner(UserRepository userRepository){
        this.userRepository = userRepository;
    }
    @Override
    public void run(String... args) throws Exception {
        User user = new User("master","master","master@gmail.com");
        userRepository.save(user);
        userRepository.findAll().forEach(System.out::println);
    }
}
