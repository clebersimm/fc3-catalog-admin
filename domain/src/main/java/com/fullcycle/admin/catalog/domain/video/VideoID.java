package com.fullcycle.admin.catalog.domain.video;

import com.fullcycle.admin.catalog.domain.Identifier;

import java.util.Objects;
import java.util.UUID;

public class VideoID extends Identifier {

    private final String value;
    private VideoID(final String anId) {
        this.value = Objects.requireNonNull(anId);
    }
    public static VideoID unique(){
        return VideoID.from(UUID.randomUUID());
    }
    public static VideoID from(final String anId) {
        return new VideoID(anId);
    }
    public static VideoID from(final UUID anId){
        return new VideoID(anId.toString().toLowerCase());
    }
    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VideoID that)) return false;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
