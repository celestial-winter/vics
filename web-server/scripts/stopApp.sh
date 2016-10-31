#!/usr/bin/env bash

if [ -f /etc/init.d/vics ]; then
    service vics stop
fi
