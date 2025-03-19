package com.care.project.main.controller;

import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.care.project.main.dto.FileDTO;
import com.care.project.main.service.FileService;

@Controller
public class FileController {
   @Autowired FileService fs;
   
   @GetMapping("form")
   public String form() {
      return "uploadForm";
   }
   @PostMapping("upload")
   public String upload(MultipartHttpServletRequest mul,
         String id, String name, MultipartFile file) {
      fs.fileProcess(file, id, name);
      
      return "redirect:form";
   }
   @GetMapping("views")
   public String views(Model model) {
      model.addAttribute("list", fs.getList());
      return "result";
   }
   @GetMapping("download")
   public void download(@RequestParam("file") String fileName,
         HttpServletResponse res)  throws Exception {
      res.addHeader("Content-disposition", "attachment; fileName="+URLEncoder.encode(fileName, StandardCharsets.UTF_8));
      File file = new File(FileService.IMG_REPO + "/" + fileName);
      FileInputStream in = new FileInputStream(file);
      FileCopyUtils.copy(in, res.getOutputStream());
      in.close();
      
   }
   @GetMapping("delete")
   public String delete(String file, String id) {
      fs.delete(file, id);
      return "redirect:views";
   }
   @GetMapping("modify_form")
   public String modify_form(Model model, String id) {
      model.addAttribute("info", fs.getData(id));
      return "modify_form";
   }
   @PostMapping("modify")
   public String modify(MultipartFile file, String origin, FileDTO dto) {
      fs.modify(file, origin, dto);
      return "redirect:views";
   }
}
