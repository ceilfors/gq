package com.ceilfors.transform.gq.ast

/**
 * @author ceilfors
 */
class GqExample {

    @Gq
    int "return 5"() {
        5
    }

    @Gq
    int add(int x, int y) {
        return x + y
    }

    @Gq
    void "return void"() {
    }

    @Gq
    int nested1() {
        return nested2() + 5
    }

    int nested2() {
        return nested3() + 5
    }

    @Gq
    int nested3() {
        return 5
    }
}
