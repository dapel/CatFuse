![CatFuse Logo](Logo.png)

A statistical tool for merging item response categories with few observations.

CatFuse is a console tool developed in cooperation with *Viola Gauß* which automatically merges item response categories (e.g., for item responses retrieved from a survey) that have too few observations (i.e., a relative frequency below a given threshold).

As input, the tool takes a file with tab-separated data without column headlines (as can be usually exported/imported from/to common statistical software) and outputs a new file in the same format where the corresponding categories are fused. This is done by shifting item categories with a relative frequency below a given threshold towards the middle of the category’s scale. Furthermore, CatFuse outputs in detail what changes have been made to the data to make the processing transparent to the user (this output is currently only available in German). 

Items are numbered from left to right according to the order of the columns in the data file, starting with 0. Item values are taken as provided in the data file; missing item values are assumed to have the value 999. In the output which explains the changes made by CatFuse, the relative frequencies of every item value are annotated in `[` and `]`; category merges are indicated by `->` and `<-` (according to the direction of the merge), followed by the number of values that have been merged from the one category to the other.

CatFuse automates a process which can be a lot of work when done manually as a preprocessing to statistical data. In most cases, the resulting data file can be taken as is; except for extremely unbalanced distributions: In these cases, some additional manual work may be required on some categories of the resulting data file (which of the resulting categories do need some addtional work can be seen easily from the provided output explaining the changes that have been made by CatFuse).

# CatFuse is called as follows:
`java -jar catfuse.jar FILENAME THRESHOLD`

where FILENAME is the name of the data file (optionally preceded by a path) and THRESHOLD is decimal number indicating the relative frequency below which category is merged with its next neighbor towards the middle of the underlying scale. 

# Example: 
`java -jar catfuse.jar mydata.dat 0.05`

This will produce a file *mydata_fused.dat* in the same directory where all categories with a relative frequency below 0.05 will be merged with the next neighbor towards the middle of the underlying scale. 

# Download: 
To download the latest released version, see [releases](https://github.com/dapel/CatFuse/releases).
