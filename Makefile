prepare-for-graal:
	scala-cli run scripts/package-setup.scala

package:
	scala-cli package --native-image --graalvm-java-version 19 --graalvm-args --verbose --graalvm-args --no-fallback --graalvm-args -H:+ReportExceptionStackTraces --graalvm-args --initialize-at-build-time=scala.runtime.Statics$$VM --graalvm-args --initialize-at-build-time=scala.Symbol --graalvm-args --initialize-at-build-time=scala.Symbol$$ --graalvm-args --native-image-info --graalvm-args -H:IncludeResources=libcrossterm.dylib --graalvm-args -H:-UseServiceLoaderFeature scala/ -o skan

run:
	scala-cli run scala

clean:
	rm -rf scala/.scala-build/
	rm -rf scala/.bsp/
	rm -rf scripts/.scala-build/
	rm scala/resources/jni-config.json
	rm scala/resources/libcrossterm.dylib

install:
	cp skan ~/bin/skan

format:
	scala-cli format .

format-check:
	scala-cli format --check .

