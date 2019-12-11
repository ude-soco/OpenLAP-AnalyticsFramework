package com.openlap.AnalyticsMethods.model;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Objects;

/**
 *  Created by Faizan Riaz on 12/06/19.
 */

@Entity
public class AnalyticsMethods{
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Type(type = "objectid")
    String id;

    public String description;

    public String name;

    public String binaries_location;

    public String creator;

    public String filename;

    public String implementing_class;


    /**
     *
     * Getter and Setters for the Upload Method
     */
    public String  getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBinaries_location() {
        return binaries_location;
    }

    public void setBinaries_location(String binaries_location) {
        this.binaries_location = binaries_location;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getImplementing_class() {
        return implementing_class;
    }

    public void setImplementing_class(String implementing_class) {
        this.implementing_class = implementing_class;
    }

    /**
     * Empty constructor.
     */
    public AnalyticsMethods() {
        this.name = null;
        this.creator = "";
        this.description = "";
        this.binaries_location = null;
        this.implementing_class = "";
        this.filename = "";
    }

    /**
     * Standard constructor
     *
     * @param name              Name of the Analytics Method
     * @param creator           Creator of te Analytics Method
     * @param description       Short description of the Analytisc Method
     * @param implementingClass Class that implements the OpenLAP-AnalyticsFramework
     * @param binariesLocation  Path of the server where the JAR files of the Analytics Method are located.
     */
    public AnalyticsMethods(String name, String creator, String description,
                            String implementingClass, String binariesLocation) {
        this.name = name;
        this.creator = creator;
        this.description = description;
        this.implementing_class = implementingClass;
        this.binaries_location = binariesLocation;
    }

    @Override
    public String toString(){
        return "id:"+ this.id + ",description:" + this.description + ",name:" + this.name + ",binaries_location:" + this.binaries_location +
                ",creator:" + this.creator + ",filename:" + this.filename + ",implementing_class:" + this.implementing_class;
    }
    /**
     * Cloning Method for the AnalyticsMethods Metadata.
     *
     * @return An Object with the properties of this AnalyticsMethodMetadata
     * @throws CloneNotSupportedException
     */
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * Use another AnalyticsMethodMetadata to update this object
     *
     * @param updatedMetadata updated metadata about the analytics method
     */
    public void updateWithMetadata(AnalyticsMethods updatedMetadata) {
        this.setCreator(updatedMetadata.getCreator());
        this.setDescription(updatedMetadata.getDescription());
        this.setImplementing_class(updatedMetadata.getImplementing_class());
        this.setFilename(updatedMetadata.getFilename());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AnalyticsMethods)) return false;

        AnalyticsMethods that = (AnalyticsMethods) o;

        if (getId() != that.getId()) return false;
        if (!getName().equals(that.getName())) return false;
        if (!getCreator().equals(that.getCreator())) return false;
        if (!getDescription().equals(that.getDescription())) return false;
        if (!getImplementing_class().equals(that.getImplementing_class())) return false;
        if (!getBinaries_location().equals(that.getBinaries_location())) return false;
        return getFilename().equals(that.getFilename());

    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, name, binaries_location, creator, filename, implementing_class);
    }

}
