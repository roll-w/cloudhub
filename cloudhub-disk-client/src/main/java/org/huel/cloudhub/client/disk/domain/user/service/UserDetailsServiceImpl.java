package org.huel.cloudhub.client.disk.domain.user.service;

import org.huel.cloudhub.client.disk.domain.user.User;
import org.huel.cloudhub.client.disk.domain.user.UserDetailsService;
import org.huel.cloudhub.client.disk.domain.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author RollW
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.getUserByName(username);
        if (user == null) {
            throw new UsernameNotFoundException("Username " + username + " not exist");
        }
        return user;
    }

    @Override
    public UserDetails loadUserByUserId(long userId) throws UsernameNotFoundException {
        User user = userRepository.getUserById(userId);
        if (user == null) {
            throw new UsernameNotFoundException("User ID " + userId + " not exist");
        }
        return user;
    }
}
