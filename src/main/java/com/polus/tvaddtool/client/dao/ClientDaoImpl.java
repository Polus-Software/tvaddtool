package com.polus.tvaddtool.client.dao;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.polus.tvaddtool.client.pojo.BroadcastSchedule;
import com.polus.tvaddtool.client.pojo.Client;
import com.polus.tvaddtool.client.pojo.ClientRequest;
import com.polus.tvaddtool.client.pojo.ClientTag;

@Transactional
@Service(value = "clientDao")
public class ClientDaoImpl implements ClientDao {

	protected static Logger logger = LogManager.getLogger(ClientDaoImpl.class.getName());

	@Autowired
	private HibernateTemplate hibernateTemplate;

	@Override
	public List<Client> fetchAllClients() {
		return hibernateTemplate.loadAll(Client.class);
	}

	@Override
	public List<ClientRequest> fetchAllClientRequetsByClientId(Integer clientId) {
		Session session = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<ClientRequest> query = builder.createQuery(ClientRequest.class);
			Root<ClientRequest> clientRequestRoor = query.from(ClientRequest.class);
			query.where(builder.equal(clientRequestRoor.get("clientId"), clientId));
			return session.createQuery(query).getResultList();
		} catch (Exception e) {
			logger.error("Exception in fetchAllClientRequetsByClientId : {} ", e.getMessage());
		} finally {
			if (session != null)
				session.close();
		}
		return null;
	}

	@Override
	public List<BroadcastSchedule> fetchAllBroadcastSchedulesByClientId(Integer clientId) {
		Session session = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<BroadcastSchedule> query = builder.createQuery(BroadcastSchedule.class);
			Root<BroadcastSchedule> client = query.from(BroadcastSchedule.class);
			query.where(builder.equal(client.get("clientId"), clientId));
			return session.createQuery(query).getResultList();
		} catch (Exception e) {
			logger.error("Exception in fetchAllBroadcastSchedulesByClientId : {} ", e.getMessage());
		} finally {
			if (session != null)
				session.close();
		}
		return null;
	}

	@Override
	public List<ClientTag> fetchAllClientTagsByClientId(Integer clientId) {
		Session session = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<ClientTag> query = builder.createQuery(ClientTag.class);
			Root<ClientTag> client = query.from(ClientTag.class);
			query.where(builder.equal(client.get("clientId"), clientId));
			return session.createQuery(query).getResultList();
		} catch (Exception e) {
			logger.error("Exception in fetchAllClientTagsByClientId : {} ", e.getMessage());
		} finally {
			if (session != null)
				session.close();
		}
		return null;
	}

	@Override
	public void saveOrUpdateClient(Client client) {
		Session session = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			session.beginTransaction();
			session.saveOrUpdate(client);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception in saveOrUpdateClient : {} ", e.getMessage());
		} finally {
			if (session != null) {
				session.getTransaction().commit();
				session.close();
			}
		}
	}

	@Override
	public void saveOrUpdateClientRequest(ClientRequest clientRequest) {
		hibernateTemplate.saveOrUpdate(clientRequest);
	}

	@Override
	public void saveOrUpdateBroadcastSchedule(BroadcastSchedule broadcastSchedule) {
		hibernateTemplate.saveOrUpdate(broadcastSchedule);
	}

	@Override
	public void saveOrUpdateClientTag(ClientTag clientTag) {
		Session session = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			session.beginTransaction();
			session.saveOrUpdate(clientTag);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception in saveOrUpdateClientTag : {} ", e.getMessage());
		} finally {
			if (session != null) {
				session.getTransaction().commit();
				session.close();
			}
		}
	}

	@Override
	public void deleteClientByClientId(Integer clientId) {
		Session session = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			session.beginTransaction();
			session.delete(session.get(Client.class, clientId));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception in deleteClientByClientId : {} ", e.getMessage());
		} finally {
			if (session != null) {
				session.getTransaction().commit();
				session.close();
			}
		}
	}

	@Override
	public void deleteClientRequestByClientId(Integer clientId) {
		Session session = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			String hqlQuery = "DELETE FROM ClientRequest CR WHERE CR.clientId = :clientId";
			Query query = session.createQuery(hqlQuery);
			query.setParameter("clientId", clientId);
			query.executeUpdate();
		} catch (Exception e) {
			logger.error("Exception in deleteClientRequestByClientId : {} ", e.getMessage());
		} finally {
			if (session != null)
				session.close();
		}
	}

	@Override
	public void deleteBroadcastScheduleByIds(List<Integer> ids) {
		Session session = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			session.beginTransaction();
			if (ids != null && !ids.isEmpty()) {
				for (Integer id : ids) {
					session.delete(session.get(BroadcastSchedule.class, id));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception in deleteBroadcastScheduleByClientId : {} ", e.getMessage());
		} finally {
			if (session != null) {
				session.getTransaction().commit();
				session.close();
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Integer> getBroadcastScheduleIdByClientId(Integer clientId) {
		Session session = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			String hqlQuery = "SELECT id FROM BroadcastSchedule BS WHERE BS.clientId = :clientId";
			Query query = session.createQuery(hqlQuery);
			query.setParameter("clientId", clientId);
			return query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception in getBroadcastScheduleIdByClientId : {} ", e.getMessage());
		} finally {
			if (session != null)
				session.close();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void deleteClientTagtByClientId(Integer clientId) {
		Session session = null;
		List<Integer> ids = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			session.beginTransaction();
			String hqlQuery = "SELECT id FROM ClientTag WHERE clientId = :clientId";
			Query query = session.createQuery(hqlQuery);
			query.setParameter("clientId", clientId);
			ids = query.getResultList();
			if (ids != null && !ids.isEmpty()) {
				for (Integer id : ids) {
					session.delete(session.get(ClientTag.class, id));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception in deleteClientTagtByClientId : {} ", e.getMessage());
		} finally {
			if (session != null) {
				session.getTransaction().commit();
				session.close();
			}

		}
	}

	@Override
	public void deleteClientTagById(Integer tagId) {
		Session session = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			session.beginTransaction();
			session.delete(session.get(ClientTag.class, tagId));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception in deleteClientTagById : {} ", e.getMessage());
		} finally {
			if (session != null) {
				session.getTransaction().commit();
				session.close();
			}
		}
	}

	@Override
	public String convertObjectToJSON(Object object) {
		String response = "";
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
			response = mapper.writeValueAsString(object);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error occured in convertObjectToJSON : {}", e.getMessage());
		}
		return response;
	}

}
