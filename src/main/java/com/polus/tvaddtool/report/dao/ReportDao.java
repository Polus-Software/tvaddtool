package com.polus.tvaddtool.report.dao;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.stereotype.Service;

import com.polus.tvaddtool.client.pojo.BroadcastSchedule;
import com.polus.tvaddtool.client.pojo.ClientRequest;

@Service
public interface ReportDao {

	public List<ClientRequest> fetchClientRequestsByParams(Integer clientId, Timestamp startDate, Timestamp endDate);

	public List<BroadcastSchedule> fetchBroadcastSchedulesByParams(Integer clientId, Timestamp startDate, Timestamp endDate);

}
