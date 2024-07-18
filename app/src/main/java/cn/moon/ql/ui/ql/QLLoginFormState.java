package cn.moon.ql.ui.ql;

import androidx.annotation.Nullable;

/**
 * Data validation state of the login form.
 */
class QLLoginFormState {
    @Nullable
    private Integer urlError;
    @Nullable
    private Integer cidError;
    private Integer cskError;
    private boolean isDataValid;

    QLLoginFormState(@Nullable Integer urlError, @Nullable Integer cidError, @Nullable Integer cskError) {
        this.urlError = urlError;
        this.cidError = cidError;
        this.cskError = cskError;
        this.isDataValid = false;
    }

    QLLoginFormState(boolean isDataValid) {
        this.urlError = null;
        this.cidError = null;
        this.cskError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    Integer getUrlError() {
        return urlError;
    }

    @Nullable
    Integer getCidError() {
        return cidError;
    }

    @Nullable
    public Integer getCskError() {
        return cskError;
    }

    boolean isDataValid() {
        return isDataValid;
    }
}