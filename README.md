# gatelib-basicdocument

This library implements the conversion of GATE documents from and to a very simple
representation which corresponds to the way documents are represented in the 
Python gatenlp package and which makes it easy to serialise/deserialise to/from
JSON and other formats for storage and communication. 

It also implements methods for updating GATE documents with (parts of) information from
basicdocument instances or with the changes from a changelog instance created with the 
Python gatenlp package.
