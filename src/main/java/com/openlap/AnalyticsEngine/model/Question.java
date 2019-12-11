package com.openlap.AnalyticsEngine.model;

import com.openlap.AnalyticsModules.model.Triad;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Faizan Riaz
 * on 12-06-19.
 */
@Entity
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Type(type = "objectid")
    private String id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private int indicatorCount;

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
    @OrderBy(value="id asc")
    private Set<Triad> triads = new HashSet<Triad>();

    public Question() {
        this.name = "";
        this.indicatorCount = 0;
    }

    public Question(String name, int indicatorCount) {
        this.name = name;
        this.indicatorCount = indicatorCount;
    }

    public Question(String name, int indicatorCount, Set<Triad> triads) {
        this.name = name;
        this.indicatorCount = indicatorCount;
        this.triads = triads;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndicatorCount() {
        return indicatorCount;
    }

    public void setIndicatorCount(int indicatorCount) {
        this.indicatorCount = indicatorCount;
    }

    public Set<Triad> getTriads() {
        return triads;
    }

    public void setTriads(Set<Triad> triads) {
        this.triads = triads;
    }
}
