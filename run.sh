#!/bin/bash
javac Main.java && java Main $@ | sort -rn
