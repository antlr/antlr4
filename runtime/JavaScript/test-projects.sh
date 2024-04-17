#!/usr/bin/env bash

cd spec/imports/setups/node-esm-ts
npm run test
cd ../node-cjs-ts
npm run test