package com.polus.tvaddtool.client.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.polus.tvaddtool.client.dao.ClientDao;
import com.polus.tvaddtool.client.pojo.BroadcastSchedule;
import com.polus.tvaddtool.client.pojo.Client;
import com.polus.tvaddtool.client.pojo.ClientRequest;
import com.polus.tvaddtool.client.pojo.ClientTag;
import com.polus.tvaddtool.client.vo.ClientVO;

@Transactional
@Service(value = "clientService")
public class ClientServiceImpl implements ClientService {

	protected static Logger logger = LogManager.getLogger(ClientServiceImpl.class.getName());

	@Autowired
	private ClientDao clientDao;

	@Override
	public String addClientDetails(MultipartFile multipartFile, String formDataJSON) {
		Client client = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			client = mapper.readValue(formDataJSON, Client.class);
			Integer clientId = client.getId();
			if (clientId == null) {
				client.setCretaedDate(getCurrentTimestamp());
			}
			clientDao.saveOrUpdateClient(client);
			clientId = client.getId();
			logger.info("clientId : {}", client.getId());
			if (client.getGeneratedScript() == null) {
				String generatedScript = generateScript(clientId);
				logger.info("generatedScript : {}", generatedScript);
				client.setGeneratedScript(generatedScript);
				clientDao.saveOrUpdateClient(client);
			}
			saveOrUpdateClientTags(client);
			if (multipartFile != null) {
				File file = convertMultiPartToFile(multipartFile);
				List<Integer> broadcastScheduleIds = clientDao.getBroadcastScheduleIdByClientId(clientId);
				if (broadcastScheduleIds != null && !broadcastScheduleIds.isEmpty()) {
					clientDao.deleteBroadcastScheduleByIds(broadcastScheduleIds);
				}
				broadcastingExcelFileProcessing(file, getExcelRowCount(file), clientId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return clientDao.convertObjectToJSON(client);
	}

	private void saveOrUpdateClientTags(Client client) {
		List<ClientTag> clientTags = client.getClientTags();
		if (clientTags != null && !clientTags.isEmpty()) {
			clientTags.forEach(tag -> {
				if (tag.getId() != null && tag.getIsDeleted()) {
					clientDao.deleteClientTagById(tag.getId());
				} else if (tag.getId() == null) {
					tag.setCreatedDate(getCurrentTimestamp());
					tag.setClientId(client.getId());
				}
				clientDao.saveOrUpdateClientTag(tag);
			});
		}
	}

	private File convertMultiPartToFile(MultipartFile file) throws IOException {
		File convFile = new File(file.getOriginalFilename());
		FileOutputStream fos = new FileOutputStream(convFile);
		fos.write(file.getBytes());
		fos.close();
		return convFile;
	}

	private Integer getExcelRowCount(File file) throws IOException {
		int rows = 0;
		String fileName = null;
		fileName = file.toString();
		logger.info("fileName : {}", fileName);
		if (fileName.endsWith(".xlsx")) {
			XSSFWorkbook workbook = null;
			XSSFSheet sheet;
			try {
				workbook = new XSSFWorkbook(new FileInputStream(file));
				sheet = workbook.getSheetAt(0);
				rows = sheet.getPhysicalNumberOfRows();
				rows--;
				logger.info("Row Count:{}", rows);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				workbook.close();
			}
		} else if (fileName.endsWith(".xls")) {
			POIFSFileSystem fs = null;
			HSSFWorkbook wb = null;
			try {
				fs = new POIFSFileSystem(new FileInputStream(file));
				wb = new HSSFWorkbook(fs);
				HSSFSheet sheet = wb.getSheetAt(0);
				rows = sheet.getPhysicalNumberOfRows();
				rows--;
				logger.info("Row Count:{}", rows);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				fs.close();
				wb.close();
			}
		}
		return rows;
	}

	private void broadcastingExcelFileProcessing(File file, Integer totalFileRowCount, Integer clientId) throws IOException {
		Workbook workbook = null;
		FileInputStream fis = null;
		try {
			DataFormatter dataFormatter = new DataFormatter();
			fis = new FileInputStream(file);
			workbook = WorkbookFactory.create(fis);
			FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
			Sheet sheet = workbook.getSheetAt(0);
			if (sheet != null) {
				for (Row row : sheet) {
					if (row.getRowNum() == 0) {
						continue; // just skip the rows if row number is 0
					}
					BroadcastSchedule broadcastSchedule = new BroadcastSchedule();
					broadcastSchedule.setBlok((dataFormatter.formatCellValue(row.getCell(0), formulaEvaluator)));
					broadcastSchedule.setVoor(dataFormatter.formatCellValue(row.getCell(1), formulaEvaluator));
					broadcastSchedule.setZender(dataFormatter.formatCellValue(row.getCell(2), formulaEvaluator));
					broadcastSchedule.setNa(dataFormatter.formatCellValue(row.getCell(3), formulaEvaluator));
					SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy hh:mm");
					String scheduledTime = dataFormatter.formatCellValue(row.getCell(4), formulaEvaluator);
					logger.info("scheduledTime : {}", scheduledTime);
					Date parsedDate = dateFormat.parse(scheduledTime);
					Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
					broadcastSchedule.setScheduledTime(timestamp);
					broadcastSchedule.setDatum(dataFormatter.formatCellValue(row.getCell(5), formulaEvaluator));
					broadcastSchedule.setTijd(dataFormatter.formatCellValue(row.getCell(6), formulaEvaluator));
					broadcastSchedule.setClientId(clientId);
					broadcastSchedule.setCreatedDate(getCurrentTimestamp());
					clientDao.saveOrUpdateBroadcastSchedule(broadcastSchedule);
					--totalFileRowCount;
				}
			}
			--totalFileRowCount;
			if (totalFileRowCount < 0) {
				totalFileRowCount = 0;
			}
		} catch (Exception ioe) {
			ioe.printStackTrace();
			logger.error("Error in method broadcastingExcelFileProcessing {}", ioe.getMessage());
		} finally {
			workbook.close();
			fis.close();
		}
	}

	@Override
	public String fetchAllClients() {
		ClientVO clientVO = new ClientVO();
		List<Client> clients = clientDao.fetchAllClients();
		if (clients != null && !clients.isEmpty()) {
			clients.forEach(client -> {
				client.setClientTags(clientDao.fetchAllClientTagsByClientId(client.getId()));
			});
			clientVO.setClients(clients);
		}
		return clientDao.convertObjectToJSON(clientVO);
	}

	@Override
	public String fetchAllBroadcastSchedules(ClientVO clientVO) {
		clientVO.setBroadcastSchedules(clientDao.fetchAllBroadcastSchedulesByClientId(clientVO.getClientId()));
		return clientDao.convertObjectToJSON(clientVO);
	}

	public Timestamp getCurrentTimestamp() {
		return new java.sql.Timestamp(getCurrentDate().getTime());
	}

	public Date getCurrentDate() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		return c.getTime();
	}

	@Override
	public String deleteClient(ClientVO clientVO) {
		Integer clientId = clientVO.getClientId();
		clientDao.deleteClientTagtByClientId(clientId);
		clientDao.deleteBroadcastScheduleByIds(clientDao.getBroadcastScheduleIdByClientId(clientId));
		clientDao.deleteClientByClientId(clientId);
		clientVO.setMessage("Client deleted successfully");
		return clientDao.convertObjectToJSON(clientVO);
	}

	public String generateScript(Integer clientId) {
		try {
			String Script = "<script>(function(){ var getSessionValue = sessionStorage.getItem('UserPresent'); var t_v_c_clientId="
					+ clientId
					+ "; var t_v_c_data=JSON.stringify({'ClientId':t_v_c_clientId,'TimeZone':Intl.DateTimeFormat().resolvedOptions().timeZone,'WebsiteUrl':window.location.href,'OffSet':new Date().getTimezoneOffset()});var e=new XMLHttpRequest();e.withCredentials=!1;e.addEventListener('readystatechange',function(){if(this.readyState===4){console.log(this.responseText)}}); if(getSessionValue == null){ e.open('POST','http://103.121.26.82:1122/savewebrequests');e.setRequestHeader('Content-Type','application/json');e.send(t_v_c_data);sessionStorage.setItem('UserPresent', 'true');}  })();</script>";
			return Script;
		} catch (Exception ex) {
			ex.printStackTrace();
//			LogToFile.writeLog("GenerateScript : " + ex.ToString());
//			return String.Empty;
		}
		return "";
	}

	@Override
	public void saveWebRequests(ClientRequest clientRequest) {
		clientDao.saveOrUpdateClientRequest(clientRequest);
	}

	@Override
	public String getClientById(ClientVO clientVO) {
		Client client = clientDao.getClientById(clientVO.getClientId());
		client.setClientTags(clientDao.fetchAllClientTagsByClientId(client.getId()));
		clientVO.setClient(client);
		return clientDao.convertObjectToJSON(clientVO);
	}

}
