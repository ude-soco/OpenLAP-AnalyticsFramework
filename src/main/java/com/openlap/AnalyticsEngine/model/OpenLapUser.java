package com.openlap.AnalyticsEngine.model;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
public class OpenLapUser {

    @Id
    private String email;

    private String password;

    private String confirmpassword;

    private String firstname;

    private String lastname;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER, mappedBy = "openLapUser")
    private Set<Roles> roles = new HashSet<>();


    public OpenLapUser() {
    }

    public OpenLapUser(String email, String password, String confirmpassword, String firstname, String lastname) {
        this.email = email;
        this.password = password;
        this.confirmpassword = confirmpassword;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public OpenLapUser(String email, String password, String confirmpassword, String firstname, String lastname, Set<Roles> roles) {
        this.email = email;
        this.password = password;
        this.confirmpassword = confirmpassword;
        this.firstname = firstname;
        this.lastname = lastname;
        this.roles = roles;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getConfirmpassword() {
        return confirmpassword;
    }

    public void setConfirmpassword(String confirmpassword) {
        this.confirmpassword = confirmpassword;
    }

    public Set<Roles> getRoles() {
        return roles;
    }

    public void setRoles(Set<Roles> roles) {
        this.roles = roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OpenLapUser that = (OpenLapUser) o;
        return  email.equals(that.email) &&
                password.equals(that.password) &&
                confirmpassword.equals(that.confirmpassword) &&
                firstname.equals(that.firstname) &&
                lastname.equals(that.lastname) &&
                roles.equals(that.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, password, confirmpassword, firstname, lastname, roles);
    }

    @Override
    public String toString() {
        return "OpenLapUser{" +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", confirmpassword='" + confirmpassword + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", roles=" + roles +
                '}';
    }
}
