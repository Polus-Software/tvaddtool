package com.polus.tvaddtool.report.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackagePartName;
import org.apache.poi.openxml4j.opc.PackagingURIHelper;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.Header;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFPictureData;
import org.apache.poi.xssf.usermodel.XSSFRelation;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polus.tvaddtool.client.dao.ClientDao;
import com.polus.tvaddtool.client.pojo.BroadcastSchedule;
import com.polus.tvaddtool.client.pojo.ClientRequest;
import com.polus.tvaddtool.client.pojo.ClientTag;
import com.polus.tvaddtool.report.dao.ReportDao;
import com.polus.tvaddtool.report.vo.ReportRequestVO;
import com.polus.tvaddtool.report.vo.ReportResponseVO;
import com.polus.tvaddtool.report.vo.VmlDrawing;

@Transactional
@Service(value = "reportService")
public class ReportServiceimpl implements ReportService {

	protected static Logger logger = LogManager.getLogger(ReportServiceimpl.class.getName());
	private static final int NORMAL_WIDTH = 4000;

	@Autowired
	private ClientDao clientDao;

	@Autowired
	private ReportDao reportDao;

	@Override
	public ReportRequestVO generateReport(ReportRequestVO reportRequestVO) {
		try {
			List<ClientRequest> clientRequests = new ArrayList<>();
			List<ClientTag> clientTags = new ArrayList<>();
			List<BroadcastSchedule> broadcastSchedules = new ArrayList<>();
			List<ReportResponseVO> reportResponseVOs = new ArrayList<>();
			Integer clientId = reportRequestVO.getClientId();
			clientTags = clientDao.fetchAllClientTagsByClientId(clientId);
			clientRequests = reportDao.fetchClientRequestsByParams(clientId, reportRequestVO.getStartDate(), reportRequestVO.getEndDate());
			broadcastSchedules = reportDao.fetchBroadcastSchedulesByParams(clientId, reportRequestVO.getStartDate(), reportRequestVO.getEndDate());
			for (ClientRequest request : clientRequests) {
				Boolean isTagFound = false;
				for (ClientTag tag : clientTags) {
					if (request.getWebsiteURL().toUpperCase().contains(tag.getName().toUpperCase())) {
						isTagFound = true;
					}
				}
				if (isTagFound == false) {
					Timestamp createdDate = request.getCreatedDate();
					logger.info("createdDate : {}", createdDate);
					for (BroadcastSchedule schedule : broadcastSchedules) {
						Timestamp scheduledTime = schedule.getScheduledTime();
						long time = scheduledTime.getTime();
						long minute = 10 * 60 * 1000;
						Timestamp scheduleUpdatedMinutes = new Timestamp(time + minute);
						logger.info("scheduledTime : {}", scheduledTime);
						logger.info("scheduleUpdatedMinutes : {}", scheduleUpdatedMinutes);
						logger.info("createdDate.after(scheduledTime) : {}", createdDate.after(scheduledTime));
						logger.info("createdDate.before(scheduleUpdatedMinutes) : {}", createdDate.before(scheduleUpdatedMinutes));
						if (createdDate.after(scheduledTime) && createdDate.before(scheduleUpdatedMinutes)) {
							ReportResponseVO reportResponseViewModel = new ReportResponseVO();
							reportResponseViewModel.setProgramName(schedule.getVoor());
							DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
							Date scheduledDateTime = new Date(scheduledTime.getTime());
							String broadCastTime = dateFormat.format(scheduledDateTime);
							reportResponseViewModel.setBroadCastTime(broadCastTime);
							reportResponseViewModel.setTimeZone(request.getTimeZone());
							reportResponseViewModel.setVisitedURL(request.getWebsiteURL());
							Date createdDateTime = new Date(createdDate.getTime());
							String visitedTime = dateFormat.format(createdDateTime);
							reportResponseViewModel.setVisitedTime(visitedTime);
							reportResponseVOs.add(reportResponseViewModel);
						}
					}
				}
			}
			reportRequestVO.setReportResponseVOs(reportResponseVOs);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return reportRequestVO;
	}

	@Override
	public ResponseEntity<byte[]> exportGeneratedReport(ReportRequestVO reportRequestVO, HttpServletResponse response) {
		return prepareWorkbookForGeneratedReport(generateReport(reportRequestVO));
	}

	private List<String> prepareKeysForGeneratedReport() {
		List<String> keys = new ArrayList<>();
		keys.add("Program Name");
		keys.add("Visited URL");
		keys.add("Visitor TimeZone");
		keys.add("Visited Time");
		keys.add("Broadcast Time");
		return keys;
	}

	private ResponseEntity<byte[]> prepareWorkbookForGeneratedReport(ReportRequestVO reportRequestVO) {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Exported Data");
		addDetailsInHeader(workbook, sheet);
		XSSFCellStyle tableBodyStyle = workbook.createCellStyle();
		int rowNumber = prepareExcelSheetHeader(sheet, reportRequestVO, workbook, tableBodyStyle, 0);
		addDataToSheet(rowNumber, sheet, reportRequestVO, tableBodyStyle);
		try {
			return getResponseEntityForDownload(workbook);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void addDetailsInHeader(XSSFWorkbook workbook, XSSFSheet sheet) {
        try {
            Header header;
            InputStream is;
            byte[] bytes;
            int pictureIdx;
            header = sheet.getHeader();
            header.setLeft("&G");
            header.setCenter("&K000000&12" + "Broadcasting Report Details of CLIENT NAME HERE");
            Footer footer;
            footer = sheet.getFooter();
            footer.setCenter("Confidential");
            Resource resource = new ClassPathResource("logo.png");
            is = resource.getInputStream();
            bytes = IOUtils.toByteArray(is);
            pictureIdx = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
            is.close();
            sheet.setMargin(org.apache.poi.ss.usermodel.Sheet.TopMargin, 1);
            //create header picture from picture data of this workbook
            createPictureForHeader(sheet, pictureIdx, "logo", 1, "LH", sheet.getSheetName());
        } catch (Exception e) {
            logger.info("Error Occured in createPictureForHeader : {}", e.getMessage());
        }
    }

	private void createPictureForHeader(XSSFSheet sheet, int pictureIdx, String pictureTitle, int vmlIdx, String headerPos, String sheetName) {
        try {
            OPCPackage opcpackage = sheet.getWorkbook().getPackage();
            // creating /xl/drawings/vmlDrawing1.vml
            String partSheetName = new StringBuilder("/xl/drawings/vmlDrawing").append(vmlIdx).append(sheetName.replaceAll("\\s", "")).append(".vml").toString();
            PackagePartName partname = PackagingURIHelper.createPartName(partSheetName);
            PackagePart part = opcpackage.createPart(partname, "application/vnd.openxmlformats-officedocument.vmlDrawing");
            // creating new VmlDrawing
            VmlDrawing vmldrawing = new VmlDrawing(part);
            // creating the relation to the picture in
            // /xl/drawings/_rels/vmlDrawing1.vml.rels
            XSSFPictureData picData = sheet.getWorkbook().getAllPictures().get(pictureIdx);
            String rIdPic = vmldrawing.addRelation(null, XSSFRelation.IMAGES, picData).getRelationship().getId();
            // get image dimension
            ByteArrayInputStream is = new ByteArrayInputStream(picData.getData());
            // setting the image width = 3cm and height = 1.5 cm in pixels
            java.awt.Dimension imageDimension = new java.awt.Dimension(162, 56);
            is.close();
            // updating the VmlDrawing
            vmldrawing.setRelationIdPic(rIdPic);
            vmldrawing.setPictureTitle(pictureTitle);
            vmldrawing.setImageDimension(imageDimension);
            vmldrawing.setHeaderPosition(headerPos);
            // creating the relation to /xl/drawings/vmlDrawing1.xml in
            String rIdExtLink = sheet.getWorkbook().getSheet(sheetName).addRelation(null, XSSFRelation.VML_DRAWINGS, vmldrawing).getRelationship().getId();
            sheet.getWorkbook().getSheet(sheetName).getCTWorksheet().addNewLegacyDrawingHF().setId(rIdExtLink);
        } catch (Exception e) {
            logger.info("Error Occured in createPictureForHeader : {}", e.getMessage());
        }
    }

	@SuppressWarnings("deprecation")
	private int prepareExcelSheetHeader(XSSFSheet sheet, ReportRequestVO vo, XSSFWorkbook workbook, XSSFCellStyle tableBodyStyle, int rowNumber) {
		int headingCellNumber = 0;
		int rowIndex = rowNumber;
		XSSFRow tableHeadRow = sheet.createRow(rowIndex);
		rowIndex ++;
		XSSFCellStyle tableHeadStyle = workbook.createCellStyle();
		tableHeadStyle.setBorderTop(BorderStyle.HAIR);
		tableHeadStyle.setBorderBottom(BorderStyle.HAIR);
		tableHeadStyle.setBorderLeft(BorderStyle.HAIR);
		tableHeadStyle.setBorderRight(BorderStyle.HAIR);
		XSSFFont tableHeadFont = workbook.createFont();
		tableHeadFont.setBold(true);
		tableHeadFont.setFontHeightInPoints((short) 12);
		tableHeadStyle.setFont(tableHeadFont);
		tableHeadStyle.setWrapText(true);
		tableBodyStyle.setWrapText(true);
		tableBodyStyle.setBorderTop(BorderStyle.HAIR);
		tableBodyStyle.setBorderBottom(BorderStyle.HAIR);
		tableBodyStyle.setBorderLeft(BorderStyle.HAIR);
		tableBodyStyle.setBorderRight(BorderStyle.HAIR);
		XSSFCellStyle scoringColor = workbook.createCellStyle();		
		scoringColor.cloneStyleFrom(tableHeadStyle);
		scoringColor.setFillForegroundColor(new XSSFColor(new java.awt.Color(255, 255, 153)));
		scoringColor.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		XSSFCellStyle nonScoringColor = workbook.createCellStyle();
		nonScoringColor.cloneStyleFrom(scoringColor);
		nonScoringColor.setFillForegroundColor(new XSSFColor(new java.awt.Color(153, 153, 255)));
		XSSFFont tableBodyFont = workbook.createFont();
		tableBodyFont.setFontHeightInPoints((short) 12);
		tableBodyStyle.setFont(tableBodyFont);
		for (Object heading : prepareKeysForGeneratedReport()) {
			XSSFCell cell = tableHeadRow.createCell(headingCellNumber);
			cell.setCellValue((String) heading);
			cell.setCellStyle(tableHeadStyle);
			sheet.setColumnWidth(headingCellNumber, NORMAL_WIDTH);
			/* if(headingCellNumber >14) {
				cell.setCellStyle(scoringColor);
				sheet.setColumnWidth(headingCellNumber, CRITERIA_WIDTH);
			}
			if (vo.getScoringColorEndAt() != null && headingCellNumber > 15+vo.getScoringColorEndAt()) {
				cell.setCellStyle(nonScoringColor);
				sheet.setColumnWidth(headingCellNumber, CRITERIA_WIDTH);
			} */
			headingCellNumber++;
		}
		return rowIndex;
	}

	private void addDataToSheet(int rowNumber, XSSFSheet sheet, ReportRequestVO vo, XSSFCellStyle tableBodyStyle) {
		for (ReportResponseVO reportResponse : vo.getReportResponseVOs()) {
			int cellNumber = 0;
			XSSFRow row = sheet.createRow(rowNumber);
			for (String key : prepareKeysForGeneratedReport()) {
				Object objectData = null;
				if (key.equals("Program Name")) {
					objectData = reportResponse.getProgramName();
				}
				if (key.equals("Visited URL")) {
					objectData = reportResponse.getVisitedURL();
				}
				if (key.equals("Visitor TimeZone")) {
					objectData = reportResponse.getTimeZone();
				}
				if (key.equals("Visited Time")) {
					objectData = reportResponse.getVisitedTime();
				}
				if (key.equals("Broadcast Time")) {
					objectData = reportResponse.getBroadCastTime();
				}
				XSSFCell cell = row.createCell(cellNumber);
				cell.setCellStyle(tableBodyStyle);
				if (objectData instanceof String) {
					cell.setCellValue((String) objectData);
				} else if (objectData instanceof StringBuilder) {
					cell.setCellValue(objectData.toString());
				} else if (objectData instanceof Integer) {
					cell.setCellValue((Integer) objectData);
				} else if (objectData instanceof BigInteger) {
					String stringValue = ((BigInteger) objectData).toString();
					cell.setCellValue(stringValue);
				} else if (objectData instanceof BigDecimal) {
					cell.setCellValue(((BigDecimal) objectData).doubleValue());
				} else if (objectData == null) {
					cell.setCellValue(" ");
				}
				cellNumber++;
			}
			rowNumber++;
		}
	}

	private ResponseEntity<byte[]> getResponseEntity(byte[] bytes) {
        ResponseEntity<byte[]> attachmentData = null;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/octet-stream"));
            headers.setContentLength(bytes.length);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            headers.setPragma("public");
            attachmentData = new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error in method getResponseEntity", e);
        }
        return attachmentData;
    }

	public ResponseEntity<byte[]> getResponseEntityForDownload(XSSFWorkbook workbook) throws Exception {
		logger.info("--------- getResponseEntityForExcelOrPDFDownload ---------");
		byte[] byteArray = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		workbook.write(bos);
		byteArray = bos.toByteArray();
		return getResponseEntity(byteArray);
	}

}
