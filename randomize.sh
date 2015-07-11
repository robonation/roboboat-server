#!/bin/bash

find . -type f | grep -v "DS_Store" | sed -e 's/^\.\///g' | xargs -I xxx bash -c 'mv xxx `echo xxx | sed -e 's/IMG.*//'``uuidgen`.JPG'
