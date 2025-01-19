package com.fullcycle.admin.catalog.domain.video;

import com.fullcycle.admin.catalog.domain.ValueObject;

import java.util.Objects;

public class AudioVideoMedia extends ValueObject {
    private final String checksum;
    private final String name;
    private final String rawLocation;
    private final String encodedLocation;
    private final MediaStatus status;

    private AudioVideoMedia(final String checksum,final String name,final String rawLocation,final String encodedLocation,final MediaStatus status) {
        this.checksum = Objects.requireNonNull(checksum);
        this.name = Objects.requireNonNull(name);
        this.rawLocation = Objects.requireNonNull(rawLocation);
        this.encodedLocation = Objects.requireNonNull(encodedLocation);
        this.status = Objects.requireNonNull(status);
    }

    public static AudioVideoMedia with(final String checksum,final String name,final String rawLocation,final String encodedLocation,final MediaStatus status){
        return new AudioVideoMedia(checksum, name, rawLocation, encodedLocation, status);
    }

    public MediaStatus getStatus() {
        return status;
    }

    public String getChecksum() {
        return checksum;
    }

    public String getEncodedLocation() {
        return encodedLocation;
    }

    public String getName() {
        return name;
    }

    public String getRawLocation() {
        return rawLocation;
    }
}
