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
class GqSupportTest extends BaseSpecification {

    def "Should write variable expression statement and the evaluated expression"() {
        setup:
        def example = newExample(GqSupportExample)

        when:
        def result = example."3 plus 5"()

        then:
        result == 8
        gqFile.text == ("3 plus 5: 3 + 5=8\n")
    }

    def "Should write method call expression statement and the evaluated expression"() {
        setup:
        def example = newExample(GqSupportExample)

        when:
        def result = example.nested1(5)

        then:
        result == 5
        gqFile.text == ("nested1: nested2(value)=5\n")
    }
}
