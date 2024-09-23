#!/usr/bin/env bash

cd spec/imports/builds/node-esm-ts
npm run test
cd ../node-cjs-ts
npm run test