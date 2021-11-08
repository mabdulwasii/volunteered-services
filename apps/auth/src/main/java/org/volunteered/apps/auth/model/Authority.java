package org.volunteered.apps.auth.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.volunteered.apps.auth.model.enumeration.AuthorityType;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "authority")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Authority implements Serializable {

    private static final long serialVersionUID = 1L;

    public Authority() {
    }

    public Authority(int id, AuthorityType name) {
        this.id = id;
        this.name = name;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false, length = 50)
    private AuthorityType name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AuthorityType getName() {
        return name;
    }

    public void setName(AuthorityType name) {
        this.name = name;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Authority{" +
                "name='" + name + '\'' +
                "}";
    }
}
