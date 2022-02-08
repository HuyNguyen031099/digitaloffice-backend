package com.datn.doffice.service.impl;

import com.aspose.words.PdfCompliance;
import com.aspose.words.PdfSaveOptions;
import com.datn.doffice.dto.UserLoginDetailDTO;
import com.datn.doffice.dto.UserPermissionDocumentDTO;
import com.datn.doffice.entity.FolderEntity;
import com.datn.doffice.entity.VersionEntity;
import com.datn.doffice.exceptions.MyFileNotFoundException;
import com.datn.doffice.utils.OKMUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.openkm.api.OKMAuth;
import com.openkm.api.OKMDocument;
import com.openkm.sdk4j.OKMWebservices;
import com.openkm.sdk4j.OKMWebservicesFactory;
import com.openkm.sdk4j.bean.Document;
import com.openkm.sdk4j.bean.Folder;
import com.openkm.sdk4j.bean.Note;
import com.openkm.sdk4j.bean.QueryResult;
import com.openkm.sdk4j.bean.Version;
import com.openkm.sdk4j.exception.UnknowException;
import com.openkm.sdk4j.exception.WebserviceException;
import com.openkm.sdk4j.impl.AuthImpl;
import com.openkm.sdk4j.util.BeanHelper;

import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestOpenKMAPI {

	public static final int DEFAULT_BUFFER_SIZE = 8192;

	public static OKMWebservices getOKMWebServices(String username, String password)
			throws UnknowException, WebserviceException {
		String url = "http://localhost:8080/OpenKM";
		OKMWebservices ws = OKMWebservicesFactory.newInstance(url, username, password);
		ws.login();
		return ws;
	}

	public static InputStream loadFileAsResource(String fileName) {
		Path filePath = Paths.get("C:/Spring tool/source/digital-office-backend/uploads").normalize().resolve(fileName);
		try {
			InputStream is = new FileInputStream(new File(filePath.toUri()));
			return is;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static void copyInputStreamToFile(InputStream inputStream, File file) throws IOException {
		try (FileOutputStream outputStream = new FileOutputStream(file, false)) {
			int read;
			byte[] bytes = new byte[DEFAULT_BUFFER_SIZE];
			while ((read = inputStream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}
		}
	}
	
	public static boolean isFileClosed(File file) throws IOException {
    	try {
    		Process plsof = new ProcessBuilder(new String[]{"lsof", "|", "grep", file.getAbsolutePath()}).start();
    		BufferedReader reader = new BufferedReader(new InputStreamReader(plsof.getInputStream()));
            String line;
            while((line=reader.readLine())!=null) {
                if(line.contains(file.getAbsolutePath())) {                            
                    reader.close();
                    plsof.destroy();
                    return false;
                }
            }
            reader.close();
            plsof.destroy();
        } catch(Exception e) {
        	e.printStackTrace();
        }
        return true;
    }
	
	public static boolean convertDocToPdf(String fileName, String destPath) throws IOException, DocumentException {
		InputStream doc = new FileInputStream(new File(fileName));
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();

	    XWPFDocument document = new XWPFDocument(doc);
	    PdfOptions options = PdfOptions.create();
	    PdfConverter.getInstance().convert(document, baos, options);
	    String b64_encoded = Base64.getEncoder().encodeToString(baos.toByteArray());
	    FileOutputStream fos = new FileOutputStream(new File("e:\\test.txt"));
	    fos.write(b64_encoded.getBytes());
	    fos.close();
	    return true;
	}

	public static void main(String[] args) throws UnknowException, WebserviceException, org.json.simple.parser.ParseException, JsonMappingException, JsonProcessingException, DocumentException {
		
		String path = "e:\\TacoCloud.PNG";
		File file = new File(path);
		try {
			if(isFileClosed(file)) System.out.println("OK");
			else System.out.println("NO");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		try {
//			boolean flag = convertDocToPdf("C:\\Spring tool\\source\\digital-office-backend\\uploads\\tài liệu mới.docx", "e:\\test.pdf");
//			System.out.println(flag);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
//		String name = "text.abc.docx";
//		String tmp[] = name.split("\\.");
//		String res = "";
//		res = "abc";
//		System.out.println(res + ".pdf");
		
		
//		try {
//			com.aspose.words.Document document = new com.aspose.words.Document("C:\\Spring tool\\source\\digital-office-backend\\uploads\\tài liệu mới.docx");
////			PdfSaveOptions options = new PdfSaveOptions();
////			options.setCompliance(PdfCompliance.PDF_17);
//			document.save("C:\\Users\\pytha\\Downloads\\Compressed\\test.pdf");
//			System.out.println("Done!");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		
//		String val = "[{\"username\":\"hatqptit\",\"roles\":[2]},{\"username\":\"administrator\",\"roles\":[1]}]";
//        ObjectMapper mapper = new ObjectMapper();
//        UserPermissionDocumentDTO[] updd = mapper.readValue(val, UserPermissionDocumentDTO[].class);
//        for(UserPermissionDocumentDTO u: updd) {
//        	System.out.println(u);
//        }
		
		
//		String name = "10.1.137.12.docx";
//		String[] tmp = name.split("\\.");
//		int len = tmp.length;
//		System.out.println(tmp[len-1]);
		
		
		
//		String date = "";
//		if(date.equals("")) System.out.println("ulll");
//		else {
//			String res = "";
//			String[] tmp = date.split("-");
//			res = tmp[2] + "/" + tmp[1] + "/" + tmp[0] + " 00:00:00";
//			System.out.println(res);
//		}
		
		
//		String name = "kbjvskvbksbvkjdbvkjb";
//		String name2 = "bksbv";
//		System.out.println(name.contains(name2));
		
//		String oldName = "usecase.docx"; // extract from url
//		String newName = "Usecase.docx";
//		String urlVersionDir = "C:\\Spring tool\\source\\digital-office-backend\\versions";
//		
//		// rename file version
//		File[] dirs = new File(urlVersionDir).listFiles();
//		for(File dir: dirs) {
//			if(dir.isDirectory()) {
//				File[] fileVersions = new File(dir.getAbsolutePath()).listFiles();
//				for(File fileVersion: fileVersions) {
//					if(fileVersion.getAbsolutePath().equals(dir.getAbsolutePath() + "\\" + oldName)) {
//						File f1 = new File(fileVersion.getAbsolutePath());
//						File f2 = new File(dir.getAbsolutePath() + "\\" + newName);
//						boolean flag = f1.renameTo(f2);
//						System.out.println(flag);
//					} else continue;
//				}
//			}
//		}
		
//		File[] dirs = new File(urlVersionDir).listFiles();
//		for(File dir: dirs) {
//			if(dir.isDirectory()) {
//				File[] fileVersions = new File(dir.getAbsolutePath()).listFiles();
//				for(File fileVersion: fileVersions) {
//					System.out.println(fileVersion);
//				}
//			}
//		}
		
//		String[] keywords = {"abc", "cba", "dino"};
//		List<String> keys = Arrays.asList(keywords);
//		String tmp = "";
//		int len = keys.size();
//		for(int i = 0; i < len; i++) {
//			if(i == len - 1) tmp += "\"" + keys.get(i) +"\"";
//			else tmp += "\"" + keys.get(i) + "\",";
//		}
//		System.out.println(tmp);
//		String query = "db.document_keyword.find({ keyword: { $all: [[" + tmp + "]]}})";
//		System.out.println(query);
		
//		InputStream is = loadFileAsResource("Usecase.docx");
//		File file = new File("e:/test.docx");
//		try {
//			copyInputStreamToFile(is, file);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

// 		restoreVersion("3a94a14f-9145-4dc7-abdc-b4e4206e6de1", "1.0", "okmAdmin", "admin");
//		getVersionHistory("3a94a14f-9145-4dc7-abdc-b4e4206e6de1", "huynd", "huynd");
//		createDocument("E:/TacoCloud.PNG", "645823e0-8a99-42e6-9b62-91090e6f5d48", "taco.png", "huynd", "huynd");
//		System.out.println(extractNameFromPath("/okm:root/doc3/taco.png"));

	}
}
