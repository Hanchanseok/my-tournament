package dev.hcs.mytournament.results.user;

import dev.hcs.mytournament.results.Result;

public enum ResetPasswordResult implements Result {
    FAILURE_PASSWORD_REGEX,
    FAILURE_PASSWORD_CHECK,
    FAILURE_EMAIL_AUTH_VERIFIED
}
