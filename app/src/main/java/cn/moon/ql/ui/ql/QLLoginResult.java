package cn.moon.ql.ui.ql;

import androidx.annotation.Nullable;

/**
 * Authentication result : success (user details) or error message.
 */
class QLLoginResult {
    @Nullable
    private QLLoginView success;
    @Nullable
    private Integer error;

    QLLoginResult(@Nullable Integer error) {
        this.error = error;
    }

    QLLoginResult(@Nullable QLLoginView success) {
        this.success = success;
    }

    @Nullable
    QLLoginView getSuccess() {
        return success;
    }

    @Nullable
    Integer getError() {
        return error;
    }
}