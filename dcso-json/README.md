# Transformation between CMP Common Standard Serializations

## Introduction

This little utility converts machine-actionable data management plans (maDMPs) that conform to the JSON schema of
the [RDA DMP Common Standard](https://github.com/RDA-DMP-Common/RDA-DMP-Common-Standard) to equivalent JSON-LD
representations based on the DMP Common Standard
Ontology ([DCSO](https://github.com/RDA-DMP-Common/RDA-DMP-Common-Standard/tree/master/ontologies)).

It supports the following conversion directions:

* JSON => JSON-LD/Turtle
* JSON-LD/Turtle => JSON

The tool was initially created by [Fajar J. Ekaputra](https://github.com/fekaputra) and is published
at [https://github.com/fekaputra/dcso-json](https://github.com/fekaputra/dcso-json). We added a command line interface
for more transparent usage. Furthermore, we refactored the code in order to achieve better encapsulation of related
pieces of business logic.

**Disclaimer:** We have not been able to successfully run this tool from a Windows machine or from a directory with
spaces in its path (neither the initial tool nor our customization). Since this is not of high importance for our goal
(it is only the first preprocessing step), we did not investigate these issues any further.

## Usage

You can retrieve the integrated help message by invoking the program with the `--help` argument. It provides extensive
information about the program and execution modalities:

```shell
Usage: dcsojson [-hV] -i=<inputFormat> -o=<outputFormat> [--] <inputFile>
                <outputFile>
Transforms JSON maDMPs to DCSO (JSON-LD/Turtle) and vice versa.
The following conversions are valid:
JSON => JSON-LD, JSON => Turtle, JSON-LD => JSON, Turtle => JSON.

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

## Build Lifecycle Customizations

The build is based on `maven`. The following custom behaviours are achieved via build plugins:

* `package`: Package application, place JAR in `lib/` folder...
* `clean`: Remove files from distribution location...
* `install`: Distribute packaged files with dependencies to ...

The files to be deployed are represented by the files produced by the `package` phase.

## Methodology (to be moved to main readme)

* get maDMPs from [Zenodo](https://zenodo.org/communities/dast-2021)
* preprocessing (uniform indenting + formatting) + ensuring schema conformity
    * <https://www.jsonschemavalidator.net/>
    * 4: removed line breaks in JSON string
    * 6: some closing brackets were missing + the object hierarchy in the `distribution` field was not correct (only a guess as to the author's intentions could be made)

```text
Found 2 error(s)
Message: Required properties are missing from object: license_ref, start_date.
Schema path: https://github.com/RDA-DMP-Common/RDA-DMP-Common-Standard/tree/master/examples/JSON/JSON-schema/1.1#/properties/dmp/properties/dataset/items/properties/distribution/items/properties/license/items/required

Message: Required properties are missing from object: ethical_issues_exist, language, modified.
Schema path: https://github.com/RDA-DMP-Common/RDA-DMP-Common-Standard/tree/master/examples/JSON/JSON-schema/1.1#/properties/dmp/required
```

  * 10: 56 errors (mostly "Expected Array but got Object") **TODO**
    * mostly objects were used instead of arrays with one element
    * string instead of numerical values
    * two of the four data sets had no `dataset_id` field
    * used datetime format for date field
  * 11: 2021-04-12T25:10:16.8 -> 2021-04-12T25:10:16.8Z
  * 2021-04-12T25:10:16.8Z -> 2021-04-12T23:10:16.8Z (a day does not have more than 24 hours)
* normalization (achieve same, alphabetical key sorting using tool)
* convert to JSON-LD
* import into GraphDB
* formulate SPARQL queries, based on (link to evaluation Rubric)
* report

## License

MIT (see [License](LICENSE))