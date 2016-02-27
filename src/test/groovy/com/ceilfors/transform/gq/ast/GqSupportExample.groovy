package com.ceilfors.transform.gq.ast

import static com.ceilfors.transform.gq.GqSupport.gq

/**
 * @author ceilfors
 */
class GqSupportExample {

    int "3 plus 5"() {
        gq(3 + 5)
    }

    int nested1(int value) {
        gq(nested2(value))
    }

    int nested2(int value) {
        return value
    }
}
