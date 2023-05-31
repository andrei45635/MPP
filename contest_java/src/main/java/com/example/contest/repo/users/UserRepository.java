package com.example.contest.repo.users;

import com.example.contest.domain.User;
import com.example.contest.repo.IRepository;

public interface UserRepository extends IRepository<Integer, User> {
    boolean findUser(String username, String password);
}
