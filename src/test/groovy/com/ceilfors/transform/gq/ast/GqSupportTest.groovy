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
        gqFile.file.text == ("3 + 5=8\n")
    }

    def "Should write method call expression statement and the evaluated expression"() {
        setup:
        def example = newExample(GqSupportExample)

        when:
        def result = example.nested1(5)

        then:
        result == 5
        gqFile.file.text == ("nested2(value)=5\n")
    }
}
