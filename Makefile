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
		--graalvm-args -H:IncludeResources=libnative-x86_64-darwin-crossterm.dylib \
		--graalvm-args -H:IncludeResources=libnative-arm64-darwin-crossterm.dylib \
		--graalvm-args -H:IncludeResources=libnative-x86_64-linux-crossterm.so \
		--graalvm-args -H:IncludeResources=native-x86_64-windows-crossterm.dll \
		--graalvm-args -H:-UseServiceLoaderFeature \
		skan/ -o out/skan

run:
	scala-cli run .

clean:
	rm -rf skan/.scala-build/
	rm -rf skan/.bsp/

install:
	cp out/skan ~/bin/skan

format:
	scala-cli format .

format-check:
	scala-cli format --check .

test:
	scala-cli test .

compile:
	scala-cli compile .

setup-ide:
	scala-cli setup-ide .
