package org.victorprocure.graphql.model.starwars

import groovy.transform.CompileStatic
import org.victorprocure.graphql.annotation.SchemaDocumentation

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
@SchemaDocumentation("Database driven enumeration")
@CompileStatic
class CodeList {

    @Id
    @SchemaDocumentation("Primary Key for the Code List Class")
    Long id;

    String type;
    String code;
    Integer sequence;
    boolean active;
    String description;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    CodeList parent;

}
