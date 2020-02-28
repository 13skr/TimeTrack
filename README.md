![versionjava](https://img.shields.io/badge/jdk-8-brightgreen.svg?logo=java)
![versiongradle](https://img.shields.io/badge/gradle-6.1-brightgreen.svg?logo=gradle)
![version](https://img.shields.io/badge/version-1.0-orange.svg?)

# TimeTrack

TimeTrack is a simple plugin for Intellij IDEA to track the time spent on a specific project.

## Usage

Timer is placed in the right lower corner. Green icon means timer is running, grey - paused. To pause/run the timer just simply press on it. Timer file will be placed in `/.idea` directory inside your project.

## Installation

Create JAR from sources

Open Intellij IDEA

Go to File -> Settings -> Plugins -> Install Plugin from Disk...

Find JAR in the file tree

Done!

## Build

Clone this repository

```bash
git clone https://github.com/13skr/TimeTrack.git
```
Open project directory

```bash
cd TimeTrack
```

Use Gradle to create a JAR from sources

```bash
gradle jar
```
JAR was created in `/build/libs` directory