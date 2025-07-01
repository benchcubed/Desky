package dev.benchurchill.desky.commands;

public enum EDeskyCommandResultStatus {
    PROCESSED(true),
    FAILED(false),
    NO_PERMISSION(false),
    PROCESSING(true),
    TIMEOUT(false),
    UNKNOWN(false),
    DELEGATED(true);

    private final boolean didSucceed;

    EDeskyCommandResultStatus(boolean didSucceed) {
        this.didSucceed = didSucceed;
    }

    public boolean failed() {
        return !didSucceed;
    }
}

