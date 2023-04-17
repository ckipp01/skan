prepare-for-graal:
	scala-cli run scripts/package-setup.scala

package:
	scala-cli --power \
		package \
		--native-image \
		--graalvm-java-version 19 \
		--graalvm-version 22.3.1 \
		--graalvm-args --verbose \
		--graalvm-args --no-fallback \
		--graalvm-args -H:+ReportExceptionStackTraces \
		--graalvm-args --initialize-at-build-time=scala.runtime.Statics$$VM \
		--graalvm-args --initialize-at-build-time=scala.Symbol \
		--graalvm-args --initialize-at-build-time=scala.Symbol$$ \
		--graalvm-args --native-image-info \
		--graalvm-args -H:IncludeResources=libcrossterm.dylib \
		--graalvm-args -H:-UseServiceLoaderFeature \
		skan/ -o skan-local

run:
	scala-cli run skan

clean:
	rm -rf skan/.scala-build/
	rm -rf skan/.bsp/
	rm -rf scripts/.scala-build/
	rm -f skan/resources/jni-config.json
	rm -f skan/resources/libcrossterm.dylib

install:
	cp skan-local ~/bin/skan

format:
	scala-cli format scripts
	scala-cli format skan

format-check:
	scala-cli format --check scripts
	scala-cli format --check skan

test:
	scala-cli test skan
