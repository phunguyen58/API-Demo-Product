package com.tutorial.apidemo.controller;

import com.tutorial.apidemo.ResponseObject;
import com.tutorial.apidemo.services.IStorageService;
import org.apache.catalina.LifecycleState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping(path= "/api/v1/FileUpload")
public class FileUploadController {
    //Inject Storage Service here
    @Autowired
    private IStorageService storageService;
    //This controller receives file/image from client
    @PostMapping("")
    public ResponseEntity<ResponseObject> uploadFile(@RequestParam("File")MultipartFile file) {
        try {
            //saving file to a folder => using a service
            String generatedFileName = storageService.storeFile(file);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("ok", "Upload file successfully", generatedFileName)
            );
            //fb1a0a22cec64bb0b4da12d2d64d48a7.jpg => how to open this file Web browser?
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                    new ResponseObject("failed", exception.getMessage(), "")
            );
        }
    }
    //get image's url
    @GetMapping("/files/{fileName:.+}")
    ///api/v1/FileUpload/files/fb1a0a22cec64bb0b4da12d2d64d48a7.jpg
    public ResponseEntity<byte[]> readDetailFile(@PathVariable String fileName){
        try {
            byte[] bytes = storageService.readFileContent(fileName);
            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.IMAGE_JPEG).body(
                    bytes
            );
        } catch (Exception exception){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }
    //How to load all uploaded files?
    @GetMapping("")
    public ResponseEntity<ResponseObject> getUploadedFiles(){
        try{
            List<String> urls = storageService.loadAll()
                    .map(path -> {
                        String urlPath = MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                                "readDetailFile", path.getFileName().toString()).build().toUri().toString();
                        return urlPath;
                    })
                    .collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("ok", "List files is successfully", urls)
            );
        } catch(Exception exception){
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                    new ResponseObject("ok", "List files is failed", new String[] {})
            );
        }
    }
}
