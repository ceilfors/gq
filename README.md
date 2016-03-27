# gq

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/ceilfors/gq/blob/master/LICENSE)
[![Linux Build Status](https://img.shields.io/circleci/project/ceilfors/gq/master.svg?label=Linux Build)](https://circleci.com/gh/ceilfors/gq)
[![Windows Build Status](https://img.shields.io/appveyor/ci/ceilfors/gq/master.svg?label=Windows Build)](https://ci.appveyor.com/project/ceilfors/gq/branch/master)
[![Groovy 2.4.5](https://img.shields.io/badge/groovy-2.4.5-red.svg)](http://www.groovy-lang.org/)
[![Java 1.7.0_79](https://img.shields.io/badge/java-1.7.0__79-red.svg)](https://java.com)

Quick and dirty debugging output for Groovy.

# Quick Start

Source code
```groovy
@Grab(group='com.ceilfors.groovy', module='gq', version='0.1.0') // 1. Get the dependency!
import gq.Gq as q // 2. Import the class

def who() { "world" }
def greet() { "hello" }

@q // 3. Annotate a method to get trace of method calls
def welcome(arg) {
    return "${q(greet())} $arg !!" // 4. Use q() to capture the expression in the parentheses
}

// 5. Use q | to capture all expressions on the right of the operator
// 6. Use q / to capture a single expression
q | welcome(q / who())
```

Output
![gq output](docs/quick-start-output-75.png?raw=true "gq output")

