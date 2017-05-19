# WikiSearchEngine
This project consists of two parts. One of them is to build inverted index on dumps of russian wikipedia. The second part to search for documents


The inverted index contains the ids of Wikipedia articles, term freequency and positions of terms in document. With the help of this index it is possible to perform boolean search, ranked search, search for quotes and search by headings. Also this index is compressed using the variable byte code and we can perform jumping on the index for intersection operations.
