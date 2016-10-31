#!/bin/bash

host=134.213.209.158

scp -r -i ~/.ssh/vl_devbox.pub ../dist/ forge@134.213.209.158:/home/forge/deploy
