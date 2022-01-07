package com.polus.tvaddtool.client.pojo;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "clients")
public class Client implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Id")
	private Integer id;

	@Column(name = "Name")
	private String name;

	@Column(name = "WebsiteUrl")
	private String websiteURL;

	@Column(name = "GeneratedScript")
	private String generatedScript;

	@Column(name = "CreatedDate")
	private Timestamp cretaedDate;

	@Transient
	private List<ClientTag> clientTags;

	@Transient
	private List<BroadcastSchedule> broadcastSchedules;

	@Transient
	private List<ClientRequest> clientRequests;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getWebsiteURL() {
		return websiteURL;
	}

	public void setWebsiteURL(String websiteURL) {
		this.websiteURL = websiteURL;
	}

	public String getGeneratedScript() {
		return generatedScript;
	}

	public void setGeneratedScript(String generatedScript) {
		this.generatedScript = generatedScript;
	}

	public Timestamp getCretaedDate() {
		return cretaedDate;
	}

	public void setCretaedDate(Timestamp cretaedDate) {
		this.cretaedDate = cretaedDate;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public List<BroadcastSchedule> getBroadcastSchedules() {
		return broadcastSchedules;
	}

	public void setBroadcastSchedules(List<BroadcastSchedule> broadcastSchedules) {
		this.broadcastSchedules = broadcastSchedules;
	}

	public List<ClientRequest> getClientRequests() {
		return clientRequests;
	}

	public void setClientRequests(List<ClientRequest> clientRequests) {
		this.clientRequests = clientRequests;
	}

	public List<ClientTag> getClientTags() {
		return clientTags;
	}

	public void setClientTags(List<ClientTag> clientTags) {
		this.clientTags = clientTags;
	}

}
