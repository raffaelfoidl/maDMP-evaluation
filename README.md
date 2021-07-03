# maDMP Evaluation

[![DOI](https://zenodo.org/badge/364735916.svg)](https://zenodo.org/badge/latestdoi/364735916)

Have a look at our project documentation
at [https://raffaelfoidl.github.io/maDMP-evaluation/](https://raffaelfoidl.github.io/maDMP-evaluation/).

This page provides a less linear and more structured version of the information presented in this README.

## Project Overview

[Machine-actionable data management plans (maDMPs)](https://github.com/RDA-DMP-Common/RDA-DMP-Common-Standard) have, by
their very nature, advantages over data management plans that are written exclusively in text form. By employing maDMPs,
not only researches should be able to benefit from their merits, but also research funders receiving and assessing the
DMPs.

In
its _[Practical Guide to the International Alignment of Research Data Management](https://www.scienceeurope.org/our-resources/practical-guide-to-the-international-alignment-of-research-data-management/)_
, Science Europe has published an evaluation rubric (section _Guidance for Reviewers_) that provides a solid basis to
support research (funding) organisations in evaluating DMPs. By stating a set of criteria, it helps to ensure submitted
DMPs cover required aspects and support [FAIR data management](https://www.go-fair.org/fair-principles/).

This project aims to facilitate leveraging the machine-actionability of DMPs by
providing [SPARQL](https://www.w3.org/TR/sparql11-query/)
queries that are meant to automatically give an initial assessment of the respective data management plan's quality.

## Repository Structure

```shell
.
├── app-bin
├── dcso-json
│   ├── src
│   ├── target
│   ├── README.md
│   ├── dcso-json.iml
│   └── pom.xml
├── docs
├── maDMPs
│   ├── 1-community
│   ├── 2-preprocessed
│   ├── 3-normalized
│   ├── 4-json-ld-converted
│   ├── 5-json-ld-postprocessed
│   └── convert.sh
├── queries
└── generate_docs.sh
```

* `app-bin`: distribution directory of the `dcso-json` tool (built and distributed upon `mvn install`, refer to its
  [documentation](https://raffaelfoidl.github.io/maDMP-evaluation/0008.html) for more information)
* `dcso-json`: the source code of the `dcso-json` tool, bundled as a `maven` project
* `docs`: the content of the [documentation webpage](https://raffaelfoidl.github.io/maDMP-evaluation/); served via
  GitHub pages and the static site generator [Jekyll](https://jekyllrb.com/).
* `maDMPs`: different versions of the machine-actionable DMPs assessed during this project, from raw input data to
  normalized JSON-LD [DCSO](https://github.com/RDA-DMP-Common/RDA-DMP-Common-Standard/tree/master/ontologies)
  serializations (see also [Methodology](https://raffaelfoidl.github.io/maDMP-evaluation/0003.html)).
* `queries`: SPARQL queries conceived during this project to assess the quality of maDMPs
* `generate_docs.sh`: regenerates the content of the `docs` folder based on the README files in this repository

The regeneration of the documentation webpage is triggered and automatically executed on the `docs` branch using a
GitHub Action at every push to the `main` branch. In other words, the `docs` branch is the source of truth
for [https://raffaelfoidl.github.io/maDMP-evaluation/](https://raffaelfoidl.github.io/maDMP-evaluation/)
and is updated at every push to `main` using a
dedicated [GitHub Action](https://docs.github.com/en/actions/learn-github-actions/introduction-to-github-actions).

The `docs` folder on the `main` branch is only updated at sporadically, e.g. at releases (commits with a release tag).

## Methodology

### Input Pipeline

The maDMPs we use as raw input data for our project are taken directly from the Zenodo
Community [Data Stewardship 2021 - DMPs](https://zenodo.org/communities/dast-2021/).

1. Start with raw maDMPs from the Zenodo Community.
2. Ensure [schema](https://github.com/RDA-DMP-Common/RDA-DMP-Common-Standard/blob/master/examples/JSON/JSON-schema/1.1/maDMP-schema-1.1.json)
conformity, uniform formatting and indenting
3. Normalization: Establish uniform, alphabetical sorting of JSON properties.
4. Convert JSON/Turtle maDMPs to
   a [DCSO](https://github.com/RDA-DMP-Common/RDA-DMP-Common-Standard/tree/master/ontologies) instance in a JSON-LD
   serialization using the `dcsojon` tool.
5. Apply postprocessing to the JSON-LD maDMPs (again, establish uniform, alphabetical sorting of JSON-LD properties).

Regarding step 2, the following changes had to be made in order to achieve schema conformity for all input maDMPs:

* `4.json`
    * removed line breaks within JSON string literals
* `6.json`
    * some closing brackets were missing
    * object nesting hierarchy in the `distribution` field was incorrect (we could only make a guess as to the author's
      original intentions)
* `10.json`
    * objects instead of arrays with one element were used
    * sometimes, strings instead of numerical values (e.g. `int`) were used
    * two of the four data sets did not exhibit the required `dataset_id` field
    * on one occasion, a datetime format was used instead of a date
* `11.json`
    * correction regarding datetime format: `2021-04-12T25:10:16.8` -> `2021-04-12T25:10:16.8Z`
    * correction regarding incorrect time value: `2021-04-12T25:10:16.8Z` -> `2021-04-12T23:10:16.8Z` (a day does not
      have more than 24 hours)

Step 4 has been performed automatically via
the [convert.sh](https://github.com/raffaelfoidl/maDMP-evaluation/blob/main/maDMPs/convert.sh) shell script. For more
information on the `dcso-json` tool invoked by this script, please refer to
the [dcso-json overview](https://raffaelfoidl.github.io/maDMP-evaluation/0008.html).

### Processing of the Semantic maDMP Representation

After having brought the maDMPs into a semantically enriched JSON-LD format, we were ready to express requirements from
the evaluation rubric mentioned in the [Project Overview](https://raffaelfoidl.github.io/maDMP-evaluation/0001.html). We
developed queries that project certain subsets of the data into a customized view (`SELECT` queries) as well as ones
that simply indicate whether some criteria are satisfied (`ASK` queries).

A more in-depth, but still summarized, overview of the queries we created can be observed
in [Queries](https://raffaelfoidl.github.io/maDMP-evaluation/0005.html). The queries themselves are available in
the [queries](https://github.com/raffaelfoidl/maDMP-evaluation/tree/main/queries) directory.

During our experiment, we used a local [GraphDB](https://www.ontotext.com/products/graphdb/) instance as triple store
and SPARQL endpoint. Other triple stores such as [Jena Fuseki](https://jena.apache.org/documentation/fuseki2/) are of
course eligible as well. However, as a result of previous experiences with it, we opted for GraphDB.

### Report on Quality of Input maDMPs

Finally, after having created the queries, we applied them to the maDMPs that made up our input and which had previously
been imported into a GraphDB repository. The results of this assessment can be found in
the [Assessment Report](https://raffaelfoidl.github.io/maDMP-evaluation/0006.html).

## Covered Criteria

The tables in the following subsections depict which criteria from the evaluation rubric we were able to express via
SPARQL queries.

We want to stress that some queries could be formulated less strict, i.e. `OPTIONAL` blocks could be inserted for triple
patterns that join elements from the maDMP schema that are, by the schema definition, optional. However, as this project
is more of a proof-of-concept-kind, this could be easily be done when extending or building upon the work at hand - in
order not to "lose" any results/information about the corresponding maDMP.

The queries can be found in the corresponding [directory](https://github.com/raffaelfoidl/maDMP-evaluation/tree/main/queries)
in the GitHub repository. In the next subsections, the queries are referred to by their filename without extension, e.g.
a reference to `5-a-1` is points to the query file `5-a-1.sparql`.

### General Information

<table class="rtable">
<thead>
    <tr>
        <th>Requirement</th>
        <th>Covered In</th>
        <th>Remarks</th>
    </tr>
</thead>
<tbody>
    <tr>
        <td colspan="4"><b>Administrative information</b></td>
    </tr>
    <tr>
        <td>Provide information such as name of applicant, project number, funding programme, version of DMP.Provide information such as name of applicant, project number, funding programme, version of DMP.</td>
        <td><code>0-1</code></td>
        <td></td>
    </tr>
</tbody>
</table>

### Data Description and Collection or Re-Use of Existing Data

<table class="rtable">
<thead>
    <tr>
        <th>Requirement</th>
        <th>Covered In</th>
        <th>Remarks</th>
    </tr>
</thead>
<tbody>
    <tr>
        <td colspan="4"><b>1a How will new data be produced and/or how will existing data be re-used?</b></td>
    </tr>
    <tr>
        <td>Explain which methodologies or software will be used if new data are collected or produced.</td>
        <td>/</td>
        <td>Information provided by the <code>methodology</code> field in the <code>dataset</code> structure - however, this field is only specified in the funder extension and is not included in the RDA-DMP Common Standard; therefore, it can not be translated when converting the JSON files to a JSON-LD format and in consequence, not be queried.</td>
    </tr>
    <tr>
        <td>State any constraints on re-use of existing data if there are any.</td>
        <td>/</td>
        <td>Not really covered by maDMP.</td>
    </tr>
    <tr>
        <td>Explain how data provenance will be documented.</td>
        <td>/</td>
        <td>Not really covered by maDMP.</td>
    </tr>
    <tr>
        <td>Briefly state the reasons if the re-use of any existing data sources has been considered but discarded.</td>
        <td>/</td>
        <td>Not really covered by maDMP.</td>
    </tr>
    <tr>
        <td colspan="4"><b>1b What data (for example the kind, formats, and volumes) will be collected or produced?</b></td>
    </tr>
    <tr>
        <td>Give details on the kind of data: for example, numeric (databases, spreadsheets), textual (documents), image, audio, video, and/or mixed media.</td>
        <td><code>1-b-1</code></td>
        <td>Queries all declared datasets and displays their title, type and identifier.</td>
    </tr>
    <tr>
        <td>Give details on the data format: the way in which the data is encoded for storage, often reflected by the filename extension (for example pdf, xls, doc, txt, or rdf).</td>
        <td><code>1-b-2</code></td>
        <td>Returns the data formats of each specified distribution (including the respective access URL and description of the distribution).</td>
    </tr>
    <tr>
        <td>Justify the use of certain formats. For example, decisions may be based on staff expertise within the host organisation, a preference for open formats, standards accepted by data repositories, widespread usage within the research community, or on the software or equipment that will be used.</td>
        <td>/</td>
        <td>Not really covered by maDMP.</td>
    </tr>
    <tr>
        <td>Give preference to open and standard formats as they facilitate sharing and long-term re-use of data (several repositories provide lists of such ‘preferred formats’).</td>
        <td>/</td>
        <td>Not directly covered by maDMP; difficult to cover with a simple SPARQL query.</td>
    </tr>
    <tr>
        <td>Give details on the volumes (they can be expressed in storage space required (bytes), and/or in numbers of objects, files, rows, and columns).</td>
        <td><code>1-b-3</code></td>
        <td>Displays for each defined distribution its size in bytes.</td>
    </tr>
</tbody>
</table>

### Documentation and Data Quality

<table class="rtable">
<thead>
    <tr>
        <th>Requirement</th>
        <th>Covered In</th>
        <th>Remarks</th>
    </tr>
</thead>
<tbody>
    <tr>
        <td colspan="4"><b>2a What metadata and documentation (for example the methodology of data collection and way of organising data) will accompany the data?</b></td>
    </tr>
    <tr>
        <td>Indicate which metadata will be provided to help others identify and discover the data.</td>
        <td><code>2-a-1</code>, <code>2-a-2</code></td>
        <td><code>2-a-1</code> collects all information provided by the <code>metadata</code> field, i.e., a description (optional), the used standard and the language. <code>2-a-2</code> displays the specified keywords for each defined dataset.</td>
    </tr>
    <tr>
        <td>Indicate which metadata standards (for example DDI, TEI, EML, MARC, CMDI) will be used.</td>
        <td><code>2-a-1</code></td>
        <td>Information about the used metadata standards is covered in this query.</td>
    </tr>
    <tr>
        <td>Use community metadata standards where these are in place.</td>
        <td><code>2-a-3</code></td>
        <td>Example query for testing whether certain community standards (Dublin Core, DDI, EML, TEI or MARC) are used. This can be arbitrarily modified based on which standards are preferred.</td>
    </tr>
    <tr>
        <td>Indicate how the data will be organised during the project mentioning, for example, conventions, version control, and folder structures. Consistent, well-ordered research data will be easier to find, understand, and re-use.</td>
        <td><code>2-a-4</code></td>
        <td>Displays whether the given distribution hosts support versioning. The other information is not really covered by maDMP; if it is included in the maDMP, then probably in the <code>data_quality_assurance</code> field which is covered by query <code>2-b-1</code>.</td>
    </tr>
    <tr>
        <td>Consider what other documentation is needed to enable re-use. This may include information on the methodology used to collect the data, analytical and procedural information, definitions of variables, units of measurement, and so on.</td>
        <td>/</td>
        <td>This information would (if anything) probably be included in the <code>methodology</code> field in the <code>dataset</code> structure - however, this field is only specified in the funder extension and is not included in the RDA-DMP Common Standard; therefore, it can not be translated when converting the JSON files to a JSON-LD format and in consequence, not be queried.</td>
    </tr>
    <tr>
        <td>Consider how this information will be captured and where it will be recorded (for example in a database with links to each item, a 'readme' text file, file headers, code books, or lab notebooks).</td>
        <td>/</td>
        <td>Not really covered by maDMP.</td>
    </tr>
    <tr>
        <td colspan="4"><b>2b What data quality control measures will be used?</b></td>
    </tr>
    <tr>
        <td>Explain how the consistency and quality of data collection will be controlled and documented. This may include processes such as calibration, repeated samples or measurements, standardised data capture, data entry validation, peer review of data, or representation with controlled vocabularies.</td>
        <td><code>2-b-1</code></td>
        <td>The best one can do is with the <code>data_quality_assurance</code> element.</td>
    </tr>
</tbody>
</table>

### Storage and Backup During the Research Process

<table class="rtable">
<thead>
    <tr>
        <th>Requirement</th>
        <th>Covered In</th>
        <th>Remarks</th>
    </tr>
</thead>
<tbody>
    <tr>
        <td colspan="4"><b>3a How will data and metadata be stored and backed up during the research?</b></td>
    </tr>
    <tr>
        <td>Describe where the data will be stored and backed up during research activities and how often the backup will be performed. It is recommended to store data in least at two separate locations.</td>
        <td><code>3-a-1</code></td>
        <td>Retrieving information about backups is only possible by querying the <code>host</code> element. If provided, the query returns the backup type and frequency for each specified host, as well as some information about the host.</td>
    </tr>
    <tr>
        <td>Give preference to the use of robust, managed storage with automatic backup, such as provided by IT support services of the home institution. Storing data on laptops, stand-alone hard drives, or external storage devices such as USB sticks is not recommended.</td>
        <td>/</td>
        <td>Not really covered by maDMP.</td>
    </tr>
    <tr>
        <td colspan="4"><b>3b How will data security and protection of sensitive data be taken care of during the research?</b></td>
    </tr>
    <tr>
        <td>Explain how the data will be recovered in the event of an incident.</td>
        <td>/</td>
        <td>Not really covered by maDMP.</td>
    </tr>
    <tr>
        <td>Explain who will have access to the data during the research and how access to data is controlled, especially in collaborative partnerships.</td>
        <td><code>3-b-1</code></td>
        <td>The best one can do is with the <code>security_and_privacy</code> field. Information about the availability of data hosts is included in query <code>3-a-1</code>.</td>
    </tr>
    <tr>
        <td>Consider data protection, particularly if your data is sensitive (for example containing personal data, politically sensitive information, or trade secrets). Describe the main risks and how these will be managed.</td>
        <td><code>3-b-2</code></td>
        <td>Description of risks and countermeasures are not really covered by maDMP. Information about whether data are sensitive is covered.</td>
    </tr>
    <tr>
        <td>Explain which institutional data protection policies are in place.</td>
        <td>/</td>
        <td>Information provided by the <code>related_policy</code> field in the <code>dmp</code> structure - however, this field is only specified in the funder extension and is not included in the RDA-DMP Common Standard; therefore, it can not be translated when converting the JSON files to a JSON-LD format and in consequence, not be queried.</td>
    </tr>
</tbody>
</table>

### Legal and Ethical Requirements, Code of Conduct

<table class="rtable">
<thead>
    <tr>
        <th>Requirement</th>
        <th>Covered In</th>
        <th>Remarks</th>
    </tr>
</thead>
<tbody>
    <tr>
        <td colspan="4"><b>4a If personal data are processed, how will compliance with legislation on personal data and security be ensured?</b></td>
    </tr>
    <tr>
        <td>Ensure that when dealing with personal data, data protection laws (for example GDPR) are complied with.  <i>(including sub-points)</i></td>
        <td><code>5-a-2</code></td>
        <td>If anything, information about consent for preservation or sharing and anonymization would be included in the <code>preservation_statement</code> which is already covered by query <code>5-a-2</code>. The other aspects are not really covered by maDMP.</td>
    </tr>
    <tr>
        <td colspan="4"><b>4b How will other legal issues, such as intellectual property rights and ownership, be managed? What legislation is applicable?</b></td>
    </tr>
    <tr>
        <td>Explain who will be the owner of the data, meaning who will have the rights to control access. <i>(including sub-points)</i></td>
        <td><code>3-b-1</code>, <code>5-a-3</code></td>
        <td>If anything, access restrictions would be included in the <code>security_and_privacy</code> field which is already covered by query <code>3-b-1</code>. Descriptions of the licenses in place are queried with query <code>5-a-3</code>.</td>
    </tr>
    <tr>
        <td>Indicate whether intellectual property rights (for example Database Directive, sui generis rights) are affected. If so, explain which and how will they be dealt with.</td>
        <td>/</td>
        <td>Not really covered by maDMP.</td>
    </tr>
    <tr>
        <td>Indicate whether there are any restrictions on the re-use of third-party data.</td>
        <td>/</td>
        <td>Not really covered by maDMP.</td>
    </tr>
    <tr>
        <td colspan="4"><b>4c What ethical issues and codes of conduct are there, and how will they be taken into account?</b></td>
    </tr>
    <tr>
        <td>Consider whether ethical issues can affect how data are stored and transferred, who can see or use them, and how long they are kept. Demonstrate awareness of these aspects and respective planning.</td>
        <td><code>4-c-1</code>, <code>4-c-2</code></td>
        <td>Query <code>4-c-1</code> checks whether ethical issues exist. Query <code>4-c-2</code> returns a description of the specified ethical issues, if there are any, as well as the ethical issues report, if there is one.</td>
    </tr>
    <tr>
        <td>Follow the national and international codes of conducts and institutional ethical guidelines, and check if ethical review (for example by an ethics committee) is required for data collection in the research project.</td>
        <td>/</td>
        <td>Not really covered by maDMP.</td>
    </tr>
</tbody>
</table>

### Data Sharing and Long-Term Preservation

<table class="rtable">
<thead>
    <tr>
        <th>Requirement</th>
        <th>Covered In</th>
        <th>Remarks</th>
    </tr>
</thead>
<tbody>
    <tr>
        <td colspan="4"><b>5a How and when will data be shared? Are there possible restrictions to data sharing or embargo reasons?</b></td>
    </tr>
    <tr>
        <td>Explain how the data will be discoverable and shared (for example by deposit in a trustworthy data repository, indexed in a catalogue, use of a secure data service, direct handling of data requests, or use of another mechanism).</td>
        <td><code>5-a-1</code></td>
        <td>Displays information about distribution such as Host, access URL and distributed file formats.</td>
    </tr>
    <tr>
        <td>Outline the plan for data preservation and give information on how long the data will be retained.</td>
        <td><code>5-a-2</code></td>
        <td>The best one can do is with the preservation statement. However, note that this query does not return anything for our input files in JSON-LD format, probably because the <code>preservation_statement</code> field in the JSON files is ignored by the DCSO-JSON tool (see "Known issues" in <a href="https://raffaelfoidl.github.io/maDMP-evaluation/0008.html">the README of the tool</a>) and hence, not converted. In consequence, this field can obviously not be queried.</td>
    </tr>
    <tr>
        <td>Explain when the data will be made available. Indicate the expected timely release. Explain whether exclusive use of the data will be claimed and if so, why and for how long. Indicate whether data sharing will be postponed or restricted for example to publish, protect intellectual property, or seek patents.</td>
        <td><code>5-a-3</code></td>
        <td>Gathers information about the data usage constraints (license, embargo period, data access, release data).</td>
    </tr>
    <tr>
        <td>Indicate who will be able to use the data. If it is necessary to restrict access to certain communities or to apply a data sharing agreement, explain how and why. Explain what action will be taken to overcome or to minimise restrictions.</td>
        <td>/</td>
        <td>Not really covered by maDMP. Possible information contained in maDMP is already queried by <code>5-a-3</code>.</td>
    </tr>
    <tr>
        <td colspan="4"><b>5b How will data for preservation be selected, and where data will be preserved long-term (for example a data repository or archive)?</b></td>
    </tr>
    <tr>
        <td>Indicate what data must be retained or destroyed for contractual, legal, or regulatory purposes.</td>
        <td>/</td>
        <td>Not explicitly covered by maDMP; maybe with <code>5-a-2</code></td>
    </tr>
    <tr>
        <td>Indicate how it will be decided what data to keep. Describe the data to be preserved long-term.</td>
        <td>/</td>
        <td>Not explicitly covered by maDMP; maybe with <code>5-a-2</code></td>
    </tr>
    <tr>
        <td>Explain the foreseeable research uses (and/or users) for the data.</td>
        <td>/</td>
        <td>Not covered by maDMP.</td>
    </tr>
    <tr>
        <td>Indicate where the data will be deposited. If no established repository is proposed, demonstrate in the DMP that the data can be curated effectively beyond the lifetime of the grant. It is recommended to demonstrate that the repositories policies and procedures (including any metadata standards, and costs involved) have been checked.</td>
        <td><code>5-b-1</code></td>
        <td>Enumerates all information available about the hosts mentioned in the maDMP.</td>
    </tr>
    <tr>
        <td colspan="4"><b>5c What methods or software tools are needed to access and use data?</b></td>
    </tr>
    <tr>
        <td>Indicate whether potential users need specific tools to access and (re-)use the data. Consider the sustainability of software needed for accessing the data.</td>
        <td><code>5-c-1</code></td>
        <td>A stripped down version of <code>5-a-1</code>, with focus on the distributed file formats. This is the best we can get with the maDMP since file formats indicate software/tools to be used to read the files.</td>
    </tr>
    <tr>
        <td>Indicate whether data will be shared via a repository, requests handled directly, or whether another mechanism will be used?</td>
        <td><code>5-a-1</code></td>
        <td>There is no dedicated field in the maDMP for this. However, <code>5-a-1</code> obtains data the requested information can inferred from.</td>
    </tr>
    <tr>
        <td colspan="4"><b>5d How will the application of a unique and persistent identifier (such as a Digital Object Identifier (DOI)) to each data set be ensured?</b></td>
    </tr>
    <tr>
        <td>Explain how the data might be re-used in other contexts. Persistent identifiers (PIDs) should be applied so that data can be reliably and efficiently located and referred to. PIDs also help to track citations and re-use.</td>
        <td><code>5-d-1</code></td>
        <td>This is a slightly modified version of <code>5-a-1</code>, with an emphasis on the employed PID system.</td>
    </tr>
    <tr>
        <td>Indicate whether a PID for the data will be pursued. Typically, a trustworthy, long-term repository will provide a persistent identifier.</td>
        <td><code>5-d-2</code></td>
        <td>Tests whether there exists a distribution with a host that specifies the use of a PID system.</td>
    </tr>
</tbody>
</table>

### Data Management Responsibilities and Resources

<table class="rtable">
<thead>
    <tr>
        <th>Requirement</th>
        <th>Covered In</th>
        <th>Remarks</th>
    </tr>
</thead>
<tbody>
    <tr>
        <td colspan="4"><b>6a Who (for example role, position, and institution) will be responsible for data management (i.e. the data steward)?</b></td>
    </tr>
    <tr>
        <td>Outline the roles and responsibilities for data management/ stewardship activities for example data capture, metadata production, data quality, storage and backup, data archiving, and data sharing. Name responsible individual(s) where possible.</td>
        <td><code>6-a-1</code>, <code>6-a-2</code></td>
        <td>The queries show all available information about the contact person and contributors.</td>
    </tr>
    <tr>
        <td>For collaborative projects, explain the co-ordination of data management responsibilities across partners</td>
        <td><code>6-a-2</code></td>
        <td>Depicts information about contributors defined by the maDMP.</td>
    </tr>
    <tr>
        <td>Indicate who is responsible for implementing the DMP, and for ensuring it is reviewed and, if necessary, revised.</td>
        <td>/</td>
        <td>Not explicitly covered by maDMP, but <code>6-a-1</code> and <code>6-a-2</code> give a good indicator of who might be resposible.</td>
    </tr>
    <tr>
        <td>Consider regular updates of the DMP.</td>
        <td>/</td>
        <td>Not covered by maDMP.</td>
    </tr>
    <tr>
        <td colspan="4"><b>6b What resources (for example financial and time) will be dedicated to data management and ensuring that data will be FAIR (Findable, Accessible, Interoperable, Re-usable)?</b></td>
    </tr>
    <tr>
        <td>Explain how the necessary resources (for example time) to prepare the data for sharing/preservation (data curation) have been costed in.</td>
        <td><code>6-b-1</code></td>
        <td>Not explicitly covered by maDMP; related information may be found by <code>6-b-1</code>.</td>
    </tr>
    <tr>
        <td>Carefully consider and justify any resources needed to deliver the data. These may include storage costs, hardware, staff time, costs of preparing data for deposit, and repository charges.</td>
        <td><code>6-b-1</code></td>
        <td>Lists everything related to costs that is captured by the maDMP.</td>
    </tr>
    <tr>
        <td>Indicate whether additional resources will be needed to prepare data for deposit or to meet any charges from data repositories. If yes, explain how much is needed and how such costs will be covered.</td>
        <td><code>6-b-2</code></td>
        <td>Specifies equipment needed or used to create or process the data.</td>
    </tr>
</tbody>
</table>

## Summary

The chapter [Covered Criteria](https://raffaelfoidl.github.io/maDMP-evaluation/0004.html) gave a brief overview of the
queries developed over the course of the project. Furthermore, it also contains assumptions that had to be made as well
as challenges we faced.

Overall, the Science Europe Evaluation Rubric defines 6 broad categories in its assessment guideline. The following
table gives of the spectrum we were able to cover with our queries.

<table class="rtable">
<thead>
  <tr>
    <th>Category</th>
    <th>Number Of Subitems</th>
    <th>Largely Covered Subitems</th>
    <th>Percentage</th>
  </tr>
</thead>
<tbody>
  <tr>
    <td>0 General Information</td>
    <td>1</td>
    <td>1</td>
    <td>100 %</td>
  </tr>
  <tr>
    <td>1 Data Description and Collection or Re-Use of Existing Data</td>
    <td>9</td>
    <td>0</td>
    <td>0 %</td>
  </tr>
  <tr>
    <td>2 Documentation and Data Quality</td>
    <td>11</td>
    <td>0</td>
    <td>0 %</td>
  </tr>
  <tr>
    <td>3 Storage and Backup During the Research Process</td>
    <td>6</td>
    <td>0</td>
    <td>0 %</td>
  </tr>
  <tr>
    <td>4 Legal and Ethical Requirements, Code of Conduct</td>
    <td>6</td>
    <td>0</td>
    <td>0 %</td>
  </tr>
  <tr>
    <td>5 Data Sharing and Long-Term Preservation</td>
    <td>12</td>
    <td>8</td>
    <td>67 %</td>
  </tr>
  <tr>
    <td>6 Data Management Responsibilities and Resources</td>
    <td>7</td>
    <td>5</td>
    <td>71 %</td>
  </tr>
</tbody>
<tfoot>
  <tr>
    <td>Sum</td>
    <td>51</td>
    <td>13</td>
    <td>25 %</td>
  </tr>
</tfoot>
</table>

## Assessment Report

In this section, we want to present an aggregated assessment of the maDMPs submitted to the aforementioned
[Zenodo community](https://zenodo.org/communities/dast-2021/). As it is possible to determine the respective authors
from the content of an maDMP, we want to clarify that we - of course - do not intend to disparage neither the efforts
that went into creating the documents nor the authors themselves by this assessment in any way. We merely utilized the
files as realistic test data since they stem from experiments with diverse topics - in order to gauge the utility of the
SPARQL queries developed during our project. The tables below form a summary of our attempts at evaluating the maDMPs.

The column(s) "Satisfaction Value" are numeric on a scale from zero to five. A value of five is equivalent
to a holistic fulfilment of the respective criterion, a value of zero either denotes that the criterion is "not satisfied"
or that the SPARQL queries are not able to extract the required pieces of information.

### 1.jsonld

<table class="rtable">
<thead>
  <tr>
    <th>Category</th>
    <th>Satisfaction Value</th>
    <th>Justification</th>
  </tr>
</thead>
<tbody>
  <tr>
    <td>0 General Information</td>
    <td>0</td>
    <td></td>
  </tr>
  <tr>
    <td>1 Data Description and Collection or Re-Use of Existing Data</td>
    <td>2</td>
    <td>The size of the produced/used data is provided. However, for two out of four distributions, the description is missing. Furthermore, the file formats of the produced data are not specified (in contrast to the reused data).</td>
  </tr>
  <tr>
    <td>2 Documentation and Data Quality</td>
    <td>2</td>
    <td>No information about metadata or versioning provided. Keywords are included for half of the defined datasets. Minimal information about naming conventions included, as well as some statements about quality assurance measures.</td>
  </tr>
  <tr>
    <td>3 Storage and Backup During the Research Process</td>
    <td>2</td>
    <td>maDMP does not have <code>host</code> elements defined, therefore some information is missing (backup type and frequency, availability). Good description of access restrictions. For most datasets, clear indication whether personal/sensitive data is stored provided.</td>
  </tr>
  <tr>
    <td>4 Legal and Ethical Requirements, Code of Conduct</td>
    <td>5</td>
    <td>There is no information about potential preservation considerations. Regarding licenses, the maDMP does contain helpful data. However, the SPARQL query is a little bit too strict and fails due to the missing host definition. Good description of access restrictions and sufficient declaration of ethical considerations.</td>
  </tr>
  <tr>
    <td>5 Data Sharing and Long-Term Preservation</td>
    <td>3</td>
    <td>maDMP does not have <code>host</code> elements defined, therefore a lot of important information is missing (PID system, backup strategies, URLs etc.). There are preservation statements in the original JSON file, but they cannot be queried from the JSON-LD due to the reason explained above. Regarding licenses (license, embargo, openness, sensitivity), the maDMP does contain helpful data. However, the SPARQL query is a little bit too strict and fails due to the missing host definition.</td>
  </tr>
  <tr>
    <td>6 Data Management Responsibilities and Resources</td>
    <td>1</td>
    <td>Contact person is defined, but no contributors and their roles. Costs (resources, equipment, staff expenses etc.) are not specified in the maDMP.</td>
  </tr>
</tbody>
<tfoot>
  <tr>
    <td>Average</td>
    <td>0/30</td>
    <td></td>
  </tr>
</tfoot>
</table>

### 2.jsonld

<table class="rtable">
<thead>
  <tr>
    <th>Category</th>
    <th>Satisfaction Value</th>
    <th>Justification</th>
  </tr>
</thead>
<tbody>
  <tr>
    <td>0 General Information</td>
    <td>0</td>
    <td></td>
  </tr>
  <tr>
    <td>1 Data Description and Collection or Re-Use of Existing Data</td>
    <td>4</td>
    <td>There is a clear description for each distribution. The file formats are specified (except for the source code). The size of the data is given as well (except for the source code).</td>
  </tr>
  <tr>
    <td>2 Documentation and Data Quality</td>
    <td>0</td>
    <td>No keywords specified. Information about metadata, data quality assurance and versioning is missing.</td>
  </tr>
  <tr>
    <td>3 Storage and Backup During the Research Process</td>
    <td>0</td>
    <td>maDMP does not have <code>host</code> elements defined, therefore some information is missing (backup type and frequency, availability). No description of security measures. For most datasets, no clear indication whether personal/sensitive data is stored provided.</td>
  </tr>
  <tr>
    <td>4 Legal and Ethical Requirements, Code of Conduct</td>
    <td>2</td>
    <td>There is no information about potential preservation considerations. Regarding licensing, the maDMP contains helpful data, but the SPARQL query is a little bit too strict and fails due to the missing host definition. No description of access restrictions. Sufficient declaration of ethical considerations.</td>
  </tr>
  <tr>
    <td>5 Data Sharing and Long-Term Preservation</td>
    <td>2</td>
    <td>maDMP does not have <code>host</code> elements defined, therefore a lot of important information is missing (PID system, backup strategies, URLs etc.). There are no preservation statements, therefore no information about research uses and preservation details (which data is kept, how to select etc.). Regarding licenses (license, embargo, openness, sensitivity), the maDMP does contain helpful data. However, the SPARQL query is a little bit too strict and fails due to the missing host definition.</td>
  </tr>
  <tr>
    <td>6 Data Management Responsibilities and Resources</td>
    <td>1</td>
    <td>Contact person is defined, but no contributors and their roles. Costs (resources, equipment, staff expenses etc.) are not specified in the maDMP.</td>
  </tr>
</tbody>
<tfoot>
  <tr>
    <td>Average</td>
    <td>0/30</td>
    <td></td>
  </tr>
</tfoot>
</table>

### 3.jsonld

<table class="rtable">
<thead>
  <tr>
    <th>Category</th>
    <th>Satisfaction Value</th>
    <th>Justification</th>
  </tr>
</thead>
<tbody>
  <tr>
    <td>0 General Information</td>
    <td>0</td>
    <td></td>
  </tr>
  <tr>
    <td>1 Data Description and Collection or Re-Use of Existing Data</td>
    <td>4</td>
    <td>There is a clear description for each distribution. The file formats are specified (except for the source code). The size of the data is given as well (except for the source code).</td>
  </tr>
  <tr>
    <td>2 Documentation and Data Quality</td>
    <td>0</td>
    <td>No keywords specified. Information about metadata and versioning is missing. Minimal information regarding data quality assurance provided.</td>
  </tr>
  <tr>
    <td>3 Storage and Backup During the Research Process</td>
    <td>2</td>
    <td>Extensive description of where the data is stored; however, information about backups and security measures is missing. Clear indication whether personal/sensitive data is stored.</td>
  </tr>
  <tr>
    <td>4 Legal and Ethical Requirements, Code of Conduct</td>
    <td>3</td>
    <td>There is no information about potential preservation considerations. Useful information about licensing provided. No description of access restrictions. Sufficient declaration of ethical considerations.</td>
  </tr>
  <tr>
    <td>5 Data Sharing and Long-Term Preservation</td>
    <td>4</td>
    <td>Substantial information about the data hosts (Zenodo); however, the corresponding queries are a little bit too strict and do not return anything. Good description of licensing/usage (sensitive data, embargo, openness). No explicit data preservation statement (missing data: retention period, data destruction, what data is kept).</td>
  </tr>
  <tr>
    <td>6 Data Management Responsibilities and Resources</td>
    <td>4</td>
    <td>Clear information about creator, contributors and their roles. Costs (resources, equipment, staff expenses etc.) are not specified in the maDMP.</td>
  </tr>
</tbody>
<tfoot>
  <tr>
    <td>Average</td>
    <td>0/30</td>
    <td></td>
  </tr>
</tfoot>
</table>

### 4.jsonld

<table class="rtable">
<thead>
  <tr>
    <th>Category</th>
    <th>Satisfaction Value</th>
    <th>Justification</th>
  </tr>
</thead>
<tbody>
  <tr>
    <td>0 General Information</td>
    <td>0</td>
    <td></td>
  </tr>
  <tr>
    <td>1 Data Description and Collection or Re-Use of Existing Data</td>
    <td>5</td>
    <td>There is a clear description for each distribution. The file formats are specified as well as the size of the data.</td>
  </tr>
  <tr>
    <td>2 Documentation and Data Quality</td>
    <td>5</td>
    <td>Significant keywords are provided as well as the metadata accompanying the data. Community metadata standards are used. Minimal information about versioning is available. Extensive description of data quality assurance measures.</td>
  </tr>
  <tr>
    <td>3 Storage and Backup During the Research Process</td>
    <td>5</td>
    <td>Extensive description of where the data is stored, the respective backup modalities and access regulations. Clear indication whether personal/sensitive data is stored.</td>
  </tr>
  <tr>
    <td>4 Legal and Ethical Requirements, Code of Conduct</td>
    <td>5</td>
    <td>Extensive documentation of access restrictions, ethical considerations and licensing. Information about data preservation with regard to sensitive or personal data included.</td>
  </tr>
  <tr>
    <td>5 Data Sharing and Long-Term Preservation</td>
    <td>5</td>
    <td>Substantial information about the data hosts (GitHub, Zenodo). Good description of licensing/usage (sensitive data, embargo, openness). There is a preservation statement in the original JSON file, but it cannot be queried from the JSON-LD due to the reason explained above.</td>
  </tr>
  <tr>
    <td>6 Data Management Responsibilities and Resources</td>
    <td>4</td>
    <td>Clear information about creator, contributors and their roles. Costs (resources, equipment, staff expenses etc.) are not specified in the maDMP.</td>
  </tr>
</tbody>
<tfoot>
  <tr>
    <td>Average</td>
    <td>0/30</td>
    <td></td>
  </tr>
</tfoot>
</table>

### 5.jsonld

<table class="rtable">
<thead>
  <tr>
    <th>Category</th>
    <th>Satisfaction Value</th>
    <th>Justification</th>
  </tr>
</thead>
<tbody>
  <tr>
    <td>0 General Information</td>
    <td>0</td>
    <td></td>
  </tr>
  <tr>
    <td>1 Data Description and Collection or Re-Use of Existing Data</td>
    <td>3</td>
    <td>There is a clear description for each distribution; the file formats are defined as well. However, the type of the dataset is not specified and the size of the produced/used data is missing.</td>
  </tr>
  <tr>
    <td>2 Documentation and Data Quality</td>
    <td>0</td>
    <td>Only few keywords specified. Information about metadata and versioning is missing. No information regarding data quality assurance provided.</td>
  </tr>
  <tr>
    <td>3 Storage and Backup During the Research Process</td>
    <td>1</td>
    <td>Minimal description of where the data is stored and the respective backup modalities (which the SPARQL queries fail to detect). No description of security measures. No indication whether personal/sensitive data is stored.</td>
  </tr>
  <tr>
    <td>4 Legal and Ethical Requirements, Code of Conduct</td>
    <td>2</td>
    <td>No mention of data preservation considerations and no description of access control mechanisms. Regarding licenses (license, embargo, openness, sensitivity), the maDMP does contain helpful data. However, the SPARQL query is a little bit too strict and fails due to the incomplete host definitions. Sufficient declaration of ethical considerations.</td>
  </tr>
  <tr>
    <td>5 Data Sharing and Long-Term Preservation</td>
    <td>4</td>
    <td>Almost complete information about the data hosts (GitHub, Zenodo); information about the PID system is missing. There is no preservation statement, therefore no information about research uses and preservation details (which data is kept, how to select etc.). Regarding licenses (license, embargo, openness, sensitivity), the maDMP does contain helpful data. However, the SPARQL query is a little bit too strict and fails due to the incomplete host definitions.</td>
  </tr>
  <tr>
    <td>6 Data Management Responsibilities and Resources</td>
    <td>5</td>
    <td>Extensive information about creator, contributors and their roles. Needed resources are defined. Financial costs are not specified in the maDMP.</td>
  </tr>
</tbody>
<tfoot>
  <tr>
    <td>Average</td>
    <td>0/30</td>
    <td></td>
  </tr>
</tfoot>
</table>

### 6.jsonld

<table class="rtable">
<thead>
  <tr>
    <th>Category</th>
    <th>Satisfaction Value</th>
    <th>Justification</th>
  </tr>
</thead>
<tbody>
  <tr>
    <td>0 General Information</td>
    <td>0</td>
    <td></td>
  </tr>
  <tr>
    <td>1 Data Description and Collection or Re-Use of Existing Data</td>
    <td>4</td>
    <td>There is a clear description for each distribution. The file formats are specified (except for the source code). The size of the data is given as well (except for the source code).</td>
  </tr>
  <tr>
    <td>2 Documentation and Data Quality</td>
    <td>0</td>
    <td>No keywords specified. Information about metadata and versioning is missing. Minimal information regarding data quality assurance provided.</td>
  </tr>
  <tr>
    <td>3 Storage and Backup During the Research Process</td>
    <td>1</td>
    <td>maDMP does not have <code>host</code> elements defined, therefore some information is missing (backup type and frequency, availability). No information about security measures provided. Clear indication whether personal/sensitive data is stored.</td>
  </tr>
  <tr>
    <td>4 Legal and Ethical Requirements, Code of Conduct</td>
    <td>2</td>
    <td>No mention of data preservation considerations and no description of access control mechanisms. Regarding licenses (license, embargo, openness, sensitivity), the maDMP does contain helpful data. However, the SPARQL query is a little bit too strict and fails due to the incomplete host definitions. Sufficient declaration of ethical considerations.</td>
  </tr>
  <tr>
    <td>5 Data Sharing and Long-Term Preservation</td>
    <td>2</td>
    <td>maDMP does not have host elements defined, therefore a lot of important information is missing (PID system, backup strategies, URLs etc.). There is no preservation statement, therefore no information about research uses and preservation details (which data is kept, how to select etc.). Regarding licenses (license, embargo, openness, sensitivity), the maDMP does contain helpful data. However, the SPARQL query is a little bit too strict and fails due to the missing host definition.</td>
  </tr>
  <tr>
    <td>6 Data Management Responsibilities and Resources</td>
    <td>1</td>
    <td>Contact person is defined, but no contributors and their roles. Costs (resources, equipment, staff expenses etc.) are not specified in the maDMP.</td>
  </tr>
</tbody>
<tfoot>
  <tr>
    <td>Average</td>
    <td>0/30</td>
    <td></td>
  </tr>
</tfoot>
</table>

### 7.jsonld

<table class="rtable">
<thead>
  <tr>
    <th>Category</th>
    <th>Satisfaction Value</th>
    <th>Justification</th>
  </tr>
</thead>
<tbody>
  <tr>
    <td>0 General Information</td>
    <td>0</td>
    <td></td>
  </tr>
  <tr>
    <td>1 Data Description and Collection or Re-Use of Existing Data</td>
    <td>5</td>
    <td>There is a clear description for each distribution. The file formats are specified (except for the source code). The size of the data is provided as well.</td>
  </tr>
  <tr>
    <td>2 Documentation and Data Quality</td>
    <td>0</td>
    <td>No keywords specified. Specified metadata standard is not a standard. Minimal information regarding data quality assurance provided.</td>
  </tr>
  <tr>
    <td>3 Storage and Backup During the Research Process</td>
    <td>2</td>
    <td>Extensive description of where the data is stored; however, information about backups and security measures is missing. Clear indication whether personal/sensitive data is stored.</td>
  </tr>
  <tr>
    <td>4 Legal and Ethical Requirements, Code of Conduct</td>
    <td>3</td>
    <td>There is no information about potential preservation considerations. Useful information about licensing provided. No description of access restrictions. Sufficient declaration of ethical considerations.</td>
  </tr>
  <tr>
    <td>5 Data Sharing and Long-Term Preservation</td>
    <td>4</td>
    <td>Substantial information about the data host (Zenodo) and licensing/usage (sensitive data, embargo, openness). No explicit data preservation statement (missing data: retention period, data destruction, what data is kept).</td>
  </tr>
  <tr>
    <td>6 Data Management Responsibilities and Resources</td>
    <td>4</td>
    <td>Clear information about creator, contributors and their roles. Costs (resources, equipment, staff expenses etc.) are not specified in the maDMP.</td>
  </tr>
</tbody>
<tfoot>
  <tr>
    <td>Average</td>
    <td>0/30</td>
    <td></td>
  </tr>
</tfoot>
</table>

### 8.jsonld

<table class="rtable">
<thead>
  <tr>
    <th>Category</th>
    <th>Satisfaction Value</th>
    <th>Justification</th>
  </tr>
</thead>
<tbody>
  <tr>
    <td>0 General Information</td>
    <td>0</td>
    <td></td>
  </tr>
  <tr>
    <td>1 Data Description and Collection or Re-Use of Existing Data</td>
    <td>3</td>
    <td>The file formats are defined. However, some distribution descriptions are missing as well as the size of some data.</td>
  </tr>
  <tr>
    <td>2 Documentation and Data Quality</td>
    <td>2</td>
    <td>Significant keywords are specified. Specified metadata standards are no standards. No information regarding data quality assurance provided, except for minimal information about versioning.</td>
  </tr>
  <tr>
    <td>3 Storage and Backup During the Research Process</td>
    <td>3</td>
    <td>Extensive description of where the data is stored; however, information about access restrictions is missing, as well as a description of the backup modalities for some hosts. Clear indication whether personal/sensitive data is stored for most specified datasets.</td>
  </tr>
  <tr>
    <td>4 Legal and Ethical Requirements, Code of Conduct</td>
    <td>3</td>
    <td>There is no information about potential preservation considerations and access restrictions. Extensive documentation regarding licensing. Sufficient declaration of ethical issues.</td>
  </tr>
  <tr>
    <td>5 Data Sharing and Long-Term Preservation</td>
    <td>5</td>
    <td>Extensive information about the data hosts (GitHub, Zenodo) and licensing/usage (sensitive data, embargo, openness). There are preservation statements in the original JSON file, but they cannot be queried from the JSON-LD due to the reason explained above.</td>
  </tr>
  <tr>
    <td>6 Data Management Responsibilities and Resources</td>
    <td>5</td>
    <td>Extensive information about creator, contributors and their roles. Extensive description of needed resources and costs.</td>
  </tr>
</tbody>
<tfoot>
  <tr>
    <td>Average</td>
    <td>0/30</td>
    <td></td>
  </tr>
</tfoot>
</table>

### 9.jsonld

<table class="rtable">
<thead>
  <tr>
    <th>Category</th>
    <th>Satisfaction Value</th>
    <th>Justification</th>
  </tr>
</thead>
<tbody>
  <tr>
    <td>0 General Information</td>
    <td>0</td>
    <td></td>
  </tr>
  <tr>
    <td>1 Data Description and Collection or Re-Use of Existing Data</td>
    <td>5</td>
    <td>There is a clear description for each distribution. The file formats are specified as well as the size of the data.</td>
  </tr>
  <tr>
    <td>2 Documentation and Data Quality</td>
    <td>4</td>
    <td>Keywords are provided as well as the metadata accompanying the data. Community metadata standards are used. Minimal information about versioning is available. Description of data quality assurance measurements missing.</td>
  </tr>
  <tr>
    <td>3 Storage and Backup During the Research Process</td>
    <td>4</td>
    <td>Extensive description of where the data is stored and access restrictions; however, information about backups is missing. Clear indication whether personal/sensitive data is stored.</td>
  </tr>
  <tr>
    <td>4 Legal and Ethical Requirements, Code of Conduct</td>
    <td>4</td>
    <td>Extensive documentation of access restrictions, ethical considerations and licensing. No information about data preservation with regard to sensitive or personal data included.</td>
  </tr>
  <tr>
    <td>5 Data Sharing and Long-Term Preservation</td>
    <td>4</td>
    <td>There is extensive information about the data hosts (GitHub, Zenodo, The World Bank) and licensing/usage (sensitive data, embargo, openness). No explicit data preservation statement (missing data: retention period, data destruction, what data is kept). No target audiences (foreseeable research uses).</td>
  </tr>
  <tr>
    <td>6 Data Management Responsibilities and Resources</td>
    <td>5</td>
    <td>Clear information about creator, contributors and their roles. Costs (resources, equipment, staff expenses etc.) are also specified in the maDMP.</td>
  </tr>
</tbody>
<tfoot>
  <tr>
    <td>Average</td>
    <td>0/30</td>
    <td></td>
  </tr>
</tfoot>
</table>

According to the results of the queries, this maDMP has a quite high amount of information required by the evaluation rubric, hinting at good quality.

### 10.jsonld

<table class="rtable">
<thead>
  <tr>
    <th>Category</th>
    <th>Satisfaction Value</th>
    <th>Justification</th>
  </tr>
</thead>
<tbody>
  <tr>
    <td>0 General Information</td>
    <td>0</td>
    <td></td>
  </tr>
  <tr>
    <td>1 Data Description and Collection or Re-Use of Existing Data</td>
    <td>4</td>
    <td>The file formats are specified, but not in the <a href="https://www.iana.org/assignments/media-types/media-types.xhtml">IANA media type</a> format. The size of the data is provided. However, the distribution descriptions are missing.</td>
  </tr>
  <tr>
    <td>2 Documentation and Data Quality</td>
    <td>2</td>
    <td>Significant keywords are specified. No information about metadata or versioning provided. Extensive documentation of naming conventions included.</td>
  </tr>
  <tr>
    <td>3 Storage and Backup During the Research Process</td>
    <td>1</td>
    <td>maDMP does not have <code>host</code> elements defined, therefore some information is missing (backup type and frequency, availability). No information about security measures provided. Clear indication whether personal/sensitive data is stored.</td>
  </tr>
  <tr>
    <td>4 Legal and Ethical Requirements, Code of Conduct</td>
    <td>2</td>
    <td>There is no information about potential preservation considerations and access control. Regarding licensing, the maDMP does contain helpful data. However, the SPARQL query is a little bit too strict and fails due to the missing <code>host</code> definition. Sufficient declaration of ethical considerations.</td>
  </tr>
  <tr>
    <td>5 Data Sharing and Long-Term Preservation</td>
    <td>3</td>
    <td>maDMP does not have <code>host</code> elements defined, therefore a lot of important information is missing (PID system, backup strategies, URLs etc.). There is a preservation statement in the original JSON file, but it cannot be queried from the JSON-LD due to the reason explained above. Regarding licenses (license, embargo, openness, sensitivity), the maDMP does contain helpful data. However, the SPARQL query is a little bit too strict and fails due to the missing <code>host</code> definition.</td>
  </tr>
  <tr>
    <td>6 Data Management Responsibilities and Resources</td>
    <td>4</td>
    <td>Creator/contact person is defined, but no contributors and their roles. Costs of storing and backing up the data are also specified in the maDMP.</td>
  </tr>
</tbody>
<tfoot>
  <tr>
    <td>Average</td>
    <td>0/30</td>
    <td></td>
  </tr>
</tfoot>
</table>

Due to the missing `host` definition, a lot of information could not be extracted with the queries. Apart from that,
the maDMP did provide a decent informational value.

### 11.jsonld

<table class="rtable">
<thead>
  <tr>
    <th>Category</th>
    <th>Satisfaction Value</th>
    <th>Justification</th>
  </tr>
</thead>
<tbody>
  <tr>
    <td>0 General Information</td>
    <td>0</td>
    <td></td>
  </tr>
  <tr>
    <td>1 Data Description and Collection or Re-Use of Existing Data</td>
    <td>4</td>
    <td>There is a clear description for each distribution. The file formats are specified (except for the source code). The size of the data is given as well (except for the source code).</td>
  </tr>
  <tr>
    <td>2 Documentation and Data Quality</td>
    <td>0</td>
    <td>No keywords specified. Original JSON file contains <code>documentation_and_metadata</code> element where some information about metadata is provided; this field is, however, not part of the RDA CMP Common Standard and can therefore not be considered. No information about versioning. Minimal statement regarding data quality assurance.</td>
  </tr>
  <tr>
    <td>3 Storage and Backup During the Research Process</td>
    <td>1</td>
    <td>maDMP does not have <code>host</code> elements defined, therefore some information is missing (backup type and frequency, availability). No information about security measures provided. Clear indication whether personal/sensitive data is stored.</td>
  </tr>
  <tr>
    <td>4 Legal and Ethical Requirements, Code of Conduct</td>
    <td>2</td>
    <td>There is no information about potential preservation considerations and access control. Regarding licensing, the maDMP does contain helpful data. However, the SPARQL query is a little bit too strict and fails due to the missing <code>host</code> definition. Sufficient declaration of ethical considerations.</td>
  </tr>
  <tr>
    <td>5 Data Sharing and Long-Term Preservation</td>
    <td>3</td>
    <td>maDMP does not have <code>host</code> elements defined, therefore a lot of important information is missing (PID system, backup strategies, URLs etc.). There is no preservation statement, therefore no information about research uses and preservation details (which data is kept, how to select etc.). Regarding licenses (license, embargo, openness, sensitivity), the maDMP does contain helpful data. However, the SPARQL query is a little bit too strict and fails due to the missing <code>host</code> definition.</td>
  </tr>
  <tr>
    <td>6 Data Management Responsibilities and Resources</td>
    <td>4</td>
    <td>Clear information about creator, contributors and their roles. Costs (resources, equipment, staff expenses etc.) are not specified in the maDMP.</td>
  </tr>
</tbody>
<tfoot>
  <tr>
    <td>Average</td>
    <td>7/30</td>
    <td></td>
  </tr>
</tfoot>
</table>

Since the JSON-LD maDMP was surprisingly short in content, a manual look into the source maDMP revealed that there are
a lot of fields that are not actually part of the RDA-DMP Common Standard and thus, not queryable with our approach.

### 12.jsonld

<table class="rtable">
<thead>
  <tr>
    <th>Category</th>
    <th>Satisfaction Value</th>
    <th>Justification</th>
  </tr>
</thead>
<tbody>
  <tr>
    <td>0 General Information</td>
    <td>0</td>
    <td></td>
  </tr>
  <tr>
    <td>1 Data Description and Collection or Re-Use of Existing Data</td>
    <td>5</td>
    <td>There is a clear description for each distribution. The file formats are specified as well as the size of the data.</td>
  </tr>
  <tr>
    <td>2 Documentation and Data Quality</td>
    <td>5</td>
    <td>Significant keywords are provided as well as the metadata accompanying the data. Community metadata standards are used. Minimal information about versioning is available. Extensive description of data quality assurance measures and folder structures.</td>
  </tr>
  <tr>
    <td>3 Storage and Backup During the Research Process</td>
    <td>5</td>
    <td>Extensive description of where the data is stored, the respective backup modalities and access regulations. Clear indication whether personal/sensitive data is stored. Data is stored at four locations.</td>
  </tr>
  <tr>
    <td>4 Legal and Ethical Requirements, Code of Conduct</td>
    <td>5</td>
    <td>Extensive documentation regarding licensing. Good description of access restrictions and sufficient declaration of ethical considerations.</td>
  </tr>
  <tr>
    <td>5 Data Sharing and Long-Term Preservation</td>
    <td>4</td>
    <td>Substantial information about the data hosts (GitHub, Zenodo) and licensing/usage (sensitive data, embargo, openness). No explicit data preservation statement (missing data: retention period, data destruction, what data is kept). No target audiences (foreseeable research uses).</td>
  </tr>
  <tr>
    <td>6 Data Management Responsibilities and Resources</td>
    <td>4</td>
    <td>Clear information about creator, contributors and their roles. Costs (resources, equipment, staff expenses etc.) are not specified in the maDMP.</td>
  </tr>
</tbody>
<tfoot>
  <tr>
    <td>Average</td>
    <td>0/30</td>
    <td></td>
  </tr>
</tfoot>
</table>

Overall, this maDMP could be assessed decently well. Missing aspects were mostly due to the maDMP schema, a preservation
statement would have provided some more information.

### Aggregation, Conclusion

<table class="rtable">
<thead>
  <tr>
    <th>Category</th>
    <th>Satisfaction Value</th>
    <th>Justification</th>
  </tr>
</thead>
<tbody>
  <tr>
    <td>0 General Information</td>
    <td>0</td>
    <td></td>
  </tr>
  <tr>
    <td>1 Data Description and Collection or Re-Use of Existing Data</td>
    <td>0</td>
    <td></td>
  </tr>
  <tr>
    <td>2 Documentation and Data Quality</td>
    <td>0</td>
    <td></td>
  </tr>
  <tr>
    <td>3 Storage and Backup During the Research Process</td>
    <td>0</td>
    <td></td>
  </tr>
  <tr>
    <td>4 Legal and Ethical Requirements, Code of Conduct</td>
    <td>0</td>
    <td></td>
  </tr>
  <tr>
    <td>5 Data Sharing and Long-Term Preservation</td>
    <td>0</td>
    <td></td>
  </tr>
  <tr>
    <td>6 Data Management Responsibilities and Resources</td>
    <td>0</td>
    <td></td>
  </tr>
</tbody>
<tfoot>
  <tr>
    <td>Average</td>
    <td>0/360</td>
    <td></td>
  </tr>
</tfoot>
</table>

Common pitfalls of maDMPs, where are queries lacking and why, ... 

As already mentioned in the introduction to this section, some queries could be made more tolerant against not having
defined optional schema elements. This would improve the results in gauging maDMPs. 

## License

MIT (see [LICENSE file](https://github.com/raffaelfoidl/maDMP-evaluation/blob/main/LICENSE))