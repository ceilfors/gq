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

import com.ceilfors.groovy.gq.ToStringSyntaxConverter
import spock.lang.Specification
/**
 * @author ceilfors
 */
class ToStringSyntaxConverterTest extends Specification {

    def "Should enclose in quotes"() {
        when:
        def convertedExpression = new ToStringSyntaxConverter().convertExpressionValue(expression)

        then:
        convertedExpression == result

        where:
        expression       || result
        "foo${'boo'}bar" || "'fooboobar'"
        "foo"            || "'foo'"
    }

    def "Should escape new lines character"() {
        when:
        def convertedExpression = new ToStringSyntaxConverter().convertExpressionValue(expression)

        then:
        convertedExpression == result

        where:
        expression      || result
        "foo\nbar"      || "'foo\\nbar'"
        "foo\nbar\nboo" || "'foo\\nbar\\nboo'"
    }
}
