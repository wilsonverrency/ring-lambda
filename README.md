# ring-lambda

A ring lambda handler with reitit routes.

### Dockerfile

1. Change the name of the project in project.clj (this will decide the name of the standalone jar)
2. Change the path of the standaalone jar in the Dockerfile
3. Change the classname and handler method in the Dockerfile

## Testing the container locally

- https://docs.aws.amazon.com/lambda/latest/dg/images-test.html#images-test-add

```bash
## build a local image
docker build . -t ring-lambda:latest
```

```bash
## run the container locally
docker run --rm -p 9000:8080 ring-lambda:latest
```

```bash
curl "http://localhost:9000/2015-03-31/functions/function/invocations" \
 -H "Content-Type: application/json" \
 --data-binary "@test-resources/sample-apigw-event.json"
```

## References

- https://wtfleming.github.io/blog/clojure-aws-lambda/
- https://sideshowcoder.com/2018/05/11/clojure-ring-api-gateway-lambda/
- https://docs.aws.amazon.com/lambda/latest/dg/java-image.html#java-image-instructions
- https://docs.aws.amazon.com/lambda/latest/dg/java-handler.html
- https://github.com/aws/aws-lambda-runtime-interface-emulator/
- https://github.com/Quantisan/docker-clojure

## License

Copyright © 2024 FIXME

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
