/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.integtests

import org.gradle.integtests.fixtures.CrossVersionIntegrationSpec
import org.gradle.integtests.fixtures.TargetVersions
import org.gradle.util.GradleVersion

/**
 * Tests that task classes compiled against earlier versions of Gradle are still compatible.
 */
@TargetVersions('1.0+')
class PluginBinaryCompatibilityCrossVersionSpec extends CrossVersionIntegrationSpec {
    def "plugin implemented in Groovy can use types converted from Groovy to Java"() {
        given:
        file("producer/build.gradle") << """
            apply plugin: 'groovy'
            dependencies {
                ${previous.version < GradleVersion.version("1.4-rc-1") ? "groovy" : "compile"} localGroovy()
                compile gradleApi()
            }
        """

        file("producer/src/main/groovy/SomePlugin.groovy") << """
            import org.gradle.api.Plugin
            import org.gradle.api.Project
            import org.gradle.api.plugins.JavaPluginConvention

            class SomePlugin implements Plugin<Project> {
                void apply(Project p) {
                    p.apply plugin: 'java'

                    // Must declare type to trigger issue
                    JavaPluginConvention c = p.convention.plugins.java
                    c.sourceCompatibility = 1.8
                    println c.sourceCompatibility
                    c.manifest { }
                }
            }
            """

        buildFile << """
buildscript {
    dependencies { classpath fileTree(dir: "producer/build/libs", include: '*.jar') }
}

apply plugin: SomePlugin
"""

        expect:
        version previous withTasks 'assemble' inDirectory(file("producer")) run()
        version current withTasks 'tasks' requireGradleDistribution() run()
    }
}
