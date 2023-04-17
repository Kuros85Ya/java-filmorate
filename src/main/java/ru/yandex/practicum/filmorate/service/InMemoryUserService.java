package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class InMemoryUserService implements UserService {

    private final UserStorage storage;

    @Autowired
    public InMemoryUserService(@Qualifier("inMemoryUserStorage") UserStorage storage) {
        this.storage = storage;
    }

    public void addFriend(User initiator, User acceptor) {
        Set<Long> initiatorFriends = initiator.getFriends();
        Set<Long> acceptorFriends = acceptor.getFriends();

        initiatorFriends.add(acceptor.getId());
        acceptorFriends.add(initiator.getId());
    }

    public void removeFriend(User initiator, User acceptor) {
        Set<Long> initiatorFriends = initiator.getFriends();
        Set<Long> acceptorFriends = acceptor.getFriends();

        initiatorFriends.remove(acceptor.getId());
        acceptorFriends.remove(acceptor.getId());
    }

    public List<User> getCommonFriends(User initiator, User acceptor) {
        Set<Long> intersection = new HashSet<>(initiator.getFriends());
        Set<Long> acceptorFriendsCopy = new HashSet<>(acceptor.getFriends());
        intersection.retainAll(acceptorFriendsCopy);

        return intersection.stream().map(storage::getUser).collect(Collectors.toList());
    }
}
