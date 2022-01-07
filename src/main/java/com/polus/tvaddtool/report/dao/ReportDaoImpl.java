package com.polus.tvaddtool.report.dao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polus.tvaddtool.client.pojo.BroadcastSchedule;
import com.polus.tvaddtool.client.pojo.ClientRequest;

@Transactional
@Service(value = "reportDao")
public class ReportDaoImpl implements ReportDao {

	protected static Logger logger = LogManager.getLogger(ReportDaoImpl.class.getName());

	@Autowired
	private HibernateTemplate hibernateTemplate;

	@Override
	public List<ClientRequest> fetchClientRequestsByParams(Integer clientId, Timestamp startDate, Timestamp endDate) {
		Session session = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<ClientRequest> query = builder.createQuery(ClientRequest.class);
			Root<ClientRequest> clientRequestRoot = query.from(ClientRequest.class);
			Predicate predicateClientId = builder.equal(clientRequestRoot.get("clientId"), clientId);
			Predicate predicateCreatedDate = builder.between(clientRequestRoot.get("createdDate"), startDate, endDate);
			query.where(builder.and(predicateClientId, predicateCreatedDate));
			return session.createQuery(query).getResultList();
		} catch (Exception e) {
			logger.error("Exception in fetchClientRequestsByParams : {} ", e.getMessage());
		} finally {
			if (session != null)
				session.close();
		}
		return new ArrayList<>();
	}

	@Override
	public List<BroadcastSchedule> fetchBroadcastSchedulesByParams(Integer clientId, Timestamp startDate, Timestamp endDate) {
		Session session = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<BroadcastSchedule> query = builder.createQuery(BroadcastSchedule.class);
			Root<BroadcastSchedule> broadcastScheduleRoot = query.from(BroadcastSchedule.class);
			Predicate predicateClientId = builder.equal(broadcastScheduleRoot.get("clientId"), clientId);
			Predicate predicateCreatedDate = builder.between(broadcastScheduleRoot.get("createdDate"), startDate, endDate);
			query.where(builder.and(predicateClientId, predicateCreatedDate));
			return session.createQuery(query).getResultList();
		} catch (Exception e) {
			logger.error("Exception in fetchBroadcastSchedulesByParams : {} ", e.getMessage());
		} finally {
			if (session != null)
				session.close();
		}
		return new ArrayList<>();
	}

}
