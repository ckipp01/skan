# Contributing to skan

Thanks for being willing to contribute!

`skan` is written in [Scala](https://scala-lang.org/) and built with
[scala-cli](https://scala-cli.virtuslab.org/). You'll find a
[`Makefile`](./Makefile) at the root of this project that will have most of the
commands that you'll actually need to work on `skan`.

## Prerequisites

- Make sure [scala-cli](https://scala-cli.virtuslab.org/install) is installed on
  your machine.

## Common Tasks

### Setting up for your editor

Most commonly for Scala people will use either
[IntelliJ](https://www.jetbrains.com/help/idea/discover-intellij-idea-for-scala.html)
or [Metals](https://scalameta.org/metals/). You're free to use what you'd like
but before using either, you'll want to run the following:

```
make setup-for-ide
```

After doing this you'll want to either open up the `skan` or `scripts` directory
as your root depending on which you'd like to work on.

### Compiling your project

NOTE: there is a bit of a hacky solution that we have to produce a
`BuildInfo.scala` file before you compile anything. You'll notice that many of
the commands call `make generate-build-info` before the actual command. This
make sure that the `BuildInfo` is there. If you get a warning at any point
because of a missing `BuildInfo` file, something is going wrong since `make
generate-build-info` wasn't called by whatever command you're running. However,
this is just for context, in reality it will hopefully all just work™.

To compile `skan` you'll run the following:

```
make compile
```

### To run the tests

The tests are fast enough that you shouldn't have to worry about only running a
subset of them. So the easiest way to run them is:

```
make test
```

### To run the project

To test that everything is working as expected you can run your current code
with:

```
make run
```

### Formatting the sources

To format everything with [scalafmt](https://scalameta.org/scalafmt/) you'll run
the following:

```
make format
```

### Building a native image

Currently there is a couple workarounds in place to account for [this
issue](https://github.com/oracle/graal/issues/5219) in Graal. Because of this
you need to run a prepare script ahead of packaging. So to fully package you'd
run the following (replace mac with linux or windows depending on your OS):

```
make prepare-for-graal package-mac
```

If this works you should get a `out/skan` created that you can execute with
`./out/skan` to test.

## Helpful links

- [tui-scala](https://github.com/oyvindberg/tui-scala) which is the main library
  used for `skan`.
