package model;

import java.io.Serializable;

public class Tag implements Serializable {

    private static final long serialVersionUID = 1L;

    public String key;

    public String value;

    public Tag(String key, String value){
        this.key = key;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if(o == null || !( o instanceof Tag) ) {
            return false;
        }

        Tag tag = (Tag) o;


        return tag.key.equals(this.key) && tag.value.equals(this.value);
    }

}
