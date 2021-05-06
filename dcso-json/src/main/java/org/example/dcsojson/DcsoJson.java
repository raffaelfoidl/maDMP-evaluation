package org.example.dcsojson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "dcsojson", mixinStandardHelpOptions = true, version = "dcsojson 1.0",
  parameterListHeading = "Positional Parameters:%n", optionListHeading = "Options:%n",
  showEndOfOptionsDelimiterInUsageHelp = true,
  description = "Transforms JSON maDMPs to DCSO (JSON-LD/Turtle) and vice versa.\n" +
    "The following conversions are valid:\n" +
    "JSON => JSON-LD, JSON => Turtle, JSON-LD => JSON, Turtle => JSON.\n",
  exitCodeListHeading = "Exit Codes:%n",
  exitCodeList = {
    "0:Successful program execution.",
    "1:Execution Exception: An exception occurred while executing the business logic.",
    "2:Invalid Input: An unknown option or invalid parameter was specified.",
    "3:Invalid Input: The options and parameters are syntactically correct, but failed semantic validation."
  })
public class DcsoJson implements Callable<Integer> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DcsoJson.class);

    @SuppressWarnings("unused")
    @CommandLine.Parameters(index = "0", arity = "1",
      description = "The maDMP file from which a converted representation should be created. Required.")
    private File inputFile;

    @SuppressWarnings("unused")
    @CommandLine.Parameters(index = "1", description = "The file path the transformed input file should be saved to. Required.", arity = "1")
    private File outputFile;

    @SuppressWarnings("unused")
    @CommandLine.Option(names = {"-i", "--input-format"}, arity = "1", required = true,
      description = "The format of the input file. Required. Valid values (case-insensitive): ${COMPLETION-CANDIDATES}.")
    private Representation inputFormat;

    @SuppressWarnings("unused")
    @CommandLine.Option(names = {"-o", "--output-format"}, arity = "1", required = true,
      description = "The format of the input file. Required. Valid values (case-insensitive): ${COMPLETION-CANDIDATES}.")
    private Representation outputFormat;


    private final IDcsoJsonTransformer transformer = new DcsoJsonTransformer("ontology/dcso.jsonld");

    private final Map<String, DcsoJsonTransformation<Path, Path>> VALID_CONVERSIONS = Map.of(
      Representation.JSON.convertTo(Representation.JSON_LD), transformer::convertJsonToJsonLd,
      Representation.JSON.convertTo(Representation.TURTLE), transformer::convertJsonToTurtle,
      Representation.JSON_LD.convertTo(Representation.JSON), transformer::convertJsonLdToJson,
      Representation.JSON_LD.convertTo(Representation.TURTLE), transformer::convertJsonLdToTurtle,
      Representation.TURTLE.convertTo(Representation.JSON), transformer::convertTurtleToJson,
      Representation.TURTLE.convertTo(Representation.JSON_LD), transformer::convertTurtleToJsonLd
    );


    @Override
    public Integer call() throws Exception {
        var validationResult = validateInputs();
        if (validationResult != 0) {
            return validationResult;
        }

        var conversion = inputFormat.convertTo(outputFormat);
        var transformationMethod = VALID_CONVERSIONS.get(conversion);
        transformationMethod.accept(inputFile.toPath().toAbsolutePath(), outputFile.toPath().toAbsolutePath());

        return 0;
    }

    private int validateInputs() {
        if (!inputFile.exists()) {
            return err("Invalid value for parameter 'inputFile': File at \"%s\" does not exist.%n", inputFile.toString());
        }

        try {
            inputFile.toPath().toAbsolutePath();
        } catch (InvalidPathException e) {
            return err("Invalid value for parameter 'inputFile': Path \"%s\" does not represent a valid, accessible path (%s).%n", inputFile.toString(), e.getMessage());
        }

        try {
            outputFile.toPath().toAbsolutePath();
        } catch (InvalidPathException e) {
            return err("Invalid value for parameter 'outputFile': Path \"%s\" does not represent a valid, accessible path (%s).%n", outputFile.toString(), e.getMessage());
        }

        var conversion = inputFormat.convertTo(outputFormat);
        if (!VALID_CONVERSIONS.containsKey(conversion)) {
            return err("Invalid combination of values for options '--input-format', '--output-format': The conversion \"%s\" is not supported.%n", conversion);
        }

        return 0;
    }

    private static int err(String message, Object... args) {
        System.err.printf(message, args);
        return 3;
    }

    /* TODO:
     * Logging
     * move contents of "ontology" folder to src/main/resources and from there such that reading/converting
     *   also works when executing the jar (irrespective of working directory)
     * repository metadata (GitHub)
     * Documentation (code - IDcsoJsonTransformer, readme, root readme, goals, methodology, outputs, file structure, ...)
     *  -> known issue: when converting JSON -> JSON-LD/Turtle -> JSON, array elements with arity 1 have been
     *     converted to regular objects ({"a": [{"b": "c"}]} -> {"a": {"b": "c"}}) (JSON is not equivalent to initial
     *     file anymore); would need post-processing/investigation, but not inherently relevant for this project
     * think of Group ID (not org.example)
     * Maybe some more error handling
     * make remaining non-conformant maDMPs schema-conform
     * complete conversion of maDMPs to JSON-LD
     * decide whether we want to check-in the packaged tool
     */
    public static void main(String... args) {
        var commandLine = new CommandLine(new DcsoJson());
        commandLine.setCaseInsensitiveEnumValuesAllowed(true);

        int exitCode = commandLine.execute(args);
        if (exitCode == 3) {
            commandLine.usage(System.err);
        }

        System.exit(exitCode);
    }
}
