#!/bin/bash

# an array of all versions of the dcso-json executable jar in the correct directory
CONVERTER_PATTERN=("../app-bin/dcso-json-"*".jar")

# $1: input-format (json|json-ld|turtle)
# $2: input file path
# $3: output file _name_
# returns: command for invoking dcsojson based on input parameters
function getCommand() {
  # if there are multiple converter jars in the bin folder, take the one last returned by "ls" to take highest version
  # (last element of array is denoted by index -1)
  CONVERTER=$(ls "${CONVERTER_PATTERN[-1]}")
  echo "\"$JAVA_CMD\" -jar \"$CONVERTER\" --input-format=$1 --output-format=json-ld \"$2\" \"./4-json-ld-converted/$3.jsonld\""
}

# determines a command to invoke java
# note: dcsojson is written for JDK 11, so you should have at least this version installed
function determineJava() {
  if [ -z "$JAVA_HOME" ]; then
    JAVA_CMD=$(which java)
  else
    JAVA_CMD="$JAVA_HOME/bin/java"
  fi

  if [ ! -x "$JAVA_CMD" ]; then
    echo "The JAVA_HOME environment variable is not defined correctly." >&2
    echo "This environment variable is needed to run this program." >&2
    echo "Note: JAVA_HOME should point to a JDK, not a JRE." >&2
    exit 1
  fi
}

# $1: file extension to search directory for (excluding ".")
# $2: input format for dcsojson
function convert() {
  for INPUT_FILE in "./3-normalized/"*."$1"; do
    TARGET_JSONLD="$(basename --suffix=".$1" "$INPUT_FILE")"
    CMD=$(getCommand "$2" "$INPUT_FILE" "$TARGET_JSONLD")

    echo "* Converting \"$INPUT_FILE\" to JSON-LD \"$TARGET_JSONLD\""
    echo "   $CMD"

    eval "$CMD"
  done
}

# check if converter exists (${CONVERTER_PATTERN[@]} represents all elements of the array specified returned by ls
# with the globbed pattern defined by CONVERTER_PATTERN)
# (ls returns non-zero if the file does not exist)
if ls "${CONVERTER_PATTERN[@]}" 1>/dev/null 2>&1; then
  determineJava

  echo "#### maDMP Conversion from normalized JSON/Turtle to JSON-LD ####"
  echo "Using \"$JAVA_CMD\" as Java executable."
  echo ""

  echo "#### JSON => JSON-LD ####"
  convert "json" "json"
  echo ""

  echo "#### Turtle => JSON-LD ####"
  convert "ttl" "turtle"
  echo ""

  echo "Successfully converted maDMPs to JSON-LD."
else
  echo "The converter \"dcsojson\" does not seem to have been built yet."
  echo "Please change into the \"../dcso-json\" directory and execute the maven install using \"mvn clean install\"."
  echo "The executable jar will then be placed into the \"../app-bin\" directory."
  echo "Afterwards, you may execute this script again."
  exit 1
fi
