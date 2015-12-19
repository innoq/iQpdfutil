# iQpdfutil

Utility to concatenate pdfs, inserting empty pages where necessary, and adding page numbers


## Getting Started

Fetch the latest release from: https://github.com/innoq/iQpdfutil/releases/latest

1. unzip it to a folder
2. cd into extracted folder iq-pdfutil-VERSION/
3. Integrate it into your environment as you like. E.g. add the new folder to your
path, or build a symbolic link from somewhere to the shell-script. E.g.: 

  sudo ln -s /your-installation-directory/iq-pdfutil-VERSION/iQpdfutil /usr/local/bin/iQpdfutil

Build a distribution yourself: 

1. clone the repo
2. run mvn install
3. run mvn assembly:single
4. take the built distribution from target/iq-pdfutil-VERSION-distribution.zip


## Using the pdf-utility

Invoke the script with at least one pdf-file and provide an output-filename.

<pre>

$ iQpdfutil in1 in2 in3 ... output.pdf

</pre>

Or, one use-case I often have:

<pre>

$ iQpdfutil "*.pdf" /sometargetpath/complete_with_pagenumbers.pdf

</pre>

## Releases

Find the latest release here: https://github.com/innoq/iQpdfutil/releases/latest

## License

This tool relies heavily on
<a href="http://api.itextpdf.com/itext/com/itextpdf/text/pdf/package-summary.html">iText</a>
and is licensed under <a href="http://www.gnu.org/licenses/agpl-3.0.en.html">APGL v3</a>.






