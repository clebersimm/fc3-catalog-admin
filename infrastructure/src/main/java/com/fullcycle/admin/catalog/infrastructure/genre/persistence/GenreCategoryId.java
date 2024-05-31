package com.fullcycle.admin.catalog.infrastructure.genre.persistence;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class GenreCategoryId {
    @Column(name = "genre_id", nullable = false)
    private String genreId;
    @Column(name = "category_id", nullable = false)
    private String categoryId;

    public GenreCategoryId() {
    }

    private GenreCategoryId(final String aGenreId, final String aCategoryId) {
        this.genreId = aGenreId;
        this.categoryId = aCategoryId;
    }

    public static GenreCategoryId from(final String aGenreId, final String aCategoryId) {
        return new GenreCategoryId(aGenreId, aCategoryId);
    }

    public String getGenreId() {
        return genreId;
    }

    public void setGenreId(String genreId) {
        this.genreId = genreId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((genreId == null) ? 0 : genreId.hashCode());
        result = prime * result + ((categoryId == null) ? 0 : categoryId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GenreCategoryId other = (GenreCategoryId) obj;
        if (genreId == null) {
            if (other.genreId != null)
                return false;
        } else if (!genreId.equals(other.genreId))
            return false;
        if (categoryId == null) {
            if (other.categoryId != null)
                return false;
        } else if (!categoryId.equals(other.categoryId))
            return false;
        return true;
    }

}
