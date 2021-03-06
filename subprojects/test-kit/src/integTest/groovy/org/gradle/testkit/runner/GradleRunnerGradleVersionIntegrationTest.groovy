/*
 * Copyright 2015 the original author or authors.
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

package org.gradle.testkit.runner

import org.gradle.api.Action
import org.gradle.testkit.runner.fixtures.NonCrossVersion
import org.gradle.util.DistributionLocator
import org.gradle.util.GradleVersion
import org.gradle.util.Requires
import org.gradle.util.TestPrecondition
import spock.lang.Shared

@NonCrossVersion
@Requires(TestPrecondition.ONLINE)
class GradleRunnerGradleVersionIntegrationTest extends BaseGradleRunnerIntegrationTest {

    public static final String VERSION = "2.10"

    @Shared
    DistributionLocator locator = new DistributionLocator()

    def "execute build with different distribution types"(String version, Action<GradleRunner> configurer) {
        given:
        requireIsolatedTestKitDir = true
        buildFile << """
            task writeVersion << {
                file("version.txt").with {
                    createNewFile()
                    text = gradle.gradleVersion
                }
            }
        """

        when:
        def runner = runner('writeVersion')
        configurer.execute(runner)
        runner.build()

        then:
        file("version.txt").text == version

        cleanup:
        killDaemons(version)

        where:
        version                      | configurer
        buildContext.version.version | { it.withGradleInstallation(buildContext.gradleHomeDir) }
        VERSION                      | { it.withGradleDistribution(locator.getDistributionFor(GradleVersion.version(VERSION))) }
        VERSION                      | { it.withGradleVersion(VERSION) }
    }

    def "distributions are not stored in the test kit dir"() {
        given:
        requireIsolatedTestKitDir = true

        buildFile << '''task v << {
            file("gradleVersion.txt").text = gradle.gradleVersion
            file("gradleHomeDir.txt").text = gradle.gradleHomeDir.canonicalPath
        }'''

        when:
        runner('v')
            .withGradleVersion(VERSION)
            .build()

        then:
        file("gradleVersion.txt").text == VERSION

        and:
        // Note: AbstractGradleRunnerIntegTest configures the test env to use this gradle user home dir
        file("gradleHomeDir.txt").text.startsWith(buildContext.gradleUserHomeDir.absolutePath)

        and:
        testKitDir.eachFileRecurse {
            assert !it.name.contains("gradle-$VERSION-bin.zip")
        }

        cleanup:
        killDaemons(VERSION)
    }

    private void killDaemons(String version) {
        if (!debug) {
            testKitDaemons(GradleVersion.version(version)).killAll()
        }
    }
}
