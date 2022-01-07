package com.polus.tvaddtool.client.vo;

import java.util.List;

import com.polus.tvaddtool.client.pojo.BroadcastSchedule;
import com.polus.tvaddtool.client.pojo.Client;

public class ClientVO {

	private Integer clientId;

	private String message;

	private List<Client> clients;

	private List<BroadcastSchedule> broadcastSchedules;

	public List<Client> getClients() {
		return clients;
	}

	public void setClients(List<Client> clients) {
		this.clients = clients;
	}

	public List<BroadcastSchedule> getBroadcastSchedules() {
		return broadcastSchedules;
	}

	public void setBroadcastSchedules(List<BroadcastSchedule> broadcastSchedules) {
		this.broadcastSchedules = broadcastSchedules;
	}

	public Integer getClientId() {
		return clientId;
	}

	public void setClientId(Integer clientId) {
		this.clientId = clientId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
