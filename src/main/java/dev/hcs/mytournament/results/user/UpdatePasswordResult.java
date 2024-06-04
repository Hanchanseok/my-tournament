package dev.hcs.mytournament.results.user;

import dev.hcs.mytournament.results.Result;

public enum UpdatePasswordResult implements Result {
    FAILURE,
    SUCCESS,
    FAILURE_CURRENT_PASSWORD,
    FAILURE_PASSWORD_CHECK,
    FAILURE_PASSWORD_REGEX
}
