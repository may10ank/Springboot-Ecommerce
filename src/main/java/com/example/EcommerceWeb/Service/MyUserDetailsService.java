package com.example.EcommerceWeb.Service;

import com.example.EcommerceWeb.Repository.BusinessRepository;
import com.example.EcommerceWeb.Repository.UserRepository;
import com.example.EcommerceWeb.model.Business;
import com.example.EcommerceWeb.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService{
    @Autowired
    UserRepository userRepository;
    @Autowired
    BusinessRepository businessRepository;

    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        Optional<User> user=userRepository.findByEmail(email);
        if (user.isPresent()){
            return new org.springframework.security.core.userdetails.User(
                    user.get().getEmail(),user.get().getPassword(),
                    Collections.singleton(new SimpleGrantedAuthority(user.get().getRole()))
            );
        }

        Optional<Business> business=businessRepository.findByEmail(email);
        if(business.isPresent()){
            return new org.springframework.security.core.userdetails.User(
                    business.get().getEmail(),business.get().getPassword(),
                    Collections.singleton(new SimpleGrantedAuthority(business.get().getRole()))
            );
        }
        throw new UsernameNotFoundException("User/Business not found with email: "+email);
    }
}
