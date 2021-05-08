package org.example.dcsojson;

import com.apicatalog.jsonld.JsonLd;
import com.apicatalog.jsonld.JsonLdError;
import jakarta.json.*;
import jakarta.json.stream.JsonGenerator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DcsoJsonTransformer implements IDcsoJsonTransformer {
    private static final Logger LOGGER = LoggerFactory.getLogger(DcsoJsonTransformer.class);

    private static final Property HAS_DMP = ResourceFactory.createProperty("https://w3id.org/dcso/ns/core#", "hasDMP");
    private static final Resource CLS_DMP = ResourceFactory.createResource("https://w3id.org/dcso/ns/core#DMP");
    private static final Resource NAMED_INDIVIDUAL = ResourceFactory.createResource(OWL.NS + "NamedIndividual");
    public static final String ONTOLOGY_DCSO_TTL = "/ontology/dcso.ttl";

    private final File jsonLdContext;

    public DcsoJsonTransformer(String jsonLdContextPath) throws IOException {
        this.jsonLdContext = getResourceAsFile(jsonLdContextPath);
    }

    @Override
    public void convertJsonToJsonLd(Path jsonInputFilePath, Path jsonLdOutputFilePath) throws TransformationException {
        convertPlainToSemantic(jsonInputFilePath, jsonLdOutputFilePath, Representation.JSON_LD);
    }

    @Override
    public void convertJsonToTurtle(Path jsonInputFilePath, Path turtleOutputFilePath) throws TransformationException {
        convertPlainToSemantic(jsonInputFilePath, turtleOutputFilePath, Representation.TURTLE);
    }

    @Override
    public void convertJsonLdToJson(Path jsonLdInputFilePath, Path jsonOutputFilePath) throws TransformationException {
        convertSemanticToPlain(jsonLdInputFilePath, jsonOutputFilePath, Representation.JSON_LD);
    }

    @Override
    public void convertTurtleToJson(Path turtleInputFilePath, Path jsonOutputFilePath) throws TransformationException {
        convertSemanticToPlain(turtleInputFilePath, jsonOutputFilePath, Representation.TURTLE);
    }

    @Override
    public void convertTurtleToJsonLd(Path turtleInputFilePath, Path jsonLdOutputFilePath) throws TransformationException {
        convertSemanticToSemantic(turtleInputFilePath, jsonLdOutputFilePath, Representation.TURTLE, Representation.JSON_LD);
    }

    @Override
    public void convertJsonLdToTurtle(Path jsonLdInputFilePath, Path turtleOutputFilePath) throws TransformationException {
        convertSemanticToSemantic(jsonLdInputFilePath, turtleOutputFilePath, Representation.JSON_LD, Representation.TURTLE);
    }

    private void convertPlainToSemantic(Path jsonInput, Path outputFilePath, Representation outputLanguage) throws TransformationException {
        try (var dcsoOutputFile = new FileOutputStream(outputFilePath.toString())) {
            var jenaModel = dmpJsonToJenaModel(jsonInput.toFile(), jsonLdContext, true);
            var semanticOutputLanguage = getSemanticLanguage(outputLanguage);

            RDFDataMgr.write(dcsoOutputFile, jenaModel, semanticOutputLanguage);
        } catch (Exception e) {
            throw new TransformationException("Transformation could not be performed.", e);
        }
    }

    private void convertSemanticToPlain(Path inputFilePath, Path outputFilePath, Representation inputLanguage) throws TransformationException {
        try (var semanticInputFile = new FileInputStream(inputFilePath.toFile())) {
            var semanticInputLanguage = getSemanticLanguage(inputLanguage);
            var jenaModel = ModelFactory.createDefaultModel();
            RDFDataMgr.read(jenaModel, semanticInputFile, semanticInputLanguage);

            var jsonRepresentation = jenaModelToJsonObject(jenaModel, jsonLdContext, true);
            writeJson(jsonRepresentation, outputFilePath.toFile());
        } catch (Exception e) {
            throw new TransformationException("Transformation to " + inputLanguage + " could not be performed.", e);
        }
    }

    private void convertSemanticToSemantic(Path inputFilePath, Path outputFilePath, Representation inputLanguage, Representation outputLanguage) throws TransformationException {
        try (
          var semanticInputFile = new FileInputStream(inputFilePath.toFile());
          var dcsoOutputFile = new FileOutputStream(outputFilePath.toString())
        ) {
            var semanticInputLanguage = getSemanticLanguage(inputLanguage);
            var semanticOutputLanguage = getSemanticLanguage(outputLanguage);
            if (semanticInputLanguage == semanticOutputLanguage) {
                throw new IllegalArgumentException("Input and output language must differ");
            }

            var jenaModel = ModelFactory.createDefaultModel();
            RDFDataMgr.read(jenaModel, semanticInputFile, semanticInputLanguage);
            RDFDataMgr.write(dcsoOutputFile, jenaModel, semanticOutputLanguage);
        } catch (Exception e) {
            throw new TransformationException("Transformation to " + outputLanguage + " could not be performed.", e);
        }
    }

    private Lang getSemanticLanguage(Representation representation) {
        switch (representation) {
            case JSON_LD:
                return Lang.JSONLD;
            case TURTLE:
                return Lang.TURTLE;
            default:
                throw new IllegalArgumentException("Must be '" + Representation.JSON_LD + "' or '" + Representation.TURTLE + "'");
        }
    }

    private File writeJson(JsonValue jsonValue, File outputFile) throws IOException {
        Map<String, Object> properties = new HashMap<>(1);
        properties.put(JsonGenerator.PRETTY_PRINTING, true);
        if (outputFile == null) {
            outputFile = File.createTempFile("temp", ".json");
            outputFile.deleteOnExit();
        }

        JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
        var fileWriter = new FileWriter(outputFile);
        try (var jsonWriter = writerFactory.createWriter(fileWriter)) {
            if (jsonValue.getValueType().equals(JsonValue.ValueType.ARRAY)) {
                jsonWriter.writeArray(jsonValue.asJsonArray());
            } else if (jsonValue.getValueType().equals(JsonValue.ValueType.OBJECT)) {
                jsonWriter.writeObject(jsonValue.asJsonObject());
            }
        }

        fileWriter.flush();
        fileWriter.close();

        return outputFile;
    }

    private Model dmpJsonToJenaModel(File dmpJsonFile, File contextFile, Boolean adjustDmp) throws JsonLdError {
        var dcso = ModelFactory.createDefaultModel();
        RDFDataMgr.read(dcso, getResourceAsStream(ONTOLOGY_DCSO_TTL), Lang.TURTLE);

        var model = ModelFactory.createDefaultModel();
        model.setNsPrefixes(dcso.getNsPrefixMap());
        var jsonArray = JsonLd.expand(dmpJsonFile.toURI()).context(contextFile.getAbsolutePath()).get();
        InputStream is = new ByteArrayInputStream(jsonArray.toString().getBytes());
        RDFDataMgr.read(model, is, Lang.JSONLD);

        // hack to ensure correct DMP class representation
        if (adjustDmp) {
            Statement hasDMPStmt = model.getProperty(null, HAS_DMP);
            model.add((Resource) hasDMPStmt.getObject(), RDF.type, CLS_DMP);
            model.remove(hasDMPStmt);
        }

        return model;
    }

    private JsonObject jenaModelToJsonObject(Model model, File contextFile, boolean adjustDmp) throws IOException, JsonLdError {
        var tempFile = File.createTempFile("temp", ".nq");
        tempFile.deleteOnExit();

        // hack to ensure correct DMP class representation
        if (adjustDmp) {
            Statement hasDMPStmt = model.listStatements(null, RDF.type, CLS_DMP).next();
            model.add(model.createResource(), HAS_DMP, hasDMPStmt.getSubject());
            model.removeAll(null, RDF.type, NAMED_INDIVIDUAL);
        }

        RDFDataMgr.write(new FileOutputStream(tempFile), model, Lang.NQUADS);
        JsonArray expandedJsonLd = JsonLd.fromRdf(tempFile.toURI()).get();
        File tempJson = writeJson(expandedJsonLd, null);

        var framedJsonLd = JsonLd.frame(tempJson.toURI(), contextFile.toURI()).get();
        var graph = framedJsonLd.getJsonArray("@graph");
        Iterator<JsonValue> values = graph.iterator();
        JsonObject dmpJson = null;
        while (dmpJson == null && values.hasNext()) {
            JsonValue value = values.next();
            if (value instanceof JsonObject && value.asJsonObject().containsKey("dmp")) {
                dmpJson = value.asJsonObject();
            }
        }

        if (dmpJson != null) {
            dmpJson = adjustValuesFromRDF(dmpJson);
        }

        return dmpJson;
    }

    private JsonObject adjustValuesFromRDF(JsonObject dmpJson) {
        var jsonString = dmpJson.toString();

        // convert integer/decimal without value and type
        jsonString = jsonString.replaceAll("\\{\\s*\"@value\"\\s*:\\s*\"(\\d+)\"\\s*,\\s*\"@type\"\\s*:\\s*\"xsd:integer\"\\s*}", "$1");
        jsonString = jsonString.replaceAll("\\{\\s*\"@value\"\\s*:\\s*\"(\\d+.\\d+)\"\\s*,\\s*\"@type\"\\s*:\\s*\"xsd:decimal\"\\s*}", "$1");

        // remove all ids
        jsonString = jsonString.replaceAll("\\s*\"@id\"\\s*:\\s*\"[A-Za-z/.\\s0-9:_-]*,?\"\\s*,", "");
        jsonString = jsonString.replaceAll("\\s*\"@id\"\\s*:\\s*\"[A-Za-z/.\\s0-9:_-]*\",?\\s*", "");
        jsonString = jsonString.replaceAll("\\s*\"@id\"\\s*:\\s*\\[(\\s*\"[A-Za-z/.\\s0-9:_-]*\",?)*\\s*]?,?", "");

        // remove all types
        jsonString = jsonString.replaceAll("\\s*\"@type\"\\s*:\\s*\"[A-Za-z/.\\s0-9:_-]*,?\"\\s*,", "");
        jsonString = jsonString.replaceAll("\\s*\"@type\"\\s*:\\s*\"[A-Za-z/.\\s0-9:_-]*\",?\\s*", "");
        jsonString = jsonString.replaceAll("\\s*\"@type\"\\s*:\\s*\\[(\\s*\"[A-Za-z/.\\s0-9:_-]*\",?)*\\s*]?,?", "");

        try (var reader = Json.createReader(new StringReader(jsonString))) {
            return reader.readObject();
        }
    }

    private File getResourceAsFile(String resourcePath) throws IOException {
        InputStream resourceAsStream = getResourceAsStream(resourcePath);
        if (resourceAsStream == null) {
            LOGGER.warn("Could not get InputStream from resource path '{}'", resourcePath);
            return null;
        }

        var extension = "." + FilenameUtils.getExtension(resourcePath);
        var tempFile = File.createTempFile(String.valueOf(resourceAsStream.hashCode()), extension);
        tempFile.deleteOnExit();
        FileUtils.copyInputStreamToFile(resourceAsStream, tempFile);
        return tempFile;
    }

    private InputStream getResourceAsStream(String resourcePath) {
        return getClass().getResourceAsStream(resourcePath);
    }
}
