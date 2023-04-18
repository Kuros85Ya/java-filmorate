package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;


public interface UserService {
    void addFriend(User initiator, User acceptor);
    void removeFriend(User initiator, User acceptor);
    List<User> getCommonFriends(User initiator, User acceptor);

    List<User> getFriends(User initiator);
}
