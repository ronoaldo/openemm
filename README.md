# OpenEMM

This is a fork of the OpenEMM project (http://www.openemm.org).

The main goal of this fork is to allow the project to be built from
source, following the best practices from both Maven project layout
standard, and the Debian/RPM packaging specifications.

## Proposals

1. Use Maven as build tool, since most of the source code is Java.
   This will ease the process of building the project from source.
   Also, the maven lifecycle management will help with running unit
   and integration tests, as well as generate Deb and RPM packages
   as a build artifact.

2. Add deb/rpm packaging, probably after the build from Maven complete,
   to avoid hard-coded paths and make the instalation and setup easier.

## Developing with Eclipse

Builds from Maven are available as an experimental feature from the
''maven'' branch (or ''mvn'' bookmark). You can clone the repository,
and import the project into Eclipse by issuing the following commands:


    hg clone https://bitbucket.org/ronoaldo/openemm
    cd openemm
    hg up -C maven
    mvn eclipse:eclipse

This will generate Eclipse project files for each project, and configure
the workspace to allow running  openemm and openemm-ws as a Dynamic Web
Project under Tomcat.

If you work with Git, you can also checkout the sources from Github:

    git clone https://github.com/ronoaldo/openemm.git

## Running the development server

To make testing and running a local copy of OpenEMM easier, we have setup
some development server support. In order to run the server from your 
maven project, follow those steps:

1. Setup a database locally. You need a MySQL server, with the openemm database
   created. Reffer to the OpenEMM Admin Guide for details on what files you must
   load.
2. Configure openemm/etc/dev.propertes. See openemm/etc/dev.properties.sample
   as a starting point.
3. Run the script openemm/src/main/scripts/devserver to launch the local
   server. Alternatively, you can run it by calling mvn pre-integration-test

To stop the server hit CTRL+C from the shell.

Currently only the OpenEMM front-end server is launched from this
script. You must start the backend manually.

To start the WebServices app, run the following from the project root:

    mvn clean install
    mvn -pl openemm-ws clean tomcat6:run

The default port used by both applications is 9090.

## Dependencies

To build a distribution zip, you may need to install some dependencies,
and build with maven:

    sudo apt-get install libxml2-dev libmilter-dev maven
    mvn clean package

This will generate the binary zipball at distribution/target. The binary
zipball contains itself the project webapps as war files to be easily
deployed on Tomcat, and a zip file containing the backend code, that you
can extract into /home/openemm as recomended by the upstream developers.

This procedure is still being tested, but as the build with maven reproduces
the same procedures executed by the ant build file, everithing should
work fine.
