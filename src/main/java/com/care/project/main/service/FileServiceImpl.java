package com.care.project.main.service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.care.project.main.dto.FileDTO;
import com.care.project.main.mapper.FileMapper;

@Service
public class FileServiceImpl implements FileService {

   @Autowired
   FileMapper mapper;

   public void fileProcess(MultipartFile file, String id, String name) {
      FileDTO dto = new FileDTO();
      dto.setId(id);
      dto.setName(name);
      
      if(!file.isEmpty()) {//file.isEmpty() == false
         SimpleDateFormat fo = new SimpleDateFormat("yyyyMMddHHmmss-");
         //sysFileName = 20250113105710-
         String sysFileName = fo.format(new Date());
         //sysFileName = 20250113105710-한기대시간표.png
         sysFileName += file.getOriginalFilename();
         
         dto.setImgFileName(sysFileName);
         
         File f = new File(IMG_REPO + "/" + sysFileName);
         try {
            file.transferTo(f);
         } catch (Exception e) {
            e.printStackTrace();
         }
      }else {
         dto.setImgFileName("nan");
      }
      try {
         mapper.save(dto);
      }catch(Exception e) {
         e.printStackTrace();
      }
   }
   public List<FileDTO> getList() {
      return mapper.getList();
   }
   public void delete(String file, String id) {
      int result = 0;
      try {
         result = mapper.delete(id);
         if (result == 1) {
            File d = new File(IMG_REPO + "/" + file);
            if (d.exists()) {
               d.delete();
            }
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
   public FileDTO getData(String id) {
      return mapper.getData(id);
   }
   public void modify(MultipartFile file, String origin, FileDTO dto) {
      //System.out.println("file : " +file);
      //System.out.println("origin : " +origin);
      //System.out.println("dto id : " +dto.getId());
      //System.out.println("dto name : " +dto.getName());
      if(file.isEmpty()) {
         dto.setImgFileName(origin);
      }else {
         SimpleDateFormat fo = new SimpleDateFormat("yyyyMMddHHmmss-");
         String sysFileName = fo.format(new Date());
         sysFileName += file.getOriginalFilename();
         
         dto.setImgFileName(sysFileName);
         
         File f = new File(IMG_REPO + "/" + sysFileName);
         try {
            file.transferTo(f);
            File d = new File(IMG_REPO + "/" + origin);
            d.delete();
         } catch (Exception e) {
            e.printStackTrace();
         }
         mapper.modify(dto);
      }
   }
}