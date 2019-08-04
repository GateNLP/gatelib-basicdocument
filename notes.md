# Design and implementation notes

General design ideas:
* make document text immutable. Need to construct another document with the desired 
  text and merge the annotations to actually change the text
* Make merging easy: doc1.mergeDocument(doc2, ADDORUPDATE) 
* Also for annotation sets doc1.mergeAnnotationSet(as, ADDORUPDATE)
* Also for deleting: doc1.mergeDocument(doc2, DELETE) deletes all annotations in doc1 which are in doc2
  (rethink!)
* ADDORUPDATE will just add or replace whatever is new, ADDORREPLACE will add a new or replace an existing
  annotation(by id) including all features
* A document can know that it represents a span, so all annotations are from that span 
  Mergin the annotations will adjust the offsets to the span of the target
* An annotation set inherits and knows the span
* Spans are immutable, like text
* Text is optional (may be null) 
* Can a span be optional? probably not.


## Modes of update 

Things one may want to do:
* for a given set of annotations, set all the features in the feature map
* add a bunch of new annotations.
* for a given set of annotations, remote all the features in the feature map
* remove a bunch of existing annotations
* text cannot be updated
