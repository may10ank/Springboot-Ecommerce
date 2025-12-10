package com.example.EcommerceWeb.Service;

import com.example.EcommerceWeb.DTO.PasswordChangeDto;
import com.example.EcommerceWeb.DTO.UserDTO;
import com.example.EcommerceWeb.Repository.UserRepository;
import com.example.EcommerceWeb.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserDTO getUserProfile(String email){
        User user=userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User not found"));
        return new UserDTO(user);
    }

    public UserDTO updateProfile(String email,UserDTO update) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setName(update.getName());
        user.setPhoneNumber(update.getPhoneNumber());
        user.setEmail(update.getEmail());
        user.setAddress(update.getAddress());
        User saved = userRepository.save(user);
        return new UserDTO(user);
    }

    public void deleteAccount(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }

    public void changePassword(String email,PasswordChangeDto passwordChangeDto){
        User user=userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User not found"));
        if(!passwordEncoder.matches(passwordChangeDto.getOldPassword(),user.getPassword())){
            throw new RuntimeException("Old password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(passwordChangeDto.getNewPassword()));
        userRepository.save(user);
    }
}
