package com.polus.tvaddtool.report.vo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * Pdf Header Footer Page Event created date 8 June 2021
 */
public class PdfHeaderFooterPageEvent extends PdfPageEventHelper {

	protected static Logger logger = LogManager.getLogger(PdfHeaderFooterPageEvent.class.getName());

	private static String REPORT_FOOTER = "Confidential";
	private static String REPORT_HEADER = "TV Commercial Client Report";

	private Font font1;
	private Image logo;
	private int headerYPosition;
	private int imageXPosition;
	private int imageYPosition;

	public PdfHeaderFooterPageEvent(int headerYPosition, int imageXPosition, int imageYPosition) {
		this.headerYPosition = headerYPosition;
		this.imageXPosition = imageXPosition;
		this.imageYPosition = imageYPosition;
		try {
			font1 = new Font(Font.FontFamily.TIMES_ROMAN, 11f);
			Resource resource = new ClassPathResource("logo.png");
			logo = Image.getInstance(resource.getURL());
		} catch (Exception e) {
			logger.error(" Pdf Header Footer Page Event Constructor {}", e.getMessage());
		}
	}

	@Override
	public void onEndPage(PdfWriter writer, Document document) {
		try {
			Rectangle rect = new Rectangle(0, 20, 420, headerYPosition);
			// add header text
			ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER,
					new Phrase(REPORT_HEADER, font1), rect.getRight(), rect.getTop(), 0);
			// add Footer text
			ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER,
					new Phrase(REPORT_FOOTER, font1), rect.getRight(), rect.getBottom(), 0);
			// add header image
			logo.scaleToFit(162, 56);
			logo.setAbsolutePosition(imageXPosition, imageYPosition);
			document.add(logo);
		} catch (Exception e) {
			logger.error(" Pdf Header Footer Page Event on End Page {}", e.getMessage());
		}
	}
}
