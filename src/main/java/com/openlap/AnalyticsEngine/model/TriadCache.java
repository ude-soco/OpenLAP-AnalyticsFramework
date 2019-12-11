package com.openlap.AnalyticsEngine.model;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;

/**
 * Created by Arham Muslim
 * on 03-Nov-16.
 */
@Entity
public class TriadCache {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Type(type = "objectid")
    private String id;

    private String triadId;

    private String userHash;


    private String code;

    public TriadCache() {
        this.triadId = "";
        this.code = "";
    }

    public TriadCache(String triadId, String code) {
        this.triadId = triadId;
        this.userHash = "";
        this.code = code;
    }

    public TriadCache(String triadId, String userHash, String code) {
        this.triadId = triadId;
        this.userHash = userHash;
        this.code = code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTriadId() {
        return triadId;
    }

    public void setTriadId(String triadId) {
        this.triadId = triadId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUserHash() {
        return userHash;
    }

    public void setUserHash(String userHash) {
        this.userHash = userHash;
    }
}