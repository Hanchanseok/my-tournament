package dev.hcs.mytournament.results.store;

import dev.hcs.mytournament.results.Result;

public enum OrderResult implements Result {
    FAILURE_STOKE_OVER,
    FAILURE_WRONG_PRICE,
    FAILURE_NONE_SALE,
    FAILURE_NONE_LOGIN
}
