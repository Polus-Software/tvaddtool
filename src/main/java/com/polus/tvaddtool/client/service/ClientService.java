package com.polus.tvaddtool.client.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.polus.tvaddtool.client.pojo.ClientRequest;
import com.polus.tvaddtool.client.vo.ClientVO;

@Service
public interface ClientService {

	public String addClientDetails(MultipartFile multipartFile, String formDataJSON);

	public String fetchAllClients();

	public String fetchAllBroadcastSchedules(ClientVO clientVO);

	public String deleteClient(ClientVO clientVO);

	public void saveWebRequests(ClientRequest clientRequest);

}
