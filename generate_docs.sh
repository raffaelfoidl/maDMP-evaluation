# $1: file to edit
# $2: text to insert at start of file
function insertTextAtBeginning() {
  sed -i "1s/^/$2/" "$1"
}

DOCS_CONTENT_DIR="./docs/_posts"

rm -rf "$DOCS_CONTENT_DIR"
mkdir "$DOCS_CONTENT_DIR"

TODAY=$(date +%Y-%m-%d)
# split README into multiple files at level 2 headings (prefix "## ")
csplit README.md --silent --elide-empty-files --prefix="$DOCS_CONTENT_DIR/$TODAY-" --suffix-format="%04d.md" "/^## /" "{*}"
rm "$DOCS_CONTENT_DIR/$TODAY-0000.md" # we don't need the first one (it only contains the title heading)

# copy dcso-json tool readme as last item
NUMBER_OF_POSTS=$(ls "$DOCS_CONTENT_DIR" -1 | wc -l)
DCSO_JSON_NUMBER=$((NUMBER_OF_POSTS+1))
DCSO_JSON_FILENAME=$(seq -f "$DOCS_CONTENT_DIR/$TODAY-%04g.md" $DCSO_JSON_NUMBER $DCSO_JSON_NUMBER)

cp "./dcso-json/README.md" "$DCSO_JSON_FILENAME"

# iterate over markdown files in _posts directory and insert post metadata
COUNTER=1
for INPUT_FILE in "./docs/_posts/"*."md"; do
  # prefix defines index at which title for blog post start - we want to strip leading "## " (=> offset 3), except
  # for the case of the dcso-json too, where we want to strip only the leading "# " (=> offset 2)
  case "$COUNTER" in
   "$DCSO_JSON_NUMBER") OFFSET=2 ;;
   *) OFFSET=3 ;;
  esac

  # get first line in file (= title) => remove new lines => remove leading "## " (= start at index 3) => add number
  TITLE=$(head -n 1 "$INPUT_FILE" | tr -d "\r\n")
  TITLE=${TITLE:OFFSET}
  TITLE="$COUNTER $TITLE"

  # remove first line, i.e. the heading, since it will be the title of the post
  # to avoid race conditions, first write truncated file to a temporary file and then overwrite original one
  sed '1d' "$INPUT_FILE" > "$INPUT_FILE.temp"; mv "$INPUT_FILE.temp" "$INPUT_FILE"

  POST_METADATA=\
"---\n\
layout: post\n\
title: \"$TITLE\"\n\
author: \"Raffael Foidl, Lea Salome Brugger\"\n\
---\n"
  insertTextAtBeginning "$INPUT_FILE" "$POST_METADATA"

  ((COUNTER+=1))
done