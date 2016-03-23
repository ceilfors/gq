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

/**
 * @author ceilfors
 */
class CompileStaticTest extends BaseSpecification {

    def "GqSupport should be able to be used in conjunction with CompileStatic method"() {
        setup:
        def instance = toInstance(wrapMethodInClass("""
            @groovy.transform.CompileStatic
            public String compileStatic() {
                return gq("static!")
            }
        """))

        when:
        def result = instance.compileStatic()

        then:
        result == "static!"

    }

    def "GqSupport should be able to be used in conjunction with CompileStatic class"() {
        setup:
        def instance = toInstance(insertPackageAndImport("""
            @groovy.transform.CompileStatic
            class Test {
                public String compileStatic() {
                    return gq("static!")
                }
            }
        """))

        when:
        def result = instance.compileStatic()

        then:
        result == "static!"
    }

    def "Gq should be able to be used in conjunction with CompileStatic method"() {
        setup:
        def instance = toInstance(wrapMethodInClass("""
            @gt
            @groovy.transform.CompileStatic
            public String compileStatic() {
                return "static!"
            }
        """))


        when:
        def result = instance.compileStatic()

        then:
        result == "static!"

    }

    def "Gq should be able to be used in conjunction with CompileStatic class"() {
        setup:
        def instance = toInstance(insertPackageAndImport("""
            @groovy.transform.CompileStatic
            class Test {
                @gt
                public String compileStatic() {
                    return "static!"
                }
            }
        """))

        when:
        def result = instance.compileStatic()

        then:
        result == "static!"
    }
}
