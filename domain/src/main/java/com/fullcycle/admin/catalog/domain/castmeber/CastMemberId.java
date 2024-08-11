package com.fullcycle.admin.catalog.domain.castmeber;

import com.fullcycle.admin.catalog.domain.Identifier;
import com.fullcycle.admin.catalog.domain.category.CategoryID;

import java.util.Objects;
import java.util.UUID;

public class CastMemberId extends Identifier {

    private final String value;
    private CastMemberId(final String anId) {
        Objects.requireNonNull(anId);
        this.value = anId;
    }
    public static CastMemberId unique(){
        return CastMemberId.from(UUID.randomUUID());
    }
    public static CastMemberId from(final String anId) {
        return new CastMemberId(anId);
    }
    public static CastMemberId from(final UUID anId){
        return new CastMemberId(anId.toString().toLowerCase());
    }
    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CastMemberId that)) return false;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
