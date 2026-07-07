package id.my.hendisantika.compose.service;

import id.my.hendisantika.compose.entity.User;
import id.my.hendisantika.compose.exception.NotFoundException;
import id.my.hendisantika.compose.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-boot-docker-compose-mysql
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Visit: https://s.id/hendisantika
 * Date: 21/05/24
 * Time: 07.08
 * To change this template use File | Settings | File Templates.
 */
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User create(User user) {
        user.setId(null);
        return userRepository.save(user);
    }

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User of id " + userId + " not found."));
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional
    public User update(Long userId, User user) {
        User foundUser = findById(userId);

        foundUser.setFirstName(user.getFirstName());
        foundUser.setLastName(user.getLastName());
        foundUser.setEmail(user.getEmail());
        userRepository.save(foundUser);

        return foundUser;
    }

    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }
}
