# gq

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/ceilfors/gq/blob/master/LICENSE)
[![Download](https://api.bintray.com/packages/ceilfors/maven/gq/images/download.svg) ](https://bintray.com/ceilfors/maven/gq/_latestVersion)
[![Linux Build Status](https://img.shields.io/circleci/project/ceilfors/gq/master.svg?label=Linux Build)](https://circleci.com/gh/ceilfors/gq/tree/master)
[![Windows Build Status](https://img.shields.io/appveyor/ci/ceilfors/gq/master.svg?label=Windows Build)](https://ci.appveyor.com/project/ceilfors/gq/branch/master)
[![Groovy 2.4.5](https://img.shields.io/badge/groovy-2.4.5-red.svg)](http://www.groovy-lang.org/)
[![Java 1.7.0_79](https://img.shields.io/badge/java-1.7.0__79-red.svg)](https://java.com)

Quick and dirty debugging output for Groovy.

# Quick Start

_**Source code**_

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

Run the program and you'll discover the output below. Remember to remove all these debugging notations before you publish your software to production.

_**Output**_

`tail -f /tmp/gq`

```groovy
run: me() + ' !'='world !'
run: me()='world'
run: [greet(), me() + ' !'].join(' ')='hello world !'
greeter(['hello', 'world'])
-> 'hello world !'
```

The colored output can be seen [here](doc/quick-start-output.png)!

# Configuration

These configurations are set via Java System Properties e.g. `groovy -Dgq.tmp=/elsewhere test.groovy`

- gq.tmp

  *Default: /tmp*
  
  Configures where gq should be putting gq files. By default `gq file` will go to `/tmp/gq`. This will go to `C:/tmp` in Windows. You can't change the file name generated.

- gq.color

  *Default: true*
  Set true to print ANSI color to make /tmp/gq console friendly. If you prefer to view `gq file` from text editor, you can of course install a plugin for your favourite text editor to render ANSI escape codes.

# Features

*More detailed behaviors can be found at [gq acceptance tests](test-acceptance/src/test/groovy/com/ceilfors/groovy/gq)*

- [x] Use any import alias you want

  If you can't use `import gq.Gq as q` as you have declared q variable or method already in your project, you can change it to whatever alias you want. Gq will also work without alias e.g. `import gq.Gq`. When using without alias, just replace everything to Gq: @Gq, Gq|, Gq/, Gq().

- [x] Store long values to a file

  When you do `q(new File('long-xml').text)`, then `tail /tmp/gq`, you will find:
    
  ```
  0.2s run: new File('long-xml').text='<xml><root><chi..ren></root></xml>' (file:///tmp/gq0615b779-20dc-4a7e-bcca-8e2b63c7c8a8.txt)
  ```
  
  Notice the printed value above that the xml file content has been shortened with **..** in the middle. You will also see the link to the file where the actual value will be stored i.e. **file:///tmp/gq0615b779-20dc-4a7e-bcca-8e2b63c7c8a8.txt**. A new randomly generated file will be created every time there is a long value found.

- [x] @CompileStatic

  You will be able to use gq in conjunction with @CompileStatic.
  
- [x] /tmp/gq formatting

  ```groovy
  0.0s hello() // timestamp as a prefix on every line. This helps differentiating multiple time of execution.
  0.1s   oops() // Indentation when you have nested @q call
  0.1s   !> RuntimeException('Hello') at visualizeColor.groovy:43 // Handles and prints exception with line number
  ...
  0.1s -> 'HELLO world!!!'
  ```

# Release steps

1. Create a new tag for the next release: `git tag <version>`
2. Check if axion-plugin is successfully picking up the intended version `./gradlew currentVersion`
3. Upload to jcenter: `./gradlew bintrayUpload -PbintrayUser=<> -PbintrayApiKey=<>`

# Credits

Heavily inspired by https://github.com/zestyping/q
