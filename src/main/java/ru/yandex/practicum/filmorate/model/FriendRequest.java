package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class FriendRequest {
    private Long fromUserId;
    private Long toUserId;
    private FriendStatus status;
}
