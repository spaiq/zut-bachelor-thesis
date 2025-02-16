package org.example.docmeet.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


//@Service
public class UserService {

//    private final UserRepository userRepository;
//
//    public UserService(@Autowired UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    public Flux<User> getAllUsers() {
//        return userRepository.findAll();
//    }
//
//    public Mono<User> getUserById(Integer id) {
//        return userRepository.findById(id);
//    }
//
//    public Mono<User> createUser(User user) {
//        return userRepository.save(user);
//    }

//    public Mono<User> updateUser(Integer id, User userDetails) {
//        return userRepository.findById(id)
//                .flatMap(user -> {
//                    updateUserFields(user, userDetails);
//                    return userRepository.save(user);
//                })
//                .switchIfEmpty(Mono.error(new RuntimeException("User not found")));
//    }
//
//    private void updateUserFields(User user, User userDetails) {
//        if (userDetails.getEmail() != null) user.setEmail(userDetails.getEmail());
//        if (userDetails.getPassword() != null) user.setPassword(userDetails.getPassword());
//        if (userDetails.getSeed() != null) user.setSeed(userDetails.getSeed());
//        if (userDetails.getTotpAlgorithm() != null) user.setTotpAlgorithm(userDetails.getTotpAlgorithm());
//        if (userDetails.getName() != null) user.setName(userDetails.getName());
//        if (userDetails.getSecondName() != null) user.setSecondName(userDetails.getSecondName());
//        if (userDetails.getSurname() != null) user.setSurname(userDetails.getSurname());
//        if (userDetails.getPesel() != null) user.setPesel(userDetails.getPesel());
//        if (userDetails.getPhoneNumber() != null) user.setPhoneNumber(userDetails.getPhoneNumber());
//        if (userDetails.getIsAdmin() != null) user.setIsAdmin(userDetails.getIsAdmin());
//    }

//    public Mono<Void> deleteUser(Integer id) {
//        return userRepository.deleteById(id);
//    }
}
