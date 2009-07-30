/* Copyright (C) 2004   Versant Inc.   http://www.db4o.com */

package com.yetac.doctor.writers;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.lowagie.text.Annotation;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfDestination;
import com.lowagie.text.pdf.PdfOutline;
import com.lowagie.text.pdf.PdfWriter;
import com.yetac.doctor.cmd.Anchor;
import com.yetac.doctor.cmd.Bold;
import com.yetac.doctor.cmd.Code;
import com.yetac.doctor.cmd.Graphic;
import com.yetac.doctor.cmd.Italic;
import com.yetac.doctor.cmd.Link;
import com.yetac.doctor.cmd.NewPage;
import com.yetac.doctor.cmd.Outline;
import com.yetac.doctor.cmd.OutputConsole;
import com.yetac.doctor.cmd.Source;
import com.yetac.doctor.workers.DocsFile;
import com.yetac.doctor.workers.Files;

public class PDFWriter extends AbstractWriter {
    
    private PdfWriter writer;
    private Document  document;

    private Font      invisibleFont;

    private BaseFont  baseFont;
    private Font      boldFont;
    private Font      defaultFont;
    private Font      italicFont;
    private Font      linkFont;
    private Font      h1Font;
    private Font      h2Font;
    private Font      sourceFont;
    private Font      smallFont;

    PdfOutline[]      outlineNodes;
    
    static final String      PDF_WHITESPACE  = " ";
    static final String      PDF_TAB = multiple(PDF_WHITESPACE, TAB_WHITESPACES);

    public void start(Files _files) throws Exception {
        super.start(_files);
        files.task.setVariable("pdf", new Boolean(true));

        document = new Document();
        writer = PdfWriter.getInstance(document, new FileOutputStream(path()));
        writer.setViewerPreferences(PdfWriter.PageModeUseOutlines);
        
        outlineNodes = new PdfOutline[10];
        
        installRunner();

        document.addAuthor(files.task.author());
        document.addCreationDate();
        document.addCreator(files.task.creator());
        document.addTitle(files.task.title());
        document.addProducer();
        document.open();

        PdfContentByte cb = writer.getDirectContent();
        outlineNodes[0] = cb.getRootOutline();
        
        String baseFontPath = files.task.getPdfBaseFont();

        invisibleFont = FontFactory.getFont(FontFactory.COURIER, 1,
            Font.NORMAL, Color.WHITE);
        try {
            baseFont = BaseFont.createFont(baseFontPath, BaseFont.IDENTITY_H,
                BaseFont.EMBEDDED);
        } catch (Exception e) {
            files.task.log("Font for PDF generation not available: "
                + baseFontPath);
            files.task.log("Supply a valid PDF_BASE_FONT in Configuration.java");
        }
        defaultFont = new Font(baseFont, 10, Font.NORMAL, Color.BLACK);
        boldFont = new Font(baseFont, 10, Font.BOLD, Color.BLACK);
        italicFont = new Font(baseFont, 10, Font.ITALIC, Color.BLACK);
        linkFont = new Font(baseFont, 10, Font.NORMAL, Color.BLUE);
        h1Font = new Font(baseFont, 12, Font.BOLD, Color.BLACK);
        h2Font = new Font(baseFont, 10, Font.BOLD, Color.BLACK);
        smallFont = new Font(baseFont, 6, Font.BOLD, Color.BLACK);
        sourceFont = new Font(Font.COURIER, 10, Font.NORMAL, Color.BLACK);
        
		//PdfTemplate template = cb.createTemplate(600, 38);
        try {
        	Image jpg = Image.getInstance(_files.task.inputImages() + "/db4objects.gif");
        	jpg.setAbsolutePosition(430, 750);
        	jpg.setAnnotation(new Annotation(0, 0, 0, 0, "http://www.db4o.com"));
        	document.add(jpg);
        	//template.addImage(jpg);
        } catch (MalformedURLException e) {
        	_files.task.log("db4objects logo could not be found: " + e.toString());
        } catch (IOException e) {
        	_files.task.log("db4objects logo could not be found: " + e.toString());
        } catch (DocumentException e) {
        	_files.task.log("db4objects logo could not be added");
        }
        // unfortunately the next line does not work with the old version of iText
		// template.setAction(new PdfAction("http://www.db4o.com"), 380, 750, 570, 795);
		//cb.addTemplate(template, 0, 750);
        
        Paragraph para = new Paragraph("www.db4o.com", smallFont);
        HeaderFooter footer = new HeaderFooter(para, false);
        footer.setAlignment(1);
        footer.setBorder(Rectangle.NO_BORDER);
        document.setFooter(footer);
    }

    public void setSource(DocsFile source) throws Exception {
        if (source != null) {
            for (int i = 0; i < 3; i++) {
                document.add(new Paragraph(" ", defaultFont));
            }
        }
        super.setSource(source);
    }

    public void write(Anchor command) throws Exception {
        String str = new String(command.parameter);
        Chunk chunk = new Chunk(".", invisibleFont);
        com.lowagie.text.Anchor anchor = new com.lowagie.text.Anchor(chunk);
        anchor.setName(str);
        document.add(anchor);
    }

    public void write(Bold command) throws Exception {
        document.add(new Chunk(new String(command.text), boldFont));
    }
    
    public void write(Italic command) throws Exception{
        document.add(new Chunk(new String(command.text), italicFont));
    }

    public void write(Link command) throws Exception {
        com.lowagie.text.Anchor anchor = new com.lowagie.text.Anchor(new Chunk(
            new String(command.text), linkFont));
        if (command.external()) {
            anchor.setReference(new String(command.parameter));
        } else {
            anchor.setReference("#" + new String(command.parameter));
        }
        document.add(anchor);
    }

    public void write(Graphic command) throws Exception {
        Image image = Image
            .getInstance(new URL("file:" + command.sourcePath()));
        document.add(image);
    }

    public void write(NewPage command) throws Exception {
        document.newPage();
    }

    public void write(Outline command) throws Exception {
        if (!firstPage) {
            while (outlineLevel < command.level) {
                outlineLevel++;
            }
            while (outlineLevel > command.level) {
                outlineNodes[outlineLevel] = null;
                outlineNumbers[outlineLevel--] = 0;
            }
            outlineNumbers[outlineLevel]++;
        }

        String numbersAndText = outlineNumberString()
            + new String(command.parameter);
        PdfDestination dest = new PdfDestination(PdfDestination.FIT);

        for (int i = outlineLevel; i >= 0; i--) {
            if (outlineNodes[i] != null) {
                outlineNodes[outlineLevel + 1] = new PdfOutline(
                    outlineNodes[i], dest, numbersAndText);
                break;
            }
        }

        Font outLineFont = outlineLevel == 0 ? h1Font : h2Font;

        document.add(new Chunk(numbersAndText, outLineFont));
    }

    public void write(OutputConsole command) throws Exception {
        writeOutputBlock(command.text);
    }

    private void writeOutputBlock(String text) throws BadElementException, DocumentException {
        Table table = new Table(1, 1);
        Phrase ph=new Phrase(new Chunk(new String("OUTPUT:\n"), boldFont));
        ph.add(new Chunk(new String(text), sourceFont));
        Color bc = new Color(0xA0, 0xA0, 0xA0);
        
        table.addCell(ph);
        table.setBorderColor(bc);
        table.setBorderWidth(10);
        table.setBackgroundColor(bc);
        document.add(table);
    }

    private void writeSourceCodeBlock(String code,String methodName) throws Exception {
        code = convert(code);
        Table table = new Table(1, 1);
        Chunk ch = new Chunk(new String(code), sourceFont);
        Color c = new Color(245, 245, 245);
        ch.setBackground(c);
        table.addCell(new Phrase(ch));
        table.setBorderColor(c);
        table.setBorderWidth(10);
        table.setBackgroundColor(c);
        document.add(table);
    }

    public void write(Source command) throws Exception {
        // TODO: refactor common behavior to AbstractWriter
        File file = command.getFile();
        if (!file.exists()) {
            System.err.println("File not found: " + file.getAbsolutePath());
            return;
        }
        String codeStr=files.readFileStr(file);
        if (command.getMethodName() != null) {
            codeStr = extractSource(codeStr, command.getMethodName(), command
                    .getParamValue(Source.CMD_FULL));
            if (codeStr.length() == 0) {
                throw new RuntimeException("Method '" + command.getClassName()
                        + "#" + command.getMethodName() + "' not found.");
            }
        }
        writeSourceCodeBlock(codeStr, command.getMethodName());
        String methodname = command.getMethodName();
        if (methodname != null && command.getParamValue(Source.CMD_RUN)) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            runner.runExample(command.getClassName(), command
                    .getMethodName(), out);
            out.close();
            if (command.getParamValue(Source.CMD_OUTPUT)) {
                writeOutputBlock(new String(out.toByteArray(),"ISO-8859-1"));
            }
        }
    }

    public void write(Code command) throws Exception {
        writeSourceCodeBlock(String.valueOf(command.text),null);
    }
    
    private String convert(String str){
    	return str.replaceAll(String.valueOf(TAB), PDF_TAB);
    }

    public void write(String str) {
        String conv = convert(str);
        writeToFile(conv);
    }

    protected void writeToFile(String str) {
    	try {
        	document.add(new Chunk(str,defaultFont));
        } catch (DocumentException e) {
        	throw new RuntimeException(e);
        }
    }

    public void end() {
        files.task.setVariable("PDF", new Boolean(false));
        document.close();
    }

    protected String extension() {
        return "pdf";
    }
}