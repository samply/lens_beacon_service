# Lens Beacon service

Part of a federated query system for sites exposing Beacon 2 APIs.

This component translates queries coming from a GUI into Beacon 2.0. It executes them against the Beacon APIs
of one or more sites, and then returns the results.

## Basic principles of operation

A GUI component has been set up to allow researchers to run searches against multiple Beacon sites, see [lens-beacon](https://github.com/samply/lens-beacon).

The GUI is able to talk to the lens_beacon_service via a RESTful API.

The GUI has its own internal language for describing the queries that users are makeing, known as AST. This is a hierarchically structured graph that reflects the structure of the query as seen in the GUI. The leaf nodes of the hierarchy represent the search terms specified by the user.

The GUI serializes the AST hierarchy into JSON and sends it to the lens_beacon_service.

The AST is converted by this service into Beacon filters, which are used to build queries that are sent to all of the Beacon sites that are registered at the lens_beacon_service.

Results coming back from the sites are converted into FHIR measure reports, which are serialized and returned to The GUI.

The GUI uses the measure reports to display information from each site, plus summarizing histograms or pie charts for selected attributes.

## Docker

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

You can start the service directly from the command line:

```
java -jar target/lens_beacon_service\*.jar
```

## Guide to the source code

Broadly speaking, the code splits up into 3 categories of functionality:

- The code that talks to the GUI: package de.samply.lens_beacon_service.lens.
- The code that talks to Beacon: package de.samply.lens_beacon_service.beacon.
- The code that mediates between the GUI and Beacon: packages de.samply.lens_beacon_service.query, de.samply.lens_beacon_service.entrytype, de.samply.lens_beacon_service.site and de.samply.lens_beacon_service.measurereport.

More details of these packages can be found in the following sections.

### Connection with the GUI

The URL for the lens_beacon_service API is hardcoded into the GUI.

This is on port 8080. The lens_beacon_service API provides a single POST endpoint: "query/ast".

This endpoint expects to get a JSON-serialized AST hierarchy from the GUI. The simplest possible query looks like this:

```
{
   "operand" : "AND",
   "children" : [ ],
   "key" : "main",
   "en" : "main",
   "de" : "haupt",
   "type" : null,
   "system" : null,
   "value" : null
}
```

This will search for all possible hits, because the "chlidren" list, which may contain terms that constrain the search, is empty.

A more complex query could look like this:

```
{
   "operand" : "AND",
   "children" : [ {
     "operand" : null,
     "children" : null,
     "key" : "gender",
     "en" : null,
     "de" : null,
     "type" : "IN",
     "system" : "",
     "value" : [ "female" ]
   } ],
   "key" : "main",
   "en" : "main",
   "de" : "haupt",
   "type" : null,
   "system" : null,
   "value" : null
}

```

This has one child node, and it constrains gender to "female". A search using this term should only return a count of individuals who are female.

The classes relevant to processing this are:

- de.samply.lens_beacon_service.lens.Api. This class provides the endpoint that the GUI talks to.
- de.samply.lens_beacon_service.lens.AstNode. This is the model for an AST hierarchy. It represents a node with zero or more child nodes.

### Running a query

This is done in the class de.samply.lens_beacon_service.query.QueryService, which is something like the heart of the lens_beacon_service. The method QueryService.runQuery() takes the AST, runs the query, and returns the results as a string. 

The return string is serialized JSON, and is structured as follows:

```
[
  {
    "measureReport": { ... },
    "siteName": <Name of Beacon site, e.g. "EGA Cineca">,
    "siteUrl": <Name of URL site, e.g. "https://ega-archive.org/beacon-apis/cineca">
  },
  ...
]
```

This is a list, with one result per Beacon site. "siteName" and "siteUrl" are metadata relating to the site. The measure report is a FHIR data structure that contains the results of the search, in the form of counts. E.g. the count of patiens at the given site who fit the search criteria.

The method QueryService.runQuery() executes the following steps:

- Create a fresh list of Sites.
- Convert the AST nodes from the GUI into Beacon filters.
- Run a query at each Beacon site, using the filters. Both the query specified by the user and the queries necessary for the stratifiers will be run.
- Insert the query results (counts) into measure reports, one per site.
- Decorate measure reports with site name and URL.
- Serialize to JSON string.

The class de.samply.lens_beacon_service.query.Query is responsible for runninq a query on a single endpoint at a single Beacon site. 

### Beacon

The class de.samply.lens_beacon_service.beacon.BeaconQueryService is responsible for talking to the Beacon API. It transmits filters to the API, extracts counts from the returned results, and returns these counts.

In the package de.samply.lens_beacon_service.beacon.model you will find the classes used to model the various objects relevant to Beacon queries. BeaconRequest, BeaconQuery and BeaconFilter are used to build the JSON that gets sent to the Beacon API. BeaconResponse is used to capture the return value from the API call. BeaconEndpoint describes a single endpoint at the API (there will be multiple querying endpoints, e.g. "individuals" or "biosamples"). It provides the exact URI, plus the method, either POST or GET.

### Entry Type

The Beacon endpoints mentioned above correspond to the so-called entry types that make up the Beacon data model. In the source code, you will find the following entry types:

- biosamples
- individuals
- genomicVariations

These correspond to standard Beacon endpoints. There are several more standard endpoints, but these have not yet been implemented in this repo. You could also implement your own entry types, e.g. if you want to provide DICOM image metadata. You will see that the implementation follows the same pattern for all entry types.

The concept of entry type is quite central in this implementation. There is a class used to capture everything relevant to entry types: de.samply.lens_beacon_service.entrytype.EntryType. Objects of this type are used to keep track of the querying process for a single endpoint at a single Beacon site. The following information is stored here:

- Information about the Beacon endpoint it is using. This information is stored in a BeaconEndpoint object. 
- A converter, that converts from AST nodes to Beacon filters. In this way, different entry types will get different filters. It is not possible to perform queries that cross entry types. For example, it is not possible to specify that you want all biosamples from individuals who are female. The converters should ensure that the endpoints of the entry types are only supplied with the filters that they are able to deal with.
- The generated filters.
- A GroupAdmin object is used for inserting the counts generated by a Beacon search into a MeasureReportGroupComponent (see section below on measure reports).

### Sites

When the lens_beacon_service has generated a Beacon query, it sends it to each registered Beacon site.

In the current implementation, registered sites are hard-coded into the de.samply.lens_beacon_service.site.Sites class.

The definition of the individual sites, e.g. their URLs, can be found in the package de.samply.lens_beacon_service.site.

### Measure reports

If you were to run a CQL query against a FHIR data store, you would get the results back as a MeasureReport object. The GUI understands measure reports and can use them for populating the GUI. The lens_beacon_service uses MeasureReport objects for holding the information gathered from the Beacon query in order to take advantage of this functionality.

A measure report has a hierarchical structure, with 3 levels:

- MeasureReport
- MeasureReportGroupComponent
- MeasureReportGroupStratifierComponent

We have standardized on the following mapping from Beacon results to measure reports:

- A Beacon site has one MeasureReport.
- Each entry type supported by a site has a MeasureReportGroupComponent to itself. This will receive the population count associated with the entry type, e.g. the count of individuals returned by the query.
- A MeasureReportGroupComponent can have zero or more MeasureReportGroupStratifierComponents. These will map one-to-one onto stratifier panels in the GUI.

Code for dealing with measure reports can be found in de.samply.lens_beacon_service.measurereport. There are two "Admin" classes that can instantiate and populate MeasureReport objects and MeasureReportGroupComponent objects.

## License
        
Copyright 2022 The Samply Community
        
Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
        
http://www.apache.org/licenses/LICENSE-2.0
        
Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 
