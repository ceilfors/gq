package com.ceilfors.transform.gq.ast

import com.ceilfors.transform.gq.GqUtils
/**
 * @author ceilfors
 */
class GqSupportTest extends BaseSpecification {

    def "Should write expression statement and the evaluated expression"() {
        setup:
        def example = newExample(GqSupportExample)

        when:
        def result = example."3 plus 5"()

        then:
        result == 8
        GqUtils.gqFile.text == ("3 + 5=8\n")
    }
}
