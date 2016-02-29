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
class GqExample {

    @Gq
    int throwException() {
        throw new RuntimeException("Hello!")
    }

    @Gq
    int nestedWithException1() {
        nestedWithException2()
    }

    @Gq
    private int nestedWithException2() {
        throw new RuntimeException("Hello!")
    }

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

    private int nested2() {
        return nested3() + 5
    }

    @Gq
    private int nested3() {
        return 5
    }
}
