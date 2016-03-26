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

import spock.lang.Requires
/**
 * @author ceilfors
 */
class FileFormatTest extends BaseSpecification {

    def instance

    def setup() {
        SingletonCodeFlowManager.INSTANCE.init(SingletonCodeFlowManager.INSTANCE.gqFile.parentFile, true)
        instance = toInstance(insertPackageAndImport("q(1 + 1)"))
    }

    def "Should add timestamp as a prefix when it is enabled"() {
        when:
        instance.main()

        then:
        gqFile.text ==~ /\s?\d\.\ds run.*\s+/
    }

    @Requires({ os.windows })
    def "Should use Windows new line character"() {
        when:
        instance.main()

        then:
        gqFile.text.endsWith('\r\n')
    }

    @Requires({ os.linux || os.macOs })
    def "Should use Linux new line character"() {
        when:
        instance.main()

        then:
        gqFile.text.endsWith('\n')
    }
}
