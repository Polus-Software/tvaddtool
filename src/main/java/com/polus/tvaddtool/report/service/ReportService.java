package com.polus.tvaddtool.report.service;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.polus.tvaddtool.report.vo.ReportRequestVO;

@Service
public interface ReportService {

	public ReportRequestVO generateReport(ReportRequestVO reportRequestVO);

	public ResponseEntity<byte[]> exportGeneratedReport(ReportRequestVO reportRequestVO, HttpServletResponse response);

}
