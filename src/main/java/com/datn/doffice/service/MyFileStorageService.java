package com.datn.doffice.service;

import com.aspose.words.Document;
import com.aspose.words.PdfCompliance;
import com.aspose.words.PdfSaveOptions;
import com.datn.doffice.exceptions.FileDeleteException;
import com.datn.doffice.exceptions.FileStorageException;
import com.datn.doffice.exceptions.MyFileNotFoundException;
import com.datn.doffice.utils.FileStorageProperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class MyFileStorageService {

    private final Path fileStorageLocation;
    
    public static final int DEFAULT_BUFFER_SIZE = 8192;
    
    @Autowired
    private FileStorageProperties fileStorageProperties;

    @Autowired
    public MyFileStorageService(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getVersionDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }
    
    // in uploads dir (type = public)
    public String storeFile(MultipartFile file) {
//        // Normalize file name
//        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
//
//        try {
//            // Check if the file's name contains invalid characters
//            if(fileName.contains("..")) {
//                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
//            }
//
//            // Copy file to the target location (Replacing existing file with the same name)
//            Path targetLocation = Paths.get(fileStorageProperties.getUploadDir()).resolve(fileName);
//            System.out.println(targetLocation);
//            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
//
//            return targetLocation.toString();
//        } catch (IOException ex) {
//            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
//        }
    	Path targetLocation = Paths.get(fileStorageProperties.getUploadDir()).resolve(file.getOriginalFilename());
    	String location = storeT(file, targetLocation);
    	return location;
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new MyFileNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new MyFileNotFoundException("File not found " + fileName, ex);
        }
    }
    
    public InputStream loadFileAsInputStream(String filename) {
    	Path filePath = Paths.get(fileStorageProperties.getUploadDir()).resolve(filename).normalize();
		try {
			InputStream is = new FileInputStream(new File(filePath.toUri()));
			return is;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
    }
    
    public void copyInputStreamToFile(InputStream inputStream, File file) throws IOException {
		try (FileOutputStream outputStream = new FileOutputStream(file, false)) {
			int read;
			byte[] bytes = new byte[DEFAULT_BUFFER_SIZE];
			while ((read = inputStream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}
//			outputStream.close();
		}
	}

    public Boolean deleteFile(String fileName) {
        try {
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            return Files.deleteIfExists(targetLocation);
        } catch (IOException ex) {
            throw new FileDeleteException("Could not delete file " + fileName + ". Please try again!", ex);
        }
    }
    
    // rename file in public folder
    public boolean rename(String oldName, String newName) {
    	boolean ok = false;
    	// rename actual version
    	File oldFile = new File(fileStorageProperties.getUploadDir() + "/" + oldName);
    	File newFile = new File(fileStorageProperties.getUploadDir() + "/" + newName);
    	boolean flag = oldFile.renameTo(newFile);
    	if(flag) ok = true;
    	
    	// rename file in version dir
		File[] dirs = new File(fileStorageProperties.getVersionDir()).listFiles();
		for(File dir: dirs) {
			if(dir.isDirectory()) {
				File[] fileVersions = new File(dir.getAbsolutePath()).listFiles();
				for(File fileVersion: fileVersions) {
					if(fileVersion.getAbsolutePath().equals(dir.getAbsolutePath() + "\\" + oldName)) {
						File f1 = new File(fileVersion.getAbsolutePath());
						File f2 = new File(dir.getAbsolutePath() + "\\" + newName);
						boolean flag2 = f1.renameTo(f2);
						if(flag2) ok = true;
					} else continue;
				}
			}
		}
		return ok;
    }
    
    // store file to version folder
    public boolean createFolderVersion(MultipartFile file, String versionName) {
    	String path = fileStorageProperties.getVersionDir() + "/" + versionName;
    	try {
			File dir = new File(path);
			if(dir.exists()) {
				System.out.println("1 " + dir);
				store(file, versionName);
				restore(file.getOriginalFilename(), versionName);
			} else {
				System.out.println("2 " + dir);
				dir.mkdir();
				store(file, versionName);
				restore(file.getOriginalFilename(), versionName);
			}
		} catch (Exception e) {
			throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", e);
		}
    	return false;
    }
    
    // in versions dir (type = public)
    public String store(MultipartFile file, String versionName) {
      // Normalize file name
      String fileName = StringUtils.cleanPath(file.getOriginalFilename());

      try {
          // Check if the file's name contains invalid characters
          if(fileName.contains("..")) {
              throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
          }

          // Copy file to the target location (Replacing existing file with the same name)
          Path targetLocation = Paths.get(fileStorageProperties.getVersionDir() + "/" + versionName).resolve(fileName);
          System.out.println(targetLocation);
          Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

          return targetLocation.toString();
      } catch (IOException ex) {
          throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
      }
    }
    
    // move document from version dir need to restore to upload dir (type = public)
    public String restore(String fileName, String versionName) {
    	try {
            // Check if the file's name contains invalid characters
            if(fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }
            
            Path sourceLocation = Paths.get(fileStorageProperties.getVersionDir() + "/" + versionName).resolve(fileName);
            Path targetLocation = Paths.get(fileStorageProperties.getUploadDir()).resolve(fileName);           	
            Files.copy(sourceLocation, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("source:" + sourceLocation.toString());
            System.out.println("target:" + targetLocation.toString());
            return targetLocation.toString();
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }
    
    public void makeDir(String path) {
    	File dir = new File(path);
    	if(!dir.exists()) {
    		dir.mkdirs();
    	}
    }
    
    // make private folder
    public void makePrivateFolder(String username) {
    	String doucumentFldPath = fileStorageProperties.getPrivateDir() + "/" + username + "/documents";
    	String versionFldPath = fileStorageProperties.getPrivateDir() + "/" + username + "/versions";
    	makeDir(doucumentFldPath);
    	makeDir(versionFldPath);
    }
    
    // rename document in private document
    public boolean renamePrivateDocument(String oldName, String newName, String username) {
    	boolean ok = false;
    	// rename actual version
    	File oldFile = new File(fileStorageProperties.getPrivateDir() + "/" + username + "/documents/" + oldName);
    	File newFile = new File(fileStorageProperties.getPrivateDir() + "/" + username + "/documents/" + newName);
    	boolean flag = oldFile.renameTo(newFile);
    	if(flag) ok = true;
    	
    	// rename file in version dir
		File[] dirs = new File(fileStorageProperties.getPrivateDir() + "/" + username + "/versions").listFiles();
		for(File dir: dirs) {//dir is version name
			if(dir.isDirectory()) {
				File[] fileVersions = new File(dir.getAbsolutePath()).listFiles();
				for(File fileVersion: fileVersions) {
					if(fileVersion.getAbsolutePath().equals(dir.getAbsolutePath() + "\\" + oldName)) {
						File f1 = new File(fileVersion.getAbsolutePath());
						File f2 = new File(dir.getAbsolutePath() + "\\" + newName);
						boolean flag2 = f1.renameTo(f2);
						if(flag2) ok = true;
					} else continue;
				}
			}
		}
		return ok;
    }
    
    // restore private document
    public String restorePrivateDocument(String fileName, String versionName, String username) {
    	try {
            // Check if the file's name contains invalid characters
            if(fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }
            
            Path sourceLocation = Paths.get(fileStorageProperties.getPrivateDir() + "/" + username + "/versions/" + versionName).resolve(fileName);
            Path targetLocation = Paths.get(fileStorageProperties.getPrivateDir() + "/" + username + "/documents").resolve(fileName);           	
            Files.copy(sourceLocation, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("source:" + sourceLocation.toString());
            System.out.println("target:" + targetLocation.toString());
            return targetLocation.toString();
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }
    
    // store file to private document
    public String storePrivateDocument(MultipartFile file, String username) {
    	Path targetLocation = Paths.get(fileStorageProperties.getPrivateDir() + "/" + username + "/documents")
    			.resolve(file.getOriginalFilename());
    	String location = storeT(file, targetLocation);
    	return location;
    }
    
    // store private version
    public String storePrivateVersion(MultipartFile file, String versionName, String username) {
    	Path targetLocation = Paths.get(fileStorageProperties.getPrivateDir() + "/" + username + "/versions/" + versionName)
      			.resolve(file.getOriginalFilename());
    	File tmp = new File(fileStorageProperties.getPrivateDir() + "/" + username + "/versions/" + versionName);
    	String location = "";
    	if(tmp.exists()) {
    		location = storeT(file, targetLocation);
    	} else {
    		tmp.mkdir();
    		location = storeT(file, targetLocation);
    	}
    	restorePrivateDocument(file.getOriginalFilename(), versionName, username);
    	return location;
    }
    
    // check file exist
    public boolean checkExist(String fileName) {
    	String path = fileStorageProperties.getUploadDir() + "/" + fileName;
    	File file = new File(path);
    	if(file.exists()) return true;
    	return false;
    }
    
    public String storeT(MultipartFile file, Path targetLocation) {
    	// Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if(fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return targetLocation.toString();
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }
}
