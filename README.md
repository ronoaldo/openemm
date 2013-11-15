# OpenEMM

This is a personal fork of the OpenEMM project (http://www.openemm.org).
The main goal of this fork is to allow the project to be built from
source, following the best practices from both Maven project layout
standard, and the Debian/RPM packaging specifications.

## Proposals

1. Use Maven as build tool, since most of the source code is Java.
   This will ease the process of building the project from source.
   Also, the maven lifecycle management will help with running unit
   and integration tests, as well as generate Deb and RPM packages
   as a build artifact.

2. A deb/rpm packaging, probably after the build from Maven, to
   avoid hardcoded paths and make the instalation and setup easier.

## Developing with Eclipse

Builds from Maven are available as an experimental feature from the
''maven'' branch (or ''mvn'' bookmark). You can clone the repository,
and import the project into Eclipse by issuing the following commands:


```
#!bash

hg clone https://bitbucket.org/ronoaldo/openemm
cd openemm
hg up -C maven
mvn eclipse:eclipse
```

This will generate Eclipse project files for each project, and configure
the workspace to allow running  openemm and openemm-ws as a Dynamic Web
Project under Tomcat.

If you work with Git, you can also checkout the sources from Github:

```
#!bash

git clone https://github.com/ronoaldo/openemm.git
```

## Running the development server

To make testing and running a local copy of OpenEMM easier, we have setup
H2 database support. This support is still experimental, and is integrated
withing the maven build process. To run the local server you can run the
script under ''openemm/src/main/scripts/devserver''. Hit CTRL+C to stop
the server.

Note: currently only the OpenEMM front-end server is launched from this
script.

## Packaging with Maven

To build a distribution zip, you may need to install some dependencies,
and build with maven:

```
#!bash

sudo apt-get install libxml2-dev libmilter-dev
mvn clean package
```

This will generate the binary zipball at distribution/target. The binary
zipball contains itself the project webapps as war files to be easily
deployed on Tomcat, and a zip file containing the backend code, that you
can extract into /home/openemm as recomended by the upstream developers.

This procedure is still being tested, but as the build with maven reproduces
the same procedures executed by the ant build file, everithing should
work fine.
