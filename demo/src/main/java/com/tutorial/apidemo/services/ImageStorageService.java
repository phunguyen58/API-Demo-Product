package com.tutorial.apidemo.services;

import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class ImageStorageService implements IStorageService{
    private final Path storageFolder = Paths.get("uploads");
    //constructor
    public ImageStorageService(){
        try {
            Files.createDirectories(storageFolder);
        } catch(IOException exception){
            throw new RuntimeException("Cannot initialize storage", exception);
        }
    }
    private boolean isImage(MultipartFile file){
        //Let's install FileNameUtils
        String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
        fileExtension = fileExtension.trim().toLowerCase();

        return Arrays.asList(new String[] {"png", "jpg", "jpeg", "bmp"}).contains(fileExtension.trim().toLowerCase());

        // checking file name is the extension satisfied or not
       // return fileExtension == "png" || fileExtension == "jpg" || fileExtension == "jpeg" || fileExtension == "bmp";

    }

    @Override
    public String storeFile(MultipartFile file) {
        try{
            System.out.println("haha");
            if(file.isEmpty()){
                throw new RuntimeException("Failed to store empty file");
            }
            //check file is image?
            if(!isImage(file)){
                throw new RuntimeException("You can upload image file");
            }
            //file must be <= 5mb
            float fileSizeInMegabytes = file.getSize() / 1_000_000.0f;
            if(fileSizeInMegabytes >= 5.0f){
                throw new RuntimeException("You can't upload file having the size bigger than 5MB");
            }
            //rename file before upload to server
            String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
            String generatedFileName = UUID.randomUUID().toString().replace("-", "");
            generatedFileName = generatedFileName+"."+fileExtension;
            Path destinationFilePath = this.storageFolder.resolve(Paths.get(generatedFileName))
                    .normalize().toAbsolutePath();
            if (!destinationFilePath.getParent().equals(this.storageFolder.toAbsolutePath())){
                throw new RuntimeException("Can't store file outside current directory");
            }
            try (InputStream inputStream = file.getInputStream()){
                Files.copy(inputStream, destinationFilePath, StandardCopyOption.REPLACE_EXISTING);
            }
            return generatedFileName;
        } catch(IOException exception){
            throw new RuntimeException("Failed to store file", exception);
        }

    }

    @Override
    public Stream<Path> loadAll() {
        try{
            //list all files in storageFolder : just image file
            return Files.walk(this.storageFolder, 1)
                    .filter(path ->{
                        return !path.equals(this.storageFolder) && !path.toString().contains("._");
                    })
                    .map(this.storageFolder::relativize);
        } catch (IOException exception){
            throw new RuntimeException("Failed to load stored files", exception);
        }
    }

    @Override
    public byte[] readFileContent(String fileName) {
        try {
            Path file = storageFolder.resolve(fileName);
            Resource resource = new UrlResource(file.toUri());
            if(resource.exists() || resource.isReadable()){
                byte[] bytes = StreamUtils.copyToByteArray(resource.getInputStream());
                return bytes;
            } else {
                throw new RuntimeException("Couldn't read file: " + fileName);
            }

        } catch (IOException exception){
            throw new RuntimeException("Couldn't read file: " + fileName, exception);
        }
    }

    @Override
    public void deleteAllFiles() {

    }
}
