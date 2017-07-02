package org.victorprocure.graphql.model.starwars

import groovy.transform.CompileStatic
import org.victorprocure.graphql.annotation.SchemaDocumentation

import javax.persistence.Entity

@Entity
@SchemaDocumentation("Represents an electromechanical robot in the Star Wars Universe")
@CompileStatic
class Droid extends Character {

    @SchemaDocumentation("Documents the primary purpose this droid serves")
    String primaryFunction;

}
