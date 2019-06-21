package io.grabity.planetwallet.VO;

import java.io.Serializable;

public class Board implements Serializable {

    String id;
    String subject;
    String created_at;

    String type;

    public Board( ) {
    }

    public Board( String type ) {
        this.type = type;
    }

    public String getId( ) {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
    }

    public String getSubject( ) {
        return subject;
    }

    public void setSubject( String subject ) {
        this.subject = subject;
    }

    public String getCreated_at( ) {
        return created_at;
    }

    public void setCreated_at( String created_at ) {
        this.created_at = created_at;
    }

    public String getType( ) {
        return type;
    }

    public void setType( String type ) {
        this.type = type;
    }
}
