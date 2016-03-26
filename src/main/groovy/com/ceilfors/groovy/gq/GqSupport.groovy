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

/**
 * @author ceilfors
 */
class GqSupport {

    static GqSupport gq = new GqSupport()

    /**
     * Logs expression to gq file.
     *
     * @param value which expression is to be logged to gq file
     * @return the original value of the expression to help method chaining
     */
    def <T> T call(T value) {
        throw exception("gq($value)")
    }

    /**
     * Logs expression to gq file.
     *
     * @param value which expression is to be logged to gq file
     * @return the original value of the expression to help method chaining
     */
    def <T> T div(T value) {
        throw exception("gq / $value")
    }

    /**
     * Logs expression to gq file.
     *
     * @param value which expression is to be logged to gq file
     * @return the original value of the expression to help method chaining
     */
    def <T> T or(T value) {
        throw exception("gq | $value")
    }

    Throwable exception(String method) {
        new IllegalStateException("GQ BUG! Please log a bug to the developers. The method [$method] should not have been called on runtime!")
    }
}
