package cn.moon.ql.ui.ql;

/**
 * Class exposing authenticated user details to the UI.
 */
class QLLoginView {
    private String displayName;
    //... other data fields that may be accessible to the UI

    QLLoginView(String displayName) {
        this.displayName = displayName;
    }

    String getDisplayName() {
        return displayName;
    }
}