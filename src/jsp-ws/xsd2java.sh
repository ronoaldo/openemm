#!/bin/bash

# This script is used to create all Java classes and interfaces of the domain
# model defined in the webservice contract of file OpenEmm.xsd via XML schema
# compiler xjc.
#
# You will find these classes in package org.agnitas.emm.springws.jaxb.
#
# Run this script each time you modified file OpenEmm.xsd. Option "-extension"
# allows vendor extensions and relaxes compatibility rules.

xjc -d ../../../src/java -extension ../webapp/OpenEmm.xsd
