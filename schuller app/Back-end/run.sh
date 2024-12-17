#!/bin/bash
npm run start | ts '[%Y-%m-%d %H:%M:%S]' 2>&1 | tee -a ositlog.txt &

