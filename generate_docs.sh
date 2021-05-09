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

# iterate over markdown files in _posts directory and insert post metadata
COUNTER=1
for INPUT_FILE in "./docs/_posts/"*."md"; do
  # get first line in file (= title) => remove new lines => remove leading "## " (= start at index 3) => add number
  TITLE=$(head -n 1 "$INPUT_FILE" | tr -d "\r\n")
  TITLE=${TITLE:3}
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