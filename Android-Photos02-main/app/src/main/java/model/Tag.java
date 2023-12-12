package model;

import java.io.Serializable;
import java.util.Objects;

public class Tag implements Serializable {

    private static final long serialVersionUID = 1L;

    public String key;
    public String value;

    public Tag(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return Objects.equals(key, tag.key) && Objects.equals(value, tag.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }
}
