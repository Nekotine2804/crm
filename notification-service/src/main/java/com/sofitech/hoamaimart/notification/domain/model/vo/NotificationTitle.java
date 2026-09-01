package com.sofitech.hoamaimart.notification.domain.model.vo;

/**
 * Value Object: Notification Title.
 */
public final class NotificationTitle {
    private final String value;

    private NotificationTitle(String value) {
        this.value = value;
    }

    public static NotificationTitle of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Notification title cannot be blank");
        }
        if (value.length() > 100) {
            throw new IllegalArgumentException("Notification title cannot exceed 100 characters");
        }
        return new NotificationTitle(value.trim());
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationTitle that = (NotificationTitle) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
