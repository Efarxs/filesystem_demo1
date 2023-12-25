package exam.gcc.business.service;

import exam.gcc.business.repository.UserRepository;
import exam.gcc.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.util.Base64;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(userRepository.findByUsername(username));
    }

    public int updateUser(User user) {
        if(userRepository.findByUsername(user.getUsername())==null){
            return -1;
        }
        return userRepository.updateUserVip(user);
    }

    public int saveUser(User user) {
        if(userRepository.findByUsername(user.getUsername())!=null){
            return -1;
        }
        user.setSalt(generateSalt());
        user.setPassword(encodePassword(user.getPassword(), user.getSalt()));
        return userRepository.createUser(user);
    }

    private String generateSalt() {
        // Generate a random salt for hashing
        return Long.toHexString(Double.doubleToLongBits(Math.random()));
    }

    private String encodePassword(String password, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest((salt + password).getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error encoding password", e);
        }
    }

    public boolean checkPassword(String inputPassword, User user) {
        String encodedInputPassword = encodePassword(inputPassword, user.getSalt());
        return encodedInputPassword.equals(user.getPassword());
    }
}