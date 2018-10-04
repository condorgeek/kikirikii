/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [FileUploadController.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 04.10.18 11:34
 */

package com.kikirikii.controllers;

import com.kikirikii.exceptions.StorageFileNotFoundException;
import com.kikirikii.model.Media;
import com.kikirikii.storage.StorageProperties;
import com.kikirikii.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.logging.Logger;

@RestController
@RequestMapping("/user/{userName}")
@CrossOrigin(origins = {"*"})
public class FileUploadController {
    public static Logger logger = Logger.getLogger("FileUploadController");

    @Autowired
    private StorageService storageService;

    @Autowired
    private StorageProperties storageProperties;

    @RequestMapping(value = "/posts/upload", method = RequestMethod.POST)
    public Media handleFileUpload(@PathVariable String userName, @RequestParam("file") MultipartFile file,
                                  @RequestParam("text") String text) {

        String location = storageService.storeAtLocation(file, userName);
        return Media.of(location, Media.Type.PICTURE);
    }

    @RequestMapping(value ="/avatar/upload", method = RequestMethod.POST)
    public String handleProfileUpload(@PathVariable String userName, @RequestParam("file") MultipartFile file) {
        return storageService.storeAtLocation(file, userName + storageProperties
                .getLocation().getProfile());
    }

    @RequestMapping(value ="/cover/upload", method = RequestMethod.POST)
    public String handleCoverUpload(@PathVariable String userName, @RequestParam("file") MultipartFile file) {
        return storageService.storeAtLocation(file, userName + storageProperties
                .getLocation().getCover());
    }

    @RequestMapping(value = "/validate/authorization", method = RequestMethod.GET)
    public ResponseEntity<HttpStatus> validateAuthorization() {
        return ResponseEntity.ok().build();
    }


    static class UploadForm {
        private String text;
        private MultipartFile[] files;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public MultipartFile[] getFiles() {
            return files;
        }

        public void setFiles(MultipartFile[] files) {
            this.files = files;
        }
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException e) {
        return ResponseEntity.notFound().build();
    }

}
