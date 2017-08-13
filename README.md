![CatFuse Logo](Logo.png)

A statistical tool for merging item response categories with few observations.

CatFuse is a free console tool developed in cooperation with *Viola Gauß* which automatically merges item response categories (e.g., for item responses retrieved from a survey) that have too few observations (i.e., a relative frequency below a given threshold).

As input, the tool takes a file with tab-separated data without column headlines (as can be usually exported/imported from/to common statistical software) and outputs a new file in the same format in which the corresponding categories are fused. This is done by shifting response categories with a relative frequency below a given threshold towards the middle of the item's response scale. Furthermore, CatFuse outputs in detail which changes have been made to the data to make the processing transparent to the user (this output is currently only available in German). 

In the output file, items are numbered from left to right according to the order of the columns in the input data file, starting with 0. Missing item values are assumed to have the value 999. In the output which explains the changes made by CatFuse, the relative frequencies of each item's response category are annotated in `[` and `]`; category mergings are indicated by `-->` and `<--` (according to the direction of the merging), followed by the number of values that have been merged from the one category to the other.

CatFuse automates a process which can be a lot of work when done manually as a preprocessing of statistical data. In most cases, the resulting data file can be taken without any further manual processing; except for extremely unbalanced distributions: In these cases, some additional manual work might be required on some response categories of the resulting data file (which item's resulting categories do need some addtional work can be easily seen from the provided output explaining the changes that have been made by CatFuse).

# CatFuse is called as follows:
`java -jar catfuse.jar FILENAME THRESHOLD`

where FILENAME is the name of the data file (optionally preceded by a path) and THRESHOLD is the decimal number indicating the relative frequency below which a category is merged with its next neighbour towards the middle of the underlying response scale. 

# Example: 
`java -jar catfuse.jar mydata.dat 0.05`

This will produce a file *mydata_fused.dat* in the same directory where all categories with a relative frequency below 0.05 will be merged with the next neighbour towards the middle of the underlying response scale. 

# Download: 
To download the latest released version, see [releases](https://github.com/dapel/CatFuse/releases).
