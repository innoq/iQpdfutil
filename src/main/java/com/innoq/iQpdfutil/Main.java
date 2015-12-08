package com.innoq.iQpdfutil;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This class reads in all pds, concatenates them to one,
 * adds pagenumbers to all but the first pages, and stores
 * the resulting pdf as the output file.
 *
 * Requires at least 2 arguments: The last argument specifies
 * the output file while all others provide the input files
 * in the order of concatenation.
 *
 * @author ghadir
 * @since 08/12/15 12:30
 */
public class Main {

    public static void main(String[] args) throws Exception {


        try {
            Args arguments = new Args(args);

            System.out.println( arguments );

            File tempFile = File.createTempFile( "temp___", ".pdf" , new File( "./" ) );

            try ( OutputStream os = new FileOutputStream( tempFile ) ) {

                try ( Readers readers = new Readers( arguments.inputFiles ) ) {

                    concatFiles( readers, os );
                }
            }

            try (OutputStream os = new FileOutputStream( arguments.outputFile ) ) {

                numberPages( new PdfReader( tempFile.getName() ), os );
            }

            Files.delete( tempFile.toPath() );

        } catch (IllegalArgumentException e) {
            System.err.println( e.getMessage() );
        }
    }


    public static void concatFiles(Readers readers, OutputStream os) throws DocumentException, IOException {
        Document document = new Document();

        PdfCopy copy = new PdfCopy(document, os );
        document.open();

        for ( PdfReader reader : readers ) {

            if ( copyPages( reader, copy ) ) {
                System.out.println("Main.concatFiles -- added empty page!");
            }
        }

        document.close();
    }

    private static boolean copyPages(PdfReader reader, PdfCopy copy) throws IOException, BadPdfFormatException {
        int nrOfPagesInCurrentFile = reader.getNumberOfPages();
        for (int page = 0; page < nrOfPagesInCurrentFile; ) {
            copy.addPage(copy.getImportedPage(reader, ++page));
        }

        final boolean isEmptyPageRequired = nrOfPagesInCurrentFile % 2 == 1;
        if (isEmptyPageRequired) {
            copy.addPage(reader.getPageSize( nrOfPagesInCurrentFile ), 0);
        }

        return isEmptyPageRequired;
    }

    private static void numberPages(PdfReader reader, OutputStream os ) throws IOException, DocumentException {

        PdfStamper stamper = new PdfStamper(reader, os);

        try {
            int n = reader.getNumberOfPages();

            PdfContentByte cbq;
            Font headerFont = new Font(Font.FontFamily.COURIER, 12, Font.NORMAL);
            for (int i = 2; i <= n; i++) {
                cbq = stamper.getOverContent(i);
                ColumnText ct = new ColumnText(cbq);
                ct.setSimpleColumn(250, 10, 450, 30, 1, Element.ALIGN_CENTER);
                ct.addElement(new Paragraph("- " + i + " -", headerFont));
                ct.go();
            }
        } finally {
            try {
                stamper.close();
            } catch (Exception e ) {
                e.printStackTrace();
            }
        }

    }


    private static class Readers implements AutoCloseable, Iterable<PdfReader> {

        final List<PdfReader> inputFiles;

        public Readers(List<String> filenames) {

            this.inputFiles = filenames.stream().map(this::createPdfReader).collect(Collectors.toList());
        }

        @Override
        public void close() throws Exception {

        }


        public PdfReader createPdfReader( String filename ) {
            try {
                return new PdfReader(filename);
            } catch (IOException e) {
                throw new RuntimeException( e );
            }
        }

        @Override
        public Iterator<PdfReader> iterator() {
            return inputFiles.iterator();
        }
    }


    private static class Args {

        final List<String> inputFiles;
        final String outputFile;

        public Args(String[] args) {

            if (args.length < 2) {
                throw new IllegalArgumentException( "Required arguments: inputFile+ outputFile" );
            }

            this.inputFiles = Arrays.asList( args ).subList(0, args.length -1);
            this.outputFile = args[args.length -1];

            Optional<String> missingFiles = inputFiles.stream()
                    .map(File::new)
                    .filter( f-> ! (f.exists() && f.isFile()) )
                    .map(File::getName)
                    .reduce( (a,b) -> a + ", " + b);

            if ( missingFiles.isPresent() ) {
                throw new IllegalArgumentException( "These files must exist: " + missingFiles.get() );
            }
        }


        @Override
        public String toString() {
            return "Args{" +
                    "inputFiles=" + inputFiles +
                    ", outputFile='" + outputFile + '\'' +
                    '}';
        }
    }
}
