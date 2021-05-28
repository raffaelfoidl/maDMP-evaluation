package org.example.dcsojson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "dcsojson", mixinStandardHelpOptions = true, version = "dcsojson 1.0",
  parameterListHeading = "Positional Parameters:%n", optionListHeading = "Options:%n",
  showEndOfOptionsDelimiterInUsageHelp = true,
  description = "Transforms JSON maDMPs to DCSO (JSON-LD/Turtle) and vice versa.\n" +
    "The following conversions are valid:\n" +
    "JSON => JSON-LD, JSON => Turtle; JSON-LD => JSON, JSON-LD => Turtle; Turtle => JSON, Turtle => JSON-LD.\n",
  exitCodeListHeading = "Exit Codes:%n",
  exitCodeList = {
    "0:Successful program execution.",
    "1:Execution Exception: An exception occurred while executing the business logic.",
    "2:Invalid Input: An unknown option or invalid parameter was specified.",
    "3:Invalid Input: The options and parameters are syntactically correct, but failed semantic validation."
  })
public class DcsoJson implements Callable<Integer> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DcsoJson.class);
    public static final String ONTOLOGY_DCSO_JSONLD = "/ontology/dcso.jsonld";

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

    private final Map<String, DcsoJsonTransformation<Path, Path>> conversions;

    public DcsoJson() throws IOException {
        IDcsoJsonTransformer transformer = new DcsoJsonTransformer(ONTOLOGY_DCSO_JSONLD);
        conversions = Map.of(
          Representation.JSON.convertTo(Representation.JSON_LD), transformer::convertJsonToJsonLd,
          Representation.JSON.convertTo(Representation.TURTLE), transformer::convertJsonToTurtle,
          Representation.JSON_LD.convertTo(Representation.JSON), transformer::convertJsonLdToJson,
          Representation.JSON_LD.convertTo(Representation.TURTLE), transformer::convertJsonLdToTurtle,
          Representation.TURTLE.convertTo(Representation.JSON), transformer::convertTurtleToJson,
          Representation.TURTLE.convertTo(Representation.JSON_LD), transformer::convertTurtleToJsonLd
        );
    }


    @Override
    public Integer call() throws Exception {
        var validationResult = validateInputs();
        if (validationResult != 0) {
            return validationResult;
        }

        var conversion = inputFormat.convertTo(outputFormat);
        var transformationMethod = conversions.get(conversion);
        transformationMethod.accept(inputFile.toPath().toAbsolutePath(), outputFile.toPath().toAbsolutePath());

        return 0;
    }

    private int validateInputs() {
        if (!inputFile.exists()) {
            return err("Invalid value for parameter 'inputFile': File at \"{}\" does not exist.\n", inputFile.toString());
        }

        try {
            inputFile.toPath().toAbsolutePath();
        } catch (InvalidPathException e) {
            return err("Invalid value for parameter 'inputFile': Path \"{}\" does not represent a valid, accessible path ({}).\n", inputFile.toString(), e.getMessage());
        }

        try {
            outputFile.toPath().toAbsolutePath();
        } catch (InvalidPathException e) {
            return err("Invalid value for parameter 'outputFile': Path \"{}\" does not represent a valid, accessible path ({}).\n", outputFile.toString(), e.getMessage());
        }

        var conversion = inputFormat.convertTo(outputFormat);
        if (!conversions.containsKey(conversion)) {
            return err("Invalid combination of values for options '--input-format', '--output-format': The conversion \"{}\" is not supported.\n", conversion);
        }

        return 0;
    }

    private static int err(String message, Object... args) {
        LOGGER.error(message, args);
        return 3;
    }

    public static void main(String... args) throws IOException {
        var commandLine = new CommandLine(new DcsoJson());
        commandLine.setCaseInsensitiveEnumValuesAllowed(true);

        int exitCode = commandLine.execute(args);
        if (exitCode == 3) {
            LOGGER.error(commandLine.getUsageMessage());
        }

        System.exit(exitCode);
    }
}
