/**
 * iQpdfutil -- concat pdfs and add page numbers
 * <p>
 * Copyright (C) 2016 innoQ
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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

            File tempFile = File.createTempFile( "temp___", ".pdf", new File( "./" ) );

            concatFiles(arguments.inputFiles, tempFile);
            copyPDFandAddPageNumbers(arguments.outputFile, tempFile);

            Files.delete( tempFile.toPath() );

        } catch (IllegalArgumentException e) {
            System.err.println( e.getMessage() );
        }
    }

    /**
     *
     * */
    private static void copyPDFandAddPageNumbers(String pdfFilename, File outputFile) throws IOException, DocumentException {
        try ( OutputStream os = new FileOutputStream( pdfFilename ) ) {
            numberPages( new PdfReader( outputFile.getName() ), os );
        }
    }

    private static void concatFiles(
            List<String> inputFilenames, File outputFile)
            throws IOException, DocumentException
    {
        try ( OutputStream os = new FileOutputStream( outputFile ) ) {
            try ( Readers readers = new Readers( inputFilenames ) ) {
                concatPDFs( readers, os );
            }
        }
    }


    public static void concatPDFs(Readers readers, OutputStream os) throws DocumentException, IOException {
        Document document = new Document();

        PdfCopy copy = new PdfCopy(document, os);
        document.open();

        for ( PdfReader reader : readers ) {
            copyPages( reader, copy );
        }

        document.close();
    }


    private static boolean copyPages(PdfReader reader, PdfCopy copy) throws IOException, BadPdfFormatException {
        int nrOfPagesInCurrentFile = reader.getNumberOfPages();
        for (int page = 0; page < nrOfPagesInCurrentFile; ) {
            copy.addPage( copy.getImportedPage(reader, ++page) );
        }

        final boolean isEmptyPageRequired = nrOfPagesInCurrentFile % 2 == 1;
        if (isEmptyPageRequired) {
            copy.addPage(reader.getPageSize( nrOfPagesInCurrentFile ), 0);
        }

        return isEmptyPageRequired;
    }

    private static void numberPages(PdfReader reader, OutputStream os) throws IOException, DocumentException {

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
            } catch (Exception e) {
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
        public void close() {
            inputFiles.forEach(PdfReader::close);
        }


        public PdfReader createPdfReader( String filename ) {
            try {
                return new PdfReader(filename);
            } catch (IOException e) {
                throw new RuntimeException(e);
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

            this.inputFiles = Arrays.asList(args).subList(0, args.length - 1);
            this.outputFile = args[args.length - 1];

            Optional<String> missingFiles = inputFiles.stream()
                    .map(File::new)
                    .filter(f -> !(f.exists() && f.isFile()))
                    .map(File::getName)
                    .reduce((a, b) -> a + ", " + b);

            if (missingFiles.isPresent()) {
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
