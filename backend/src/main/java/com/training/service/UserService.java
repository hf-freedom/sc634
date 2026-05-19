package com.training.service;

import com.training.model.User;
import com.training.storage.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private InMemoryStorage storage;

    public User getUser(Long id) {
        return storage.getUser(id);
    }

    public List<User> getAllUsers() {
        return storage.getAllUsers();
    }

    public User createUser(User user) {
        user.setCreatedAt(LocalDateTime.now());
        return storage.saveUser(user);
    }
}
