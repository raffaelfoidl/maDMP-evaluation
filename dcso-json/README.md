# Transformation between DMP Common Standard Serializations

## Introduction

This little utility converts machine-actionable data management plans (maDMPs) that conform to the JSON schema of
the [RDA DMP Common Standard](https://github.com/RDA-DMP-Common/RDA-DMP-Common-Standard) to equivalent JSON-LD
representations based on the DMP Common Standard
Ontology ([DCSO](https://github.com/RDA-DMP-Common/RDA-DMP-Common-Standard/tree/master/ontologies)).

It supports the following conversions:

* JSON => JSON-LD, JSON => Turtle
* JSON-LD => JSON, JSON-LD => Turtle
* Turtle => JSON, Turtle => JSON-LD

The tool was initially created by [Fajar J. Ekaputra](https://github.com/fekaputra) and published
at [https://github.com/fekaputra/dcso-json](https://github.com/fekaputra/dcso-json). We added a command line interface
for more transparent usage. Furthermore, we refactored the code in order to achieve better encapsulation of related
pieces of business logic.

**Disclaimer:** We have not been able to successfully run this tool from a Windows machine or from a directory with
spaces in its path (neither the initial tool nor our customization). Since this is not of high importance to
implementing our project (it is only the first preprocessing step), we did not investigate these issues any further.
However, in order to provide this tool to a wider audience anyway, we have also created a `Dockerfile` such
that `dcsojson` can be executed within an isolated container in a supported environment.

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
representation (the context is directly embedded into the file) `maDmp.jsonld`, run the following command:

```shell
java -jar dcso-json-1.0-jar --input-format=json --output-format=json-ld maDmp.json maDmp.jsonld
```

The logging can be configured in [src/main/resources/simplelogger.properties](https://github.com/raffaelfoidl/maDMP-evaluation/blob/main/dcso-json/src/main/resources/simplelogger.properties).

## Usage (Docker)

In order to execute the tool using docker, you need to build the docker image, run it as a container and retrieve the
generated file from the container.

The commands below run the container, the tool and leave behind the result files.

**Note:** Input files to be copied into the container **must** reside within the `input` directory.

### Build Image

In order to build the docker image, execute the following command from the `dcso-json` directory (don't forget the
dot `.` at the end):

```shell
docker build -t dcso_json .
```

This builds a docker image with the name `dcso_json` based on the [Dockerfile](https://github.com/raffaelfoidl/maDMP-evaluation/blob/main/dcso-json/Dockerfile)
contained within this `dcso-json` directory.

**Note:** After new files have been added to the `input` directory, the image has to be re-built using above command. If
nothing else changes (i.e. source code etc.), almost everything will be cached, and the image rebuild will be quite fix.

### Run Container

As described above, acts upon four input parameters: `inputFormat`, `outputFormat`, `inputFile`, `outputFile`. These are
supplied via environment variables. There are essentially three options to define them - via an environment
file (`.env`), vie command-line parameters or as a mixture of both (i.e. specifying variables in an environment file and
overriding specific variables via the command-line invocation).

#### Environment File

The [.env](https://github.com/raffaelfoidl/maDMP-evaluation/blob/main/dcso-json/.env) file contains the environment variables that will be used by the container. The environment file is
specified with the `--env-file` flag.

The following command runs the image `dcso_json` as a container with the name `dcso_json_container`:

```shell
docker run --name=dcso_json_container --env-file=.env dcso_json
```

#### Command-Line Options

The command-line flag `--env` is applied multiple times in to specify environment variables:

```shell
docker run --name=dcso_json_container --env INPUT_FORMAT=json --env OUTPUT_FORMAT=json-ld --env INPUT_FILE=file.json --env OUTPUT_FILE=file.jsonld dcso_json
```

#### Environment File with Overriding Command-Line Options

You can also deposit some never-changing environment variables in an environment file and override specific ones with
the help of the `--env` flag. Note that variables specified by `--env` always take precedence over ones specified in
environment files.

```shell
docker run --name=dcso_json_container --env-file=.env --env OUTPUT_FILE=newFile.jsonld dcso_json
```

### Retrieve Files from Container

After the tool was executed successfully, the result file can be retrieved by copying it from the container onto the
host machine.

```shell
docker cp dcso_json_container:/dcso-json/output/. ./output
```

This copies the contents of the `output` directory in the container (will contain only one file, the output file) into
the `output` directory on the host machine (overwriting possibly pre-existing files).

### Cleanup (optional)

In order to avoid clashes with image and container names, one can simply give different names to the image and
container. Another possibility is to remove the container and afterwards the image.

```shell
docker container rm dcso_json_container
docker image rm dcso_json
```

## Build Lifecycle Customizations

The build is based on `maven`. The following custom behaviours are achieved via build plugins:

<table class="rtable">
    <thead>
      <tr>
        <th>Phase</th>
        <th>Action</th>
      </tr>
    </thead>
    <tbody>
      <tr>
        <td><code>clean</code></td>
        <td>In addition to the removing the files generated during build-time, also deletes the distributed files residing within <code>../app-bin</code> - excluding the <code>.gitkeep</code> file.</td>
      </tr>
      <tr>
        <td><code>package</code></td>
        <td>Packages not only the application itself as JAR file, but also places its (transitive) dependencies into a <code>libs</code> folder and adjusts the class path such that the resulting JAR can be executed using <code>java</code>.</td>
      </tr>
      <tr>
        <td><code>install</code></td>
        <td>Copies the artifacts created during <code>package</code> (i.e. the application including its dependencies) to the <code>../app-bin</code> directory.</td>
      </tr>
    </tbody>
</table>

## Known issues

### Transformations are not bijective

The operations performed by this tool are only surjective, but not injective. Hence, they are not reversible in general
and we must not suppose the relationship `f^(-1)[f(x)] = x` holds for every possible input, where `f^(-1)` denotes the (
assumed, factually non-existent) inverse function of `f(x)`. For example, when `f(x) = "conversion from JSON to JSON-LD`
, then `f^(-1)[x] = "conversion from JSON-LD to JSON`.

More specifically, when converting `JSON -> JSON-LD/Turtle -> JSON`, array elements with arity 1 are converted to
regular objects: `{"a": [{"b": "c"}]} -> {"a": {"b": "c"}}`. This leads to the resulting JSON not being equivalent to
the initial file.

Solving this would probably require some post-processing based on assumptions that can be safely made with knowledge
about the maDMP JSON schema. Whenever an object is encountered during the traversal of the result JSON tree that -
according to the schema - must be an array, it could be transformed into a single-element
array (`{"a": {"b": "c"}} -> {"a": [{"b": "c"}]}`).

A solution without making any assumption based on the publicly available maDMP schema would probably be less trivial and
involve some further exploration.

### Spaces in paths cause problems

As already noted in the Introduction, executing the program from a directory with spaces in its path
causes problems, both on Windows and Unix-like machines.

We suspect the context file should not have spaces in its path because it can cause troubles for the resulting IRI(s).
However, we are not aware whether this is a bug in any of the project's dependencies, a bug in the project's own code or
if the actual reason is something entirely different.

Since this issue was not a major hindrance for the main project of this repository, we did not want to spend too much
time to dive into this issue because it undoubtedly would require some additional investigating.

### Missing conversions

The conversion to JSON-LD format is based on a mapping from JSON terms to ontology concepts defined in
[`src/main/resources/ontology/dcso.jsonld`](https://github.com/raffaelfoidl/maDMP-evaluation/blob/main/dcso-json/src/main/resources/ontology/dcso.jsonld). The `data_preservation` element is unfortunately missing in this mapping.
Therefore, this field is ignored in the conversion.

## License

MIT (see [License](https://github.com/raffaelfoidl/maDMP-evaluation/blob/main/dcso-json/LICENSE))