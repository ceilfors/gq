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

import com.ceilfors.transform.gq.ast.Gq
import static com.ceilfors.transform.gq.GqSupport.gq

/**
 * @author ceilfors
 */
@Gq
String hello() {
    try {
       oops()
    } catch(ignored) {}
    shout("hello ") + gq(scream("world"))
}

@Gq
String shout(String message) {
    return message.toUpperCase()
}

String scream(String message) {
    return gq(message) + "!!!"
}

@Gq
String oops() {
    throw new RuntimeException('Hello')
}

hello()
