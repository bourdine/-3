#!/bin/sh
exec java -Xmx512m -classpath gradle/wrapper/gradle-wrapper.jar org.gradle.wrapper.GradleWrapperMain "$@"
