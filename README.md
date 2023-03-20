# Lens Beacon service

A component that translates Lens queries into Beacon 2.0, executes them against a Beacon API
and then returns the results.

To run from Docker:

```
docker run --env-file .env samply/lens_beacon_service
```

Alternatively, you can use docker-compose. An example docker-compose.yml file has been
included in this repository. To start:

```
docker-compose up
```

## Standalone build and run

You can also build and run this service on your own computer, if Docker is not an option.

### Prerequisites

Please run from within Linux.

You will need to install git, Java 19 and Maven. Clone this repository and cd into it.

### Building

From the command line, run:

```
mvn clean install
```

### Running

You need to provide the necessary parameters via environment variables, as documented above. These should be set in the same shell as the one that you use to run java.

Once you have done this, you can start the service directly from the command line:

```
java -jar target/lens_beacon_service\*.jar
```

## License
        
Copyright 2022 The Samply Community
        
Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
        
http://www.apache.org/licenses/LICENSE-2.0
        
Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 
