package com.project.unispace.domain.user.service;

import com.project.unispace.domain.user.dto.UserDetailsImpl;
import com.project.unispace.domain.user.entity.User;
import com.project.unispace.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userLoginId) throws UsernameNotFoundException {
        System.out.println("UserDetailsServiceImpl.loadUserByUsername : " + userLoginId);
        User user = userRepository.findByLoginId(userLoginId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        System.out.println("잘 찾았나?");
        return new UserDetailsImpl(user, user.getLoginId(), user.getPassword());
    }
}

//        Optional<User> user = Optional.ofNullable(userRepository.findByLoginId(userLoginId).
//                orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다.")));

//        System.out.println("잘 찾았나?" + user.get().getUsername() + user.get().getUniversity().getName());
//        return new UserDetailsImpl(user.get(), user.get().getLoginId(), user.get().getPassword());
