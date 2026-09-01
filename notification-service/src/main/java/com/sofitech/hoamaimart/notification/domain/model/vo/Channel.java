package com.sofitech.hoamaimart.notification.domain.model.vo;

/**
 * Value Object: Notification Channel.
 */
public final class Channel {
    private final String value;

    private Channel(String value) {
        this.value = value;
    }

    public static Channel SMS() {
        return new Channel("SMS");
    }

    public static Channel EMAIL() {
        return new Channel("EMAIL");
    }

    public static Channel PUSH() {
        return new Channel("PUSH");
    }

    public static Channel of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Channel cannot be blank");
        }
        String normalized = value.toUpperCase().trim();
        if (!normalized.matches("SMS|EMAIL|PUSH")) {
            throw new IllegalArgumentException("Invalid channel: " + value + ". Must be SMS, EMAIL, or PUSH");
        }
        return new Channel(normalized);
    }

    public String value() {
        return value;
    }

    public boolean isSMS() {
        return "SMS".equals(value);
    }

    public boolean isEMAIL() {
        return "EMAIL".equals(value);
    }

    public boolean isPUSH() {
        return "PUSH".equals(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Channel channel = (Channel) o;
        return value.equals(channel.value);
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
