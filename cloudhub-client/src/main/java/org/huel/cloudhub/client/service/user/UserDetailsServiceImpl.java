package org.huel.cloudhub.client.service.user;

import org.huel.cloudhub.client.data.database.repository.UserRepository;
import org.huel.cloudhub.client.data.entity.user.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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
            throw new UsernameNotFoundException("Not found username: " + username);
        }

        //进行角色授权
        user.setRole(userRepository.getRoleById(user.getId()));
        return user;
    }
}
