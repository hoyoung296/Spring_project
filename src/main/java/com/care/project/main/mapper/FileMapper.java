package com.care.project.main.mapper;

import java.util.List;

import com.care.project.main.dto.FileDTO;

public interface FileMapper {
   public void save(FileDTO dto);
   public List<FileDTO> getList();
   public int delete(String id);
   public FileDTO getData(String id);
   public void modify(FileDTO dto);
}