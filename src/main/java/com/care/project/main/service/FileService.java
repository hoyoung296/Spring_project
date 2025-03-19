package com.care.project.main.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.care.project.main.dto.FileDTO;

public interface FileService {
   public static String IMG_REPO = "D:/movieProjectBack/src/main/webapp/resources/uploads";
   public void fileProcess(MultipartFile file, String id, String name);
   public List<FileDTO> getList();
   public void delete(String file, String id);
   public FileDTO getData(String id);
   public void modify(MultipartFile file, String origin, FileDTO dto);
}
