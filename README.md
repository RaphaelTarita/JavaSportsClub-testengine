# JavaSportsClub-testengine

A test engine for the SportsClub project, which is a task in the SS2021 PR2 course (UNIVIE)

This test engine is intended to be used as a test routine to validate an implementation of the SportsClub project. **It does not contain any of the actual implementations of SportsClub** (for the implementation, see [PR2_sportsclub](https://github.com/RaphaelTarita/PR2_sportsclub)). It is furthermore not guaranteed that this test engine will identify all bugs, nor that it doesn't contain bugs itself. Therefore, if the test engine reports no test failures, this **does not** mean that your implementation is correct, and vice versa, a reported error might hint at a bug in the test engine and **not in your code**.

#### If you have any questions, contact me on discord: Raph0007#3276

## Usage:

### Step 1: Clone

Step 1 is to clone the repository locally. You can do that in many different ways, for example with the IDE of your choice. Alternatively, you can just use `git` in the command line:

```batch
git clone https://github.com/RaphaelTarita/JavaSportsClub-testengine.git
```

Execute this command in the directory where you want the test engine to sit.

### Step 2: Add source files

After you have all the sources of the test engine stored locally, you have to supply them with your own implementation files. Generally, this is as easy as to copy all your 5 needed source files (`Level.java`, `Member,java`, `SportsClub.java`, `Sports.java`, `Trainer.java`) into `.../JavaSportsClub-testengine/src/matrnr/`. **But there is a problem**: Your source files contain a `package` declaration at the top, it looks like this:

```java
package a<matrikelnummer>;
```

Because the root package for every student is different, I cannot provide a solution that automatically works for everyone. **It might be that if you copy the files in using an IDE, an automatic refactoring of these package declarations is performed**. But if that is not the case, you have two options:

1. Rename all your package declarations to `matrnr`. Since it's only 5 files and therefore 5 package declarations, this should be fairly easy
  
2. Refactor the package root of the test engine to `a<matrikelnummer>`. Considering that the test engine has a total of 39 files and import relations between them, I wouldn't recommend to do this without IDE support (IDE refactoring should work fine).
  

After your code resides in `src/matrnr/`, you can continue to write your testing routine

### Step 3: Configure the test engine

The test framework is designed to be _customizable_. This means that you can (and must) set up your own test routine, by building a test engine and registering test suites and isolated tests. The class you're gonna use is called `matrnr.TestEngine`. This is the class that executes all your tests and prints the results. It consists of `TestSuite`s and isolated `Test`s. A `TestSuite` is just a collection of `Test`s. A `Test` is like a unit test, an there are 23 predefined tests, which you will find in `matrnr.tests`. There are also 5 predefined `TestSuite`s, located in the `matrnr.suites.Suites` class.

All the code needed to configure and run the engine should reside in the `main()` method of `matrnr.TestMain`.

To configure and build the `TestEngine`, you will use a builder pattern. You can invoke the builder DSL via `TestEngine.builder()`. The following options are available:

- `overviewOutput(PrintStream)`: Set the `PrintStream` to which the overview output shall be printed. Default: `System.out`.
  
- `fullOutput(Path)`: Defines the file to which the full output shall be written. Passing `null` means that no full output will be written. Default: `null`.
  
- `registerSuite(TestSuite)`: Registers a test suite that shall be tested by the engine. Every call to `registerSuite()` appends to a `Set` of `TestSuite`s. Default: empty set.
  
- `registerSuites(Collection<TestSuite>)`: Registers all test suites in the collection.
  
- `registerTest(Test)`: Register a single, isolated test that shall be tested by the engine. Every call to `registerTest()` appends to a `Set` of `Test`s. Default: empty set.
  
- `registerTests(Collection<Test>)`: Analogous to `registerSuites()`
  
- `registerAllAsIsolatedTests(TestSuite)`: Extracts all the tests of the passed `TestSuite` and registers them all as isolated tests.
  

**You only need to call the config options you wish to change (from the default)**. After configuration is done, use `build()` to build the test engine. You can then invoke `test()` on the engine to actually run it.

Here's an example for configuring a test suite that shall

- Test the suites `STRUCTURE_CHECKS`, `NULL_PARAMETER_TESTS` and `NULL_RETURN_CHECKS`
  
- Additionally test `SportsClubContractTest` and `MemberContractTest`
  
- Print the overview output to `System.out` (default)
  
- Print the full output to `C:\results\testresults.txt`
  

```java
TestEngine engine = TestEngine.builder()
    .registerSuite(Suites.STRUCTURE_CHECKS)
    .registerSuite(Suites.NULL_PARAMETER_CHECKS)
    .registerSuite(Suites.NULL_RETURN_CHECKS)
    .registerTest(new SportsClubContractTest())
    .registerTest(new MemberContractTest())
    .fullOutput(Path.of("C:\\results\\testresults.txt"))
    .build();

engine.test();
```

There are some information that you can extract from the engine and from the return value of `.test()` that might be useful, but I will not go into detail here.

### Step 4: Run the code

After you've configured the engine, you can compile the entire code and run `matrnr.TestMain.main()`. The overview output will be printed to stdout (if not specified otherwise) and the full output will be printed to a file (if you specified a path).

## Tips:

1. Test incremental. Begin with the structure check suite, then continue on to the nullcheck suites, and in the end the contract check and shallow copy suites.
  
2. The 5 suites encapsulate all 23 available tests. If you test them all, there's no need to register isolated tests.
  
3. To find more information about a failed test in the full output file, you can search for the test name or, in general, for `FAILURE` to quickly find the location at which the detail output of the test can be found.
  
4. If you have any questions, contact me (see Discord ID at the top).
