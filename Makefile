prepare-for-graal:
	scala-cli run scripts/package-setup.scala

package-mac:
	make prepare-for-graal
	make generate-build-info
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
		skan/ -o out/skan

package-linux:
	make prepare-for-graal
	make generate-build-info
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
		--graalvm-args -H:IncludeResources=libcrossterm.so\
		--graalvm-args -H:-UseServiceLoaderFeature \
		skan/ -o out/skan

package-windows:
	make prepare-for-graal
	make generate-build-info
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
		--graalvm-args -H:IncludeResources=crossterm.dll \
		--graalvm-args -H:-UseServiceLoaderFeature \
		skan/ -o out/skan

generate-build-info:
	scala-cli run scripts/generate-build-info.scala

run:
	make generate-build-info
	scala-cli run skan

clean:
	rm -rf skan/.scala-build/
	rm -rf skan/.bsp/
	rm -rf scripts/.scala-build/
	rm -f skan/resources/jni-config.json
	rm -f skan/resources/libcrossterm.dylib

install:
	cp out/skan ~/bin/skan

format:
	scala-cli format scripts
	scala-cli format skan

format-check:
	scala-cli format --check scripts
	scala-cli format --check skan

test:
	make generate-build-info
	scala-cli test skan

compile:
	make generate-build-info
	scala-cli compile skan

setup-for-ide:
	make generate-build-info
	scala-cli setup-ide scripts
	scala-cli setup-ide skan
