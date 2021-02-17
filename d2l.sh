#!/bin/bash

###########################
#
# Remember to convert the files to the Unix End of line (\n) instead of (\r\n)
# from windows files - GMAIL do that
#
###########################

cd ../input || exit

for f in *.mbox; do dos2unix -f "$f"; done
