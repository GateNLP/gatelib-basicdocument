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
* for a given set of existing annotations, set one or more features in the feature map
* (for a given annotation, update offset and/or type: for this we need same modes as for deleting)
* add a bunch of new annotations to an annotation set 
* for a given set of annotations, remove all the features in the feature map
* remove annotations from an annotation set:
  * by matching annotation id
  * by matching type and span
  * by subsumption: type and span match and all features that exist in the comparison feature set are present
    and have the same value in the target annotation
* text cannot be updated
* in theory we could add "update hints" to each of the data structures represented in our json document, e.g.
  an annotation could have an additional update hint field which tells how the annotation should get used when 
  merging with another document
