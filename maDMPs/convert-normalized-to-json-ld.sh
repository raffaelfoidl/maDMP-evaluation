#!/bin/bash

# an array of all versions of the dcso-json executable jar in the correct directory
CONVERTER_PATTERN=("../bin/dcso-json-"*".jar")

function getCommand() {
  # if there are multiple converter jars in the bin folder, take the one last returned by "ls" to take highest version
  # (last element of array is denoted by index -1)
  CONVERTER=$(ls "${CONVERTER_PATTERN[-1]}")
  echo "$JAVA_CMD -jar $CONVERTER --input-format=$1 --output-format=$2 \"$3\" \"$4\""
}

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

# check if converter exists (${CONVERTER_PATTERN[@]} represents all elements of the array specified returned by ls
# with the globbed pattern defined by CONVERTER_PATTERN)
# (ls returns non-zero if the file does not exist)
if ls "${CONVERTER_PATTERN[@]}" 1>/dev/null 2>&1; then
  determineJava

  echo "Starting maDMP Conversion from normalized JSON/Turtle to JSON-LD."
  echo "Using \"$JAVA_CMD\" as Java executable."
  echo ""

  for NORMALIZED_JSON in "./3-normalized/"*."json"; do
    TARGET_JSONLD="./4-json-ld/$(basename --suffix=.json "$NORMALIZED_JSON").jsonld"
    CMD=$(getCommand json json-ld "$NORMALIZED_JSON" "$TARGET_JSONLD")

    echo "* Converting JSON \"$NORMALIZED_JSON\" to JSON-LD \"$TARGET_JSONLD\""
    echo "   $CMD"

    eval "$CMD"
  done
else
  echo "The converter \"dcsojson\" does not seem to have been built yet."
  echo "Please change into the \"../dcso-json\" directory and execute the maven install using \"mvn clean install\"."
  echo "The executable jar will then be placed into the \"../bin\" directory."
  echo "Afterwards, you may execute this script again."
  exit 1
fi

