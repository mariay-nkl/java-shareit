package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private long idCounter = 1;

    public User save(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email не может быть пустым");
        }

        for (User existingUser : users.values()) {
            if (existingUser.getEmail().equals(user.getEmail()) &&
                    !existingUser.getId().equals(user.getId())) {
                throw new ConflictException("Пользователь с таким email уже существует");
            }
        }

        if (user.getId() == null) {
            user.setId(idCounter++);
        }
        users.put(user.getId(), user);
        return user;
    }

    public User findById(Long id) {
        User user = users.get(id);
        if (user == null) {
            throw new NotFoundException("Пользователь не найден");
        }
        return user;
    }

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public User update(User user) {
        User existingUser = findById(user.getId());

        if (user.getName() != null) {
            existingUser.setName(user.getName());
        }

        if (user.getEmail() != null) {
            for (User u : users.values()) {
                if (u.getEmail().equals(user.getEmail()) &&
                        !u.getId().equals(user.getId())) {
                    throw new ConflictException("Пользователь с таким email уже существует");
                }
            }
            existingUser.setEmail(user.getEmail());
        }

        users.put(existingUser.getId(), existingUser);
        return existingUser;
    }

    public void delete(Long id) {
        users.remove(id);
    }
}