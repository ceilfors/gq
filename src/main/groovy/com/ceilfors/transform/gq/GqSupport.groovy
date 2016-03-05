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

package com.ceilfors.transform.gq

/**
 * @author ceilfors
 */
class GqSupport {

    static GqSupport gq = new GqSupport()

    /**
     * Logs expression to gq file.
     *
     * @param values the values which expression to be logged to gq file
     * @return the first value of the parameters to help method chaining
     */
    def <T> T call(T... values) {
        throw new IllegalStateException("Can't be called during runtime!")
    }
}
