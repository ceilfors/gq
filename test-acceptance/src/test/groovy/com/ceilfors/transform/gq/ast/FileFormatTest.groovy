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

import com.ceilfors.transform.gq.GqFile
import com.ceilfors.transform.gq.SingletonCodeFlowManager
/**
 * @author ceilfors
 */
class FileFormatTest extends BaseSpecification {

    def "Should add timestamp as a prefix when it is enabled"() {
        setup:
        SingletonCodeFlowManager.INSTANCE.codeFlowListeners = [new GqFile(gqFile, true)]
        def instance = toInstance(insertPackageAndImport("gq(1 + 1)"))

        when:
        instance.main()

        then:
        gqFile.text ==~ /\s?\d\.\ds run.*\n/
    }
}
