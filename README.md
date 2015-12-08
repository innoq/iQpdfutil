# iQpdfutil
Utility to concatenate pdfs, inserting empty pages where necessary, and adding page numbers


## getting started


If there is no distribution:

1. clone the repo
2. run mvn assembly:assembly
3. take the built distribution from target/iq-pdfutil-VERSION-distribution.zip


If you've got a distribution:

1. unzip to a folder
2. cd into extracted folder iq-pdfutil-VERSION/
3. chmod 755 iQpdfutil.sh

Optional:

Integrate it into your environment as you like. E.g. add the new folder to your
path, or build a symbolic link from somewhere to the shell-script.

And then ...

## usage

Invoke the script with at least one pdf-file and provide an output-filename:


<pre>

$ iQpdfutil.sh in1 in2 in3 ... output.pdf

</pre>

Or, one use-case I often have:

$ iQpdfutil.sh "*.pdf" /sometargetpath/complete_with_pagenumbers.pdf





