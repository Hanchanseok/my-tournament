package dev.hcs.mytournament.results.user;

import dev.hcs.mytournament.results.Result;

public enum LoginResult implements Result {
    FAILURE_IS_DELETED,
    FAILURE_IS_SUSPENDED,
    FAILURE_PASSWORD,
    FAILURE_EMAIL
}
