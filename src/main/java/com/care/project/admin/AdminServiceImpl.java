package com.care.project.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class AdminServiceImpl implements AdminService {
    @Autowired
    AdminMapper adminMapper;
}