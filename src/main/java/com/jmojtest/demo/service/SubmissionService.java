package com.jmojtest.demo.service;

import org.springframework.web.multipart.MultipartFile;

public interface SubmissionService {
    String fileupload(String email,int pid,MultipartFile file)throws Exception;
    String checklist(String sid)throws Exception;
}
