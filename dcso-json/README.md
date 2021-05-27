# Transformation between CMP Common Standard Serializations

## Introduction

This little utility converts machine-actionable data management plans (maDMPs) that conform to the JSON schema of
the [RDA DMP Common Standard](https://github.com/RDA-DMP-Common/RDA-DMP-Common-Standard) to equivalent JSON-LD
representations based on the DMP Common Standard
Ontology ([DCSO](https://github.com/RDA-DMP-Common/RDA-DMP-Common-Standard/tree/master/ontologies)).

It supports the following conversions:

* JSON => JSON-LD, JSON => Turtle
* JSON-LD => JSON, JSON-LD => Turtle
* Turtle => JSON, Turtle => JSON-LD

The tool was initially created by [Fajar J. Ekaputra](https://github.com/fekaputra) and is published
at [https://github.com/fekaputra/dcso-json](https://github.com/fekaputra/dcso-json). We added a command line interface
for more transparent usage. Furthermore, we refactored the code in order to achieve better encapsulation of related
pieces of business logic.

**Disclaimer:** We have not been able to successfully run this tool from a Windows machine or from a directory with
spaces in its path (neither the initial tool nor our customization). Since this is not of high importance for our goal
(it is only the first preprocessing step), we did not investigate these issues any further. However, in order to provide
this tool for a wider audience anyway, we have also created a `Dockerfile` such that `dcsojson` can be used in an isolated
container that runs in a supported environment.

## Usage (Local)

You can retrieve the integrated help message by invoking the program with the `--help` argument. It provides extensive
information about the program and execution modalities:

```shell
Usage: dcsojson [-hV] -i=<inputFormat> -o=<outputFormat> [--] <inputFile>
                <outputFile>
Transforms JSON maDMPs to DCSO (JSON-LD/Turtle) and vice versa.
The following conversions are valid:
JSON => JSON-LD, JSON => Turtle; JSON-LD => JSON, JSON-LD => Turtle; Turtle => JSON, Turtle => JSON-LD.

Positional Parameters:
      <inputFile>    The maDMP file from which a converted representation
                       should be created. Required.
      <outputFile>   The file path the transformed input file should be saved
                       to. Required.
Options:
  -h, --help         Show this help message and exit.
  -i, --input-format=<inputFormat>
                     The format of the input file. Required. Valid values
                       (case-insensitive): JSON, JSON_LD, TURTLE.
  -o, --output-format=<outputFormat>
                     The format of the input file. Required. Valid values
                       (case-insensitive): JSON, JSON_LD, TURTLE.
  -V, --version      Print version information and exit.
  --                 This option can be used to separate command-line options
                       from the list of positional parameters.
Exit Codes:
  0   Successful program execution.
  1   Execution Exception:An exception occurred while executing the business
        logic.
  2   Invalid Input: An unknown option or invalid parameter was specified.
  3   Invalid Input: The options and parameters are syntactically correct, but
        failed semantic validation.
```

For instance, in order to convert a plain JSON maDMP `maDmp.json` into a semantically enriched, standalone JSON-LD
representation(the context is directly embedded into the file) `maDmp.jsonld`, run the following command:

```shell
java -jar dcso-json-1.0-jar --input-format=json --output-format=json-ld maDmp.json maDmp.jsonld
```

The logging can be configured in `src/main/resources/simplelogger.properties`.

## Usage (Docker)

Build : `docker build -t dcso_json .`

Run (1): `docker run --name=dcso_json_container --env-file=.env dcso_json`

Run (2): `docker run --name=dcso_json_container --env INPUT_FORMAT=json --env OUTPUT_FORMAT=json-ld --env INPUT_FILE=file.json --env OUTPUT_FILE=file.jsonld dcso_json`

Run (3): `docker run --name=dcso_json_container --env-file=.env --env OUTPUT_FILE=newFile.jsonld dcso_json`
-> `--env` overrides values supplied via `--env-file`

Retrieve: `docker cp dcso_json_container:/dcso-json/output/. ./output`


## Build Lifecycle Customizations

The build is based on `maven`. The following custom behaviours are achieved via build plugins:

* `package`: Package application, place JAR in `lib/` folder...
* `clean`: Remove files from distribution location...
* `install`: Distribute packaged files with dependencies to ...

The files to be deployed are represented by the files produced by the `package` phase.

## License

MIT (see [License](LICENSE))