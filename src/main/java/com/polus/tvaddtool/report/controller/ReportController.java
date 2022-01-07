package com.polus.tvaddtool.report.controller;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.polus.tvaddtool.client.dao.ClientDao;
import com.polus.tvaddtool.report.service.ReportService;
import com.polus.tvaddtool.report.vo.ReportRequestVO;

@RestController
public class ReportController {

	protected static Logger logger = LogManager.getLogger(ReportController.class.getName());

	@Autowired
	private ReportService reportService;

	@Autowired
	private ClientDao clientDao;

	@PostMapping(value = "/generateReport")
	public String addReview(@RequestBody ReportRequestVO vo, HttpServletRequest request, HttpServletResponse response) {
		logger.info("Requesting for generateReport");
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
			String startDateString = "2021-04-01 18:48:05.123";
		    Date startDate = dateFormat.parse(startDateString);
		    Timestamp startTimestamp = new java.sql.Timestamp(startDate.getTime());
		    String endDateString = "2021-12-31 18:48:05.123";
		    Date endDate = dateFormat.parse(endDateString);
		    Timestamp endTimestamp = new java.sql.Timestamp(endDate.getTime());
		    logger.info("startDate : {}", startTimestamp);
		    logger.info("endDate : {}", endTimestamp);
		    vo.setStartDate(startTimestamp);
		    vo.setEndDate(endTimestamp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return clientDao.convertObjectToJSON(reportService.generateReport(vo));
	}

	@PostMapping(value = "/exportGeneratedReport")
	public ResponseEntity<byte[]> exportGeneratedReport(@RequestBody ReportRequestVO vo, HttpServletResponse response) {
		logger.info("Requesting for exportGeneratedReport");
		logger.info("clientId : {}", vo.getClientId());
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
			String startDateString = "2021-04-01 18:48:05.123";
		    Date startDate = dateFormat.parse(startDateString);
		    Timestamp startTimestamp = new java.sql.Timestamp(startDate.getTime());
		    String endDateString = "2021-12-31 18:48:05.123";
		    Date endDate = dateFormat.parse(endDateString);
		    Timestamp endTimestamp = new java.sql.Timestamp(endDate.getTime());
		    logger.info("startDate : {}", startTimestamp);
		    logger.info("endDate : {}", endTimestamp);
		    vo.setStartDate(startTimestamp);
		    vo.setEndDate(endTimestamp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return reportService.exportGeneratedReport(vo, response);
	}

}
