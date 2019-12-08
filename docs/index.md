# gatelib-basicdocument

A library that supports the "basic document" representation of GATE documents:
* similar to the representation used in the Python gatenlp library (https://github.com/GateNLP/python-gatenlp)
* Easy to use for serialisation over the wire or for storage 
  * storage: https://github.com/GateNLP/gateplugin-Format_Bdoc
  * over the wire: exchanging documents with Python gatenlp in the Python plugin (https://github.com/GateNLP/gateplugin-python)
* aware of the offset changes between Java (UTF16 code units) and (Python, other)  Unicode code points
