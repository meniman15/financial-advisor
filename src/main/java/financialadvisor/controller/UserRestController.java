package financialadvisor.controller;

import financialadvisor.model.User;
import financialadvisor.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
public class UserRestController {

    @Autowired
    private UserRepository repository;

    private static final int MAX_SESSION_INTERVAL = 20;

    @GetMapping("/users")
    List<User> all(){
        return repository.findAll();
    }

    @PostMapping("/users")
    User newUser(@RequestBody User user){
        return repository.findAll().stream().filter((user1 -> user1.getEmail().equals(user.getEmail()))).findFirst().
                orElseGet( ()-> repository.save(user));
    }

    @PostMapping("/register")
    User registerUser(@RequestBody User user, HttpServletRequest request){
        if (repository.findAll().stream().anyMatch((user1 -> user1.getEmail().equals(user.getEmail()) || user1.getUsername().equals(user.getEmail())))){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"User with this email/username already exist");
        }
        else {
            try{
                User savedUser = repository.save(user);
                request.getSession(true);
                request.getSession().setMaxInactiveInterval(MAX_SESSION_INTERVAL);
                return savedUser;
            }
            catch (Exception e){
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Oh no! Something bad happened, please try again.");
            }

        }
    }

    @PostMapping("/login")
    User login(@RequestBody User user, HttpServletRequest request){
        try{
            User retrievedUser = repository.findAll().stream().
                    filter((user1 -> user1.getUsername().equals(user.getUsername()) && user1.getPassword().equals(user.getPassword())))
                    .findFirst().get();
            //set a new session cookie
            request.getSession(true);
            request.getSession().setMaxInactiveInterval(MAX_SESSION_INTERVAL);
            return retrievedUser;
        }
        catch (NoSuchElementException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Incorrect username or password");
        }
    }

    @GetMapping("/login")
    boolean isLoggedIn(HttpServletRequest request){
        //check if there is a session cookie, and if its valid (not expired)
        return (request.getSession(false) != null);
    }

    @PostMapping("/logout")
    boolean logout(HttpServletRequest request){
        try{
            request.getSession(false).invalidate();
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    @GetMapping("/users/{id}")
    User getOne(@PathVariable Long id){
        return repository.findById(id).orElseThrow(
                ()-> new ResponseStatusException(HttpStatus.BAD_REQUEST,"no user with id: "+ id));
    }

    @PutMapping("/users/{id}")
    User replaceUser(@RequestBody User newUser, @PathVariable Long id){
        return repository.findById(id).map( user -> {
            user.setUsername(newUser.getUsername());
            user.setPassword(newUser.getPassword());
            return repository.save(user);
        }).orElseGet(()-> {
            newUser.setId(id);
            return repository.save(newUser);
        });
    }

    @Bean
    //CORS configuration
    public WebMvcConfigurer configure(){
        return new WebMvcConfigurer(){
            @Override
            public void addCorsMappings(CorsRegistry registry){
                registry.addMapping("/*").allowedOrigins("*")
                        .allowCredentials(true);
            }
        };
    }
}
