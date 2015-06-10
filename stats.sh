#!/bin/bash
make
for file in `ls ../models/`; do
  valgrind --tool=massif --stacks=yes -q ./compress-x86 ../models/$file $file.compress $file.decompress
done
