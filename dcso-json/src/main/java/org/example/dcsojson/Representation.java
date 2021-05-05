package org.example.dcsojson;

/**
 * Denotes the supported representation of maDMPs.
 */
public enum Representation {
    /**
     * <p>
     * Regular JSON representation according to the RDA DMP Common Standard for maDMPs.
     * </p>
     * <p>
     * See also https://github.com/RDA-DMP-Common/RDA-DMP-Common-Standard/blob/master/examples/JSON/JSON-schema/1.1/maDMP-schema-1.1.json
     * for the corresponding JSON Schema (version 1.1).
     * </p>
     */
    JSON {
        @Override
        public String toString() {
            return "JSON";
        }
    },

    /**
     * <p>
     * JSON-LD serialization of an maDMP represented using the DMP Common Standard Ontology (DCSO).
     * </p>
     * <p>
     * See also https://github.com/RDA-DMP-Common/RDA-DMP-Common-Standard/tree/master/ontologies/core
     * for current Turtle specification of the OWL ontology.
     * </p>
     */
    JSON_LD {
        @Override
        public String toString() {
            return "JSON-LD";
        }
    },

    /**
     * <p>
     * Turtle serialization of an maDMP represented using the DMP Common Standard Ontology (DCSO).
     * </p>
     * <p>
     * See also https://github.com/RDA-DMP-Common/RDA-DMP-Common-Standard/tree/master/ontologies/core
     * for current Turtle specification of the OWL ontology.
     * </p>
     */
    TURTLE {
        @Override
        public String toString() {
            return "Turtle";
        }
    };

    /**
     * Creates a representation of a conversion from the current {@link Representation} instance to <code>target</code>.
     *
     * @param target The conversion target.
     * @return Returns the string representations of the current {@link Representation} instance and <code>target</code>
     * (as determined by {@link Enum#toString()}), separated by " => ".
     */
    public String convertTo(Representation target) {
        return this + " => " + target;
    }
}
