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

package com.ceilfors.groovy.gq

import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

/**
 * This test is required as it is possible for @Grab to fail when there's a wrong dependency being pulled.
 * One of the previous error is caused by org.codehaus.groovy:groovy-all:2.4.5 where the following exception will
 * be thrown<br />
 * <pre>Caused by: java.lang.ClassNotFoundException: # Licensed to the Apache Software Foundation (ASF) under one or more</pre>
 *
 * @author ceilfors
 */
class GrabTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder()

    static String artifactVersion
    static String artifactGroup
    static String artifactModule

    File gqFile

    @BeforeClass
    public static void setupClass() {
        artifactVersion = System.getProperty('gqVersion')
        artifactGroup = System.getProperty('gqGroup')
        artifactModule = System.getProperty('gqName')
    }

    @Before
    public void setup() {
        System.setProperty('grape.config', this.class.getResource('m2grapeConfig.xml').toString())

        def directory = temporaryFolder.newFolder().absolutePath
        System.setProperty('gq.tmp', directory)
        System.setProperty('gq.color', 'false')
        System.setProperty('grape.root', directory)

        gqFile = new File(directory, 'gq')
    }

    @Test
    public void "Should be able to use gq with Grab"() {
        def test = new GroovyClassLoader().parseClass("""

            import gq.Gq as q

            @Grab(group='${artifactGroup}', module='${artifactModule}', version='${artifactVersion}')
            class GrabTest {

                int highFive() {
                    return q(2 + 3)
                }
            }
        """).newInstance()

        test.highFive()

        assert gqFile.text ==~ ".*highFive: 2 \\+ 3=5\\s+"
    }
}
