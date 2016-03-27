# gq

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/ceilfors/gq/blob/master/LICENSE)
[![Linux Build Status](https://img.shields.io/circleci/project/ceilfors/gq/master.svg?label=Linux Build)](https://circleci.com/gh/ceilfors/gq)
[![Windows Build Status](https://img.shields.io/appveyor/ci/ceilfors/gq/master.svg?label=Windows Build)](https://ci.appveyor.com/project/ceilfors/gq/branch/master)
[![Groovy 2.4.5](https://img.shields.io/badge/groovy-2.4.5-red.svg)](http://www.groovy-lang.org/)
[![Java 1.7.0_79](https://img.shields.io/badge/java-1.7.0__79-red.svg)](https://java.com)

Quick and dirty debugging output for Groovy.

# Quick Start

```
@Grab(group='com.ceilfors.groovy', module='gq', version='0.1.0') // 1. Get dependency!
import gq.Gq as q // 2. Import q and get ready
```
```groovy
def me() { 'world' }
def greet() { 'hello' }

// 3. Use q(), q|, q/  to print values without temporary variable. Check the differences from the output below.
println([greet(), q(me() + ' !')].join(' '))
println([greet(), q/me() + ' !'].join(' '))
println(q|[greet(), me() + ' !'].join(' '))

@q // 4. Annotate a method to get trace of method calls
def greeter(args) { args << '!'; args.join(' ') }

println(greeter([greet(), me()]))
```

Output

![gq output](doc/quick-start-output-75.png?raw=true "gq output")

# Configuration via System Properties

- gq.tmp
  Default: /tmp
  Configures where gq should be putting gq files

- gq.color
  Default: true
  Set true to print ANSI color to make /tmp/gq console friendly.

# gq files

- gq
- gq000-111-222.txt

# Credits

Heavily inspired by https://github.com/zestyping/q
