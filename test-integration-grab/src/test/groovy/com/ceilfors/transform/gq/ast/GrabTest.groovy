/*
 * Copyright 2016 Wisen Tanasa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ceilfors.transform.gq.ast

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

/**
 * @author ceilfors
 */
class GrabTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder()

    File gqFile

    @Before
    public void setup() {
        System.setProperty('grape.config', this.class.getResource('m2grapeConfig.xml').toString())

        def directory = temporaryFolder.newFolder().absolutePath
        System.setProperty('GQTMP', directory)
        System.setProperty('grape.root', directory)

        gqFile = new File(directory, 'gq')
    }

    @Test
    public void "Should be able to use gq with Grab"() {
        String artifactVersion = System.getProperty('gqVersion')
        String artifactGroup = System.getProperty('gqGroup')
        String artifactModule = System.getProperty('gqName')

        def test = new GroovyClassLoader().parseClass("""

            import static com.ceilfors.transform.gq.GqSupport.gq

            @Grab(group='${artifactGroup}', module='${artifactModule}', version='${artifactVersion}')
            class GrabTest {

                int highFive() {
                    return gq(2 + 3)
                }
            }
        """).newInstance()

        test.highFive()

        assert gqFile.text ==~ ".*highFive: 2 \\+ 3=5\\s+"
    }
}
