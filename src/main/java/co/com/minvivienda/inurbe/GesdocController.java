package co.com.minvivienda.inurbe;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/")
public class GesdocController {
	
	
    @PostMapping(value = "/mock/createReceived", produces = {"multipart/mixed"}, consumes = {"multipart/form-data"})
    public ResponseEntity<?> createReceived(@RequestParam Map<String, MultipartFile> files)  throws Exception {
    	
    	try {
	    	String fileName = "etiqueta.png";
	        byte[] etiquetaBytes = readImageFromClasspath(fileName);
	        System.out.println("etiquetaBytes = " + etiquetaBytes.length);
	    	if (etiquetaBytes != null && etiquetaBytes.length <= 0) {
	        	System.out.println("Archivo no existe");
	            return ResponseEntity.notFound().build();
	        }
	        
	        ByteArrayResource resource = new ByteArrayResource(etiquetaBytes) {
	            @Override
	            public String getFilename() {
	                return fileName;
	            }
	        };
	        
	        HttpHeaders jsonResponseHeaders = new HttpHeaders();
	        jsonResponseHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);       
	        jsonResponseHeaders.add(HttpHeaders.TRANSFER_ENCODING, "binary");
	        jsonResponseHeaders.set("Content-ID", "<root>");
	        
	        files = null;
	        String jsonResponse = "{"
	        		+ "    \"idelement\": {"
	        		+ "        \"idtype\": 2,"
	        		+ "        \"id\": \"2022ER0008851\""
	        		+ "    },"
	        		+ "    \"message\": \"Los cambios se realizaron\","
	        		+ "    \"actionDate\": \"20240611063034-0500\""
	        		+ "}";
	        
	        HttpEntity<String> jsonEntity = new HttpEntity<String>(jsonResponse, jsonResponseHeaders);
	        HttpHeaders jsonFileHeaders = new HttpHeaders();
	        jsonFileHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
	        jsonFileHeaders.add(HttpHeaders.TRANSFER_ENCODING, "binary");
	        jsonFileHeaders.set("Content-ID", "<etiqueta.png>");
	        HttpEntity<ByteArrayResource> fileEntity = new HttpEntity<ByteArrayResource>(resource, jsonFileHeaders);
	        
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.MULTIPART_MIXED);
	        
	        MultiValueMap<String, Object> responseBody = new LinkedMultiValueMap<>();
	        responseBody.clear();
	        responseBody.set("root", jsonEntity);
	        responseBody.set("etiqueta.png", fileEntity);
	        
	        return new ResponseEntity<MultiValueMap<String, Object>>(responseBody, headers, HttpStatus.OK);
    	}catch(Exception ex) {
    		ex.printStackTrace();
    		return new ResponseEntity<String>("EXCEPCION ", HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
    
    
    @PostMapping(value = "/mock/createInner", produces = {"multipart/mixed"}, consumes = {"multipart/form-data"})
    public ResponseEntity<?> createInner(@RequestParam Map<String, MultipartFile> files)  throws Exception {
    	
    	try {
	    	String fileName = "etiqueta.png";
	        byte[] etiquetaBytes = readImageFromClasspath(fileName);
	        System.out.println("etiquetaBytes = " + etiquetaBytes.length);
	    	if (etiquetaBytes != null && etiquetaBytes.length <= 0) {
	        	System.out.println("Archivo no existe");
	            return ResponseEntity.notFound().build();
	        }
	        
	        ByteArrayResource resource = new ByteArrayResource(etiquetaBytes) {
	            @Override
	            public String getFilename() {
	                return fileName;
	            }
	        };
	        
	        HttpHeaders jsonResponseHeaders = new HttpHeaders();
	        jsonResponseHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);       
	        jsonResponseHeaders.add(HttpHeaders.TRANSFER_ENCODING, "binary");
	        jsonResponseHeaders.set("Content-ID", "<root>");
	        
	        files = null;
	        String jsonResponse = "{"
	        		+ "    \"idelement\": {"
	        		+ "        \"idtype\": 2,"
	        		+ "        \"id\": \"2022ER0008851\""
	        		+ "    },"
	        		+ "    \"message\": \"Los cambios se realizaron\","
	        		+ "    \"actionDate\": \"20240611063034-0500\""
	        		+ "}";
	        
	        
	        HttpEntity<String> jsonEntity = new HttpEntity<String>(jsonResponse, jsonResponseHeaders);
	        HttpHeaders jsonFileHeaders = new HttpHeaders();
	        jsonFileHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
	        jsonFileHeaders.add(HttpHeaders.TRANSFER_ENCODING, "binary");
	        jsonFileHeaders.set("Content-ID", "<etiqueta.png>");
	        HttpEntity<ByteArrayResource> fileEntity = new HttpEntity<ByteArrayResource>(resource, jsonFileHeaders);
	        
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.MULTIPART_MIXED);
	        
	        MultiValueMap<String, Object> responseBody = new LinkedMultiValueMap<>();
	        responseBody.clear();
	        responseBody.set("root", jsonEntity);
	        responseBody.set("etiqueta.png", fileEntity);
	        
	        return new ResponseEntity<MultiValueMap<String, Object>>(responseBody, headers, HttpStatus.OK);
    	}catch(Exception ex) {
    		ex.printStackTrace();
    		return new ResponseEntity<String>("EXCEPCION ", HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
    
    
    
    @PostMapping(value = "/mock/createSended", produces = {"multipart/mixed"}, consumes = {"multipart/form-data"})
    public ResponseEntity<?> createSended(@RequestParam Map<String, MultipartFile> files)  throws Exception {
    	
    	try {
	    	String fileName = "etiqueta.png";
	        byte[] etiquetaBytes = readImageFromClasspath(fileName);
	        System.out.println("etiquetaBytes = " + etiquetaBytes.length);
	    	if (etiquetaBytes != null && etiquetaBytes.length <= 0) {
	        	System.out.println("Archivo no existe");
	            return ResponseEntity.notFound().build();
	        }
	        
	        ByteArrayResource resource = new ByteArrayResource(etiquetaBytes) {
	            @Override
	            public String getFilename() {
	                return fileName;
	            }
	        };
	        
	        HttpHeaders jsonResponseHeaders = new HttpHeaders();
	        jsonResponseHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);       
	        jsonResponseHeaders.add(HttpHeaders.TRANSFER_ENCODING, "binary");
	        jsonResponseHeaders.set("Content-ID", "<root>");
	        
	        files = null;
	        String jsonResponse = "{"
	        		+ "    \"idelement\": {"
	        		+ "        \"idtype\": 2,"
	        		+ "        \"id\": \"2022ER0008851\""
	        		+ "    },"
	        		+ "    \"message\": \"Los cambios se realizaron\","
	        		+ "    \"actionDate\": \"20240611063034-0500\""
	        		+ "}";
	        
	        HttpEntity<String> jsonEntity = new HttpEntity<String>(jsonResponse, jsonResponseHeaders);
	        HttpHeaders jsonFileHeaders = new HttpHeaders();
	        jsonFileHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
	        jsonFileHeaders.add(HttpHeaders.TRANSFER_ENCODING, "binary");
	        jsonFileHeaders.set("Content-ID", "<etiqueta.png>");
	        HttpEntity<ByteArrayResource> fileEntity = new HttpEntity<ByteArrayResource>(resource, jsonFileHeaders);
	        
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.MULTIPART_MIXED);
	        
	        MultiValueMap<String, Object> responseBody = new LinkedMultiValueMap<>();
	        responseBody.clear();
	        responseBody.set("root", jsonEntity);
	        responseBody.set("etiqueta.png", fileEntity);
	        
	        return new ResponseEntity<MultiValueMap<String, Object>>(responseBody, headers, HttpStatus.OK);
    	}catch(Exception ex) {
    		ex.printStackTrace();
    		return new ResponseEntity<String>("EXCEPCION ", HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
    
    
    @PostMapping(value = "/mock/createRegistered", produces = {"multipart/mixed"}, consumes = {"multipart/form-data"})
    public ResponseEntity<?> createRegistered(@RequestParam Map<String, MultipartFile> files)  throws Exception {
    	
    	try {	        
	        HttpHeaders jsonResponseHeaders = new HttpHeaders();
	        jsonResponseHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);       
	        jsonResponseHeaders.add(HttpHeaders.TRANSFER_ENCODING, "binary");
	        jsonResponseHeaders.set("Content-ID", "<root>");
	        
	        files = null;
	        String jsonResponse = "{"
	        		+ "    \"idelement\": {"
	        		+ "        \"idtype\": 2,"
	        		+ "        \"id\": \"2022ER0008851\""
	        		+ "    },"
	        		+ "    \"message\": \"Los cambios se realizaron\","
	        		+ "    \"actionDate\": \"20240611063034-0500\""
	        		+ "}";
	        
	        HttpEntity<String> jsonEntity = new HttpEntity<String>(jsonResponse, jsonResponseHeaders);
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.MULTIPART_MIXED);
	        MultiValueMap<String, Object> responseBody = new LinkedMultiValueMap<>();
	        responseBody.set("root", jsonEntity);
	        
	        return new ResponseEntity<MultiValueMap<String, Object>>(responseBody, headers, HttpStatus.OK);
    	}catch(Exception ex) {
    		ex.printStackTrace();
    		return new ResponseEntity<String>("EXCEPCION ", HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
    
    
    public byte[] readImageFromClasspath(String fileName) throws IOException {
        Resource resource = new ClassPathResource(fileName);
        
        System.out.println("exists = " + resource.exists());
        System.out.println("contentLength = " + resource.contentLength());
        System.out.println(resource.getURI());
        try (InputStream inputStream = resource.getInputStream()) {
            
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] data = new byte[inputStream.available()];
            int bytesRead;
            while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, bytesRead);
            }
            
            return buffer.toByteArray();
        }
    }
    
}
