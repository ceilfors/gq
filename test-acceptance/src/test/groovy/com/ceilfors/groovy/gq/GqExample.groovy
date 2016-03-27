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

import gq.Gq

/**
 * @author ceilfors
 */
class GqExample {

    @Gq
    int throwException() {
        throw new RuntimeException("Hello!")
    }

    @Gq
    int nestedThrowException1() {
        nestedThrowException2()
    }

    @Gq
    private int nestedThrowException2() {
        // some comment
        nestedThrowException3()
    }

    @Gq
    private int nestedThrowException3() {
        // some comment
        { ->
            throw new RuntimeException("Hello!")
        }.call()
        return 5
    }
}
