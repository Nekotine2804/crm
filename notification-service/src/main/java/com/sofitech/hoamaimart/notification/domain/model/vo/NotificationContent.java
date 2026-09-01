package com.sofitech.hoamaimart.notification.domain.model.vo;

/**
 * Value Object: Notification Content.
 */
public final class NotificationContent {
    private final String value;

    private NotificationContent(String value) {
        this.value = value;
    }

    public static NotificationContent of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Notification content cannot be blank");
        }
        if (value.length() > 500) {
            throw new IllegalArgumentException("Notification content cannot exceed 500 characters");
        }
        return new NotificationContent(value.trim());
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationContent that = (NotificationContent) o;
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
