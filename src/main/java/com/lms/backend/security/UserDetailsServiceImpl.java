package com.lms.backend.security;


import com.lms.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepostory;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepostory){
        this.userRepostory=userRepostory;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        return userRepostory.findByEmail(email)
                .map(CustomUserDetails::new)
                .orElseThrow(()-> new UsernameNotFoundException("No user found with email: " + email));

    }

}
