#!/bin/bash

set -e

mkdir -p releases/

CWD=$(pwd -P)
            
chmod +x skan-x86_64-apple-darwin/skan
tar cvfz "$CWD/releases/skan-x86_64-apple-darwin.tar.gz" skan-x86_64-apple-darwin/skan

chmod +x skan-x86_64-linux/skan
tar cvfz "$CWD/releases/skan-x86_64-linux.tar.gz" skan-x86_64-linux/skan
