package org.volunteered.apps.auth.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Entity
@Table(name = "user",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username")
        })
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 5, max = 200)
    @Column(length = 200, unique = true, nullable = false)
    private String username;

    @JsonIgnore
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "password", length = 256, nullable = false)
    private String password;


    @NotNull
    @Column(nullable = false)
    private boolean activated = true;

    public User() {
    }

    public User(@NotNull @Size(min = 5, max = 200) String username, @NotNull @Size(min = 1, max = 256) String password) {
        this.username = username;
        this.password = password;
    }

    public User(Long id, @NotNull @Size(min = 5, max = 200) String username, @NotNull @Size(min = 1, max = 256) String password, @NotNull boolean activated) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.activated = activated;
    }

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "user_authority",
			joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "authority_id"))
	private Set<Authority> authorities = new HashSet<>();

	public Long getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public boolean isActivated() {
		return activated;
	}

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public Set<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<Authority> authorities) {
        this.authorities = authorities;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", activated=" + activated +
                ", authorities=" + authorities +
                '}';
    }
}
