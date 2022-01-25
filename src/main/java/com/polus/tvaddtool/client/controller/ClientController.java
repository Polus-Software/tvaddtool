package com.polus.tvaddtool.client.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.polus.tvaddtool.client.pojo.ClientRequest;
import com.polus.tvaddtool.client.service.ClientService;
import com.polus.tvaddtool.client.vo.ClientVO;

@RestController
public class ClientController {

	protected static Logger logger = LogManager.getLogger(ClientController.class.getName());

	@Autowired
	private ClientService clientService;

	@PostMapping(value = "/addClientDetails")
	public String addClientDetails(@RequestParam(value = "file", required = false) MultipartFile file, @RequestParam("formDataJson") String formDataJson) {
		logger.info("Requesting for addClientDetails");
		return clientService.addClientDetails(file, formDataJson);
	}

	@GetMapping(value = "/fetchAllClients")
	public String fetchAllClients() {
		logger.info("Requesting for fetchAllClients");
		return clientService.fetchAllClients();
	}

	@PostMapping(value = "/fetchAllBroadcastSchedules")
	public String fetchAllBroadcastSchedules(@RequestBody ClientVO vo, HttpServletRequest request) {
		logger.info("Requesting for fetchAllBroadcastSchedules");
		logger.info("clientId : {}", vo.getClientId());
		return clientService.fetchAllBroadcastSchedules(vo);
	}

	@PostMapping(value = "/deleteClient")
	public String deleteClient(@RequestBody ClientVO vo, HttpServletRequest request) {
		logger.info("Requesting for deleteClient");
		logger.info("clientId : {}", vo.getClientId());
		return clientService.deleteClient(vo);
	}

	@PostMapping(value = "/saveWebRequests")
	public void saveWebRequests(@RequestBody ClientRequest clientRequest, HttpServletRequest request) {
		logger.info("Requesting for saveWebRequests");
		logger.info("clientId : {}", clientRequest.getClientId());
		clientService.saveWebRequests(clientRequest);
	}

	@PostMapping(value = "/getClientById")
	public String getClientById(@RequestBody ClientVO vo, HttpServletRequest request) {
		logger.info("Requesting for getClientById");
		logger.info("clientId : {}", vo.getClientId());
		return clientService.getClientById(vo);
	}

}
