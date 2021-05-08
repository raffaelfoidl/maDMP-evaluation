package org.example.dcsojson;

import java.nio.file.Path;

public interface IDcsoJsonTransformer {

    /**
     * Converts a JSON maDMP file to a semantically enriched JSON-LD representation.
     *
     * @param jsonInputFilePath    The path to the JSON maDMP file to be converted. Must exist and point to a schema-conform maDMP.
     * @param jsonLdOutputFilePath The path the transformed JSON-LD maDMP should be saved to. A potentially already existing
     *                             file at this location will be attempted to be overwritten.
     * @throws TransformationException If preconditions are not met or an error occurs during processing.
     */
    void convertJsonToJsonLd(Path jsonInputFilePath, Path jsonLdOutputFilePath) throws TransformationException;

    /**
     * Converts a JSON maDMP file to a semantically enriched Turtle representation.
     *
     * @param jsonInputFilePath    The path to the JSON maDMP file to be converted. Must exist and point to a schema-conform maDMP.
     * @param turtleOutputFilePath The path the transformed Turtle maDMP should be saved to. A potentially already existing
     *                             file at this location will be attempted to be overwritten.
     * @throws TransformationException If preconditions are not met or an error occurs during processing.
     */
    void convertJsonToTurtle(Path jsonInputFilePath, Path turtleOutputFilePath) throws TransformationException;

    /**
     * Converts an JSON-LD-serialized DCSO maDMP to a plain JSON representation.
     *
     * @param jsonLdInputFilePath The path to the JSON-LD maDMP file to be converted. Must exist and point to a valid ontology instance.
     * @param jsonOutputFilePath  The path the transformed JSON maDMP should be saved to. A potentially already existing
     *                            file at this location will be attempted to be overwritten.
     * @throws TransformationException If preconditions are not met or an error occurs during processing.
     */
    void convertJsonLdToJson(Path jsonLdInputFilePath, Path jsonOutputFilePath) throws TransformationException;

    /**
     * Converts an JSON-LD-serialized DCSO maDMP to a a semantically enriched Turtle representation.
     *
     * @param jsonLdInputFilePath  The path to the JSON-LD maDMP file to be converted. Must exist and point to a valid ontology instance.
     * @param turtleOutputFilePath The path the transformed Turtle maDMP should be saved to. A potentially already existing
     *                             file at this location will be attempted to be overwritten.
     * @throws TransformationException If preconditions are not met or an error occurs during processing.
     */
    void convertJsonLdToTurtle(Path jsonLdInputFilePath, Path turtleOutputFilePath) throws TransformationException;

    /**
     * Converts a Turtle-serialized DCSO maDMP to a plain JSON representation.
     *
     * @param turtleInputFilePath The path to the Turtle maDMP file to be converted. Must exist and point to a valid ontology instance.
     * @param jsonOutputFilePath  The path the transformed JSON maDMP should be saved to. A potentially already existing
     *                            file at this location will be attempted to be overwritten.
     * @throws TransformationException If preconditions are not met or an error occurs during processing.
     */
    void convertTurtleToJson(Path turtleInputFilePath, Path jsonOutputFilePath) throws TransformationException;

    /**
     * Converts a Turtle-serialized DCSO maDMP to a plain JSON representation.
     *
     * @param turtleInputFilePath  The path to the Turtle maDMP file to be converted. Must exist and point to a valid ontology instance.
     * @param jsonLdOutputFilePath The path the transformed JSON-LD maDMP should be saved to. A potentially already existing
     *                             file at this location will be attempted to be overwritten.
     * @throws TransformationException If preconditions are not met or an error occurs during processing.
     */
    void convertTurtleToJsonLd(Path turtleInputFilePath, Path jsonLdOutputFilePath) throws TransformationException;
}
