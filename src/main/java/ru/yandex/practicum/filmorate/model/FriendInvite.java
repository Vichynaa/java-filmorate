package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class FriendInvite {
    private Long userId;
    private FriendStatus status;
}
