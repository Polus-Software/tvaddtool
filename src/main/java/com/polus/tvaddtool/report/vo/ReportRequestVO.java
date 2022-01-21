package com.polus.tvaddtool.report.vo;

import java.sql.Timestamp;
import java.util.List;

public class ReportRequestVO {

	private Integer clientId;

	private Timestamp startDate;

	private Timestamp endDate;

	private List<ReportResponseVO> reportResponseVOs;

	private String exportType;

	private String documentHeading;

	public Integer getClientId() {
		return clientId;
	}

	public void setClientId(Integer clientId) {
		this.clientId = clientId;
	}

	public Timestamp getStartDate() {
		return startDate;
	}

	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}

	public Timestamp getEndDate() {
		return endDate;
	}

	public void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

	public List<ReportResponseVO> getReportResponseVOs() {
		return reportResponseVOs;
	}

	public void setReportResponseVOs(List<ReportResponseVO> reportResponseVOs) {
		this.reportResponseVOs = reportResponseVOs;
	}

	public String getExportType() {
		return exportType;
	}

	public void setExportType(String exportType) {
		this.exportType = exportType;
	}

	public String getDocumentHeading() {
		return documentHeading;
	}

	public void setDocumentHeading(String documentHeading) {
		this.documentHeading = documentHeading;
	}

}
