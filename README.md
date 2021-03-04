# SimpleSearch Coding Exercise summary

## How to build
Just type

    > sbt test

to compile and run tests.

## How to run
Just as required in the specification ([Scala Developer Coding Exercise](https://gist.github.com/rockerrecruit/236f7f53f055253e0b71695af7c81ed8)):

    > sbt
    > run <folder_with_files>


## Implementation notes

* My choice of 
  
    > What constitutes a word?
 
    is: the `ISO-8859-1` encoded string that matches `"([A-Za-z0-9]+)"` and is at least two characters long.
  
* My choice of 
    
    > What constitutes two words being equal (and matching)?
 
    is: both are words in the above meaning and can differ in case-sensitivity.

* The
    
    > The rank must be 100% if a file contains all the words

    constraint seems to limit reasonable ranking logic - the full match does depend only on presence of words in the file.

