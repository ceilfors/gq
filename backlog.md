```
This file must be moved to github issue tracker once we hit 0.1.0
```

# Technical debt
- Rename GqTransformation to GqASTTransformation to follow standard
- Remove ast package as it's a useless layer.
- This code in GqFile seems to be a responsibility of someone else: writer.indentLevel = methodCalls.size()

# Bug 

# Feature
- GqSupport - support multiline text e.g. gc(3+\n\n5) -> Trim the new line. See spock's SourceLookup?
- GqSupport.gq to support void return type
- Timestamp on each line for readability
- @Gq Exception - Print source code context e.g. source code snippets and line numbers
- @Gq Exception - Test - nestedException1 catch exception from nestedexception2 and throw again. Indentation must stay the same.
- Support @Gq and GqSupport for groovy scripts e.g. not encapsulated in class
- @Gq(vars=true) to print all variable expression
- Adopt @zefifier groovy-decorator
- q.d