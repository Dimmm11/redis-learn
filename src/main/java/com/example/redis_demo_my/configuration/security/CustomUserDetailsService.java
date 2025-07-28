package com.example.redis_demo_my.configuration.security;

import com.example.redis_demo_my.model.entity.RoleEntity;
import com.example.redis_demo_my.model.entity.UserJpaEntity;
import com.example.redis_demo_my.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserJpaRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserJpaEntity userEntity = userRepository.findByName(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return userEntityToUser(userEntity);
    }

    private User userEntityToUser(UserJpaEntity userEntity) {
        return new User(userEntity.getName(), userEntity.getPassword(), authorities(userEntity.getRoles()));
    }

    private Set<SimpleGrantedAuthority> authorities(Collection<RoleEntity> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().toString()))
                .collect(Collectors.toSet());
    }
}
