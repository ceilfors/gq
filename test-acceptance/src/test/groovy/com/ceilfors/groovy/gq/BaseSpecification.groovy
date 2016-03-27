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

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
/**
 * @author ceilfors
 */
class BaseSpecification extends Specification {

    @Rule
    TemporaryFolder temporaryFolder

    def setup() {
        File gqDir = temporaryFolder.newFolder()
        SingletonCodeFlowManager.INSTANCE.init(gqDir, false)
    }

    File getGqFile() {
        SingletonCodeFlowManager.INSTANCE.gqFile
    }

    /**
     * Only use this method when it is absolutely necessary. Using this method for test case
     * will drop test readability. Example of good use case: comparing line numbers
     * where it is easier to assert when comparing it against IDE.
     *
     * @param clasz the class to be instantiated with GroovyClassLoader
     * @return the object of the example class
     */
    static <T> T newExample(Class<T> clasz) {
        def file = new File("src/test/groovy/${clasz.package.name.replace('.', '/')}/${clasz.simpleName}.groovy")
        assert file.exists()

        GroovyClassLoader invoker = new GroovyClassLoader()
        def clazz = invoker.parseClass(file)
        return clazz.newInstance() as T
    }

    static toInstance(String text) {
        GroovyClassLoader invoker = new GroovyClassLoader()
        def clazz = invoker.parseClass(text)
        return clazz.newInstance()
    }

    static execute(String text) {
        return toInstance(wrapInMethod(text)).test()
    }

    /**
     * This method allows easier debugging than standalone groovy because standalone groovy
     * will create more expressions that will be visited by AST.
     */
    static wrapInMethod(String text) {
        return wrapMethodInClass(
                """def test() {
                  |  $text
                  |}""".stripMargin())
    }

    static wrapMethodInClass(String text) {
        return insertPackageAndImport(
                """class Test {
                  |  $text
                  |}""".stripMargin())
    }


    static insertPackageAndImport(String text) {
        return """package com.ceilfors.groovy.test
                 |
                 |import gq.Gq as q
                 |
                 |$text""".stripMargin()
    }
}
