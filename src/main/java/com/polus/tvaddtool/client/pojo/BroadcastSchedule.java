package com.polus.tvaddtool.client.pojo;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "broadcastschedules")
public class BroadcastSchedule implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Id")
	private Integer id;

	@Column(name = "ClientId")
	private Integer clientId;

	@Column(name = "ScheduledTime")
	private Timestamp scheduledTime;

	@Column(name = "CreatedDate")
	private Timestamp createdDate;

	@Column(name = "Blok")
	private String blok;

	@Column(name = "Voor")
	private String voor;

	@Column(name = "Zender")
	private String zender;

	@Column(name = "Na")
	private String na;

	@Column(name = "Datum")
	private String datum;

	@Column(name = "Tijd")
	private String tijd;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getClientId() {
		return clientId;
	}

	public void setClientId(Integer clientId) {
		this.clientId = clientId;
	}

	public Timestamp getScheduledTime() {
		return scheduledTime;
	}

	public void setScheduledTime(Timestamp scheduledTime) {
		this.scheduledTime = scheduledTime;
	}

	public Timestamp getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	public String getBlok() {
		return blok;
	}

	public void setBlok(String blok) {
		this.blok = blok;
	}

	public String getVoor() {
		return voor;
	}

	public void setVoor(String voor) {
		this.voor = voor;
	}

	public String getZender() {
		return zender;
	}

	public void setZender(String zender) {
		this.zender = zender;
	}

	public String getNa() {
		return na;
	}

	public void setNa(String na) {
		this.na = na;
	}

	public String getDatum() {
		return datum;
	}

	public void setDatum(String datum) {
		this.datum = datum;
	}

	public String getTijd() {
		return tijd;
	}

	public void setTijd(String tijd) {
		this.tijd = tijd;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
