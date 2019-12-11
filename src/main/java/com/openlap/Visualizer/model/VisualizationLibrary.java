package com.openlap.Visualizer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.*;

/**
 * The model representing the Visualization framework (libraries)
 *
 * @author Faizan Riaz
 */
@Entity
@JsonIgnoreProperties
public class VisualizationLibrary {

    //@GeneratedValue(strategy = GenerationType.TABLE,generator="sequence_generator")
    // @GeneratedValue(generator = "uuid")
    // @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Type(type = "objectid")
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    String id;

    //@Column(unique = true, nullable = false)
    String name;

   // @Column(nullable = false)
    String creator;

    String description;

    //@Column(nullable = false)
    String frameworkLocation;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER, mappedBy="visualizationLibrary")
    private List<VisualizationType> visualizationTypes;


    public VisualizationLibrary() {
        visualizationTypes = new ArrayList<>();
    }

    public VisualizationLibrary(String name, String creator, String description, String frameworkLocation) {
        this.name = name;
        this.creator = creator;
        this.description = description;
        this.frameworkLocation = frameworkLocation;
        this.visualizationTypes = new ArrayList<>();
    }

/*
    public VisualizationLibrary(String name, String creator, String description, String frameworkLocation) {
        this.name = name;
        this.creator = creator;
        this.description = description;
        this.frameworkLocation = frameworkLocation;
    }
*/

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

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFrameworkLocation() {
        return frameworkLocation;
    }

    public void setFrameworkLocation(String frameworkLocation) {
        this.frameworkLocation = frameworkLocation;
    }


    public List<VisualizationType> getVisualizationTypes() {
        return visualizationTypes;
    }

    public void setVisualizationTypes(List<VisualizationType> visualizationTypes) {
        this.visualizationTypes = visualizationTypes;
    }

    @Override
    public String toString() {
        return "Vis Framework details :[id:" + id + ",name:" + name + ",creator:" + creator + ",description:" + description + ",location:" + frameworkLocation + "]";
    }

  @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VisualizationLibrary that = (VisualizationLibrary) o;
        return id.equals(that.id) &&
                 name.equals(that.getName()) &&
                creator.equals(that.getCreator()) &&
                description.equals(that.getDescription()) &&
                frameworkLocation.equals(that.getFrameworkLocation()) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, creator, description, frameworkLocation);
    }
}
