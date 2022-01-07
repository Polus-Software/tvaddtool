package com.polus.tvaddtool.client.dao;

import java.util.List;

import org.springframework.stereotype.Service;

import com.polus.tvaddtool.client.pojo.BroadcastSchedule;
import com.polus.tvaddtool.client.pojo.Client;
import com.polus.tvaddtool.client.pojo.ClientRequest;
import com.polus.tvaddtool.client.pojo.ClientTag;

@Service
public interface ClientDao {

	public List<Client> fetchAllClients();

	public List<ClientRequest> fetchAllClientRequetsByClientId(Integer clientId);

	public List<BroadcastSchedule> fetchAllBroadcastSchedulesByClientId(Integer clientId);

	public List<ClientTag> fetchAllClientTagsByClientId(Integer clientId);

	public void saveOrUpdateClient(Client client);

	public void saveOrUpdateClientRequest(ClientRequest clientRequest);

	public void saveOrUpdateBroadcastSchedule(BroadcastSchedule broadcastSchedule);

	public void saveOrUpdateClientTag(ClientTag clientTag);

	public void deleteClientByClientId(Integer clientId);

	public void deleteClientRequestByClientId(Integer clientId);

	public void deleteBroadcastScheduleByIds(List<Integer> ids);

	public void deleteClientTagtByClientId(Integer clientId);

	public List<Integer> getBroadcastScheduleIdByClientId(Integer clientId);

	public void deleteClientTagById(Integer tagId);

	public String convertObjectToJSON(Object object);

}
