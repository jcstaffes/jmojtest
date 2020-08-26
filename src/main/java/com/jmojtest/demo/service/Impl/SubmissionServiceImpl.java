package com.jmojtest.demo.service.Impl;

import com.jmojtest.demo.dao.JudgetestDOMapper;
import com.jmojtest.demo.dao.SubmissionDOMapper;
import com.jmojtest.demo.dataobject.JudgetestDO;
import com.jmojtest.demo.dataobject.SubmissionDO;
import com.jmojtest.demo.service.SubmissionService;
import com.jmojtest.demo.util.ExcuUtli;
import com.jmojtest.demo.util.SaveUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class SubmissionServiceImpl implements SubmissionService {
    @Autowired
    private SubmissionDOMapper submissionDOMapper;
    @Autowired
    private JudgetestDOMapper judgetestDOMapper;

    @Override
    public String fileupload(String email,int pid,MultipartFile file) throws Exception {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        int j=1;
        String FileID="Q"+pid+"_"+email+"_"+j;//文件编号
        System.out.println(FileID);
        while (submissionDOMapper.selectbySID(FileID)!=null){
            j++;
            FileID="Q"+pid+"_"+email+"_"+j;//文件编号
        }
        String time="";
        String memory="";
        String compile="";
        String pass="";
        System.out.println(FileID);
        JudgetestDO judgetestDO=judgetestDOMapper.selectByPrimaryKey(String.valueOf(pid));
        String testcase=judgetestDO.getContent();
        String answer=judgetestDO.getAnswer();
        System.out.println("testcase:"+testcase);
        System.out.println("answer:"+answer);
        if (!SaveUtil.isFileAllowed(extension)){
            return "The file extension is error";
        }//判断是否是java文件
        try{
//            Files.copy(file.getInputStream(),new File(SaveUtil.saveDir+FileID+".java").toPath(),
//                    StandardCopyOption.REPLACE_EXISTING);//存储原始文件
            Files.copy(file.getInputStream(),new File(SaveUtil.saveDir+"Main.java").toPath(),
                    StandardCopyOption.REPLACE_EXISTING);//复制一份并且改名Main文件作为编译文件
            submissionDOMapper.insertSubid(FileID,pid,0,0,0,0,email);//先记录submission id
            String[] ans= ExcuUtli.Exec(FileID,testcase);
            submissionDOMapper.updateifcompile(1,FileID);//编译成功
            compile="Compile success.";
            System.out.println("compile success");
            submissionDOMapper.updatetime(Integer.parseInt(ans[1]),FileID);//更新时间
            submissionDOMapper.updatememory(Integer.parseInt(ans[2]),FileID);//更新内存
            System.out.println("output:"+ans[0]);
            time="Time:"+ans[1]+"ms ";
            memory="Memory:"+ans[2]+"kb";
            int i=1;
            boolean ifhaspass=false;
            String submissionID="Q"+pid+"_"+email+"_"+i;//id已包含题目和邮箱
            while (submissionDOMapper.selectbySID(submissionID)!=null){
                SubmissionDO submissionDO=submissionDOMapper.selectbySID(submissionID);
                if (submissionDO.getEmail().equals(email)&&submissionDO.getProblemid()==pid&&submissionDO.getIfPass()==1){
                    ifhaspass=true;
                    break;
                }
                i++;
                submissionID="Q"+pid+"_"+email+"_"+i;
            }
            System.out.println("是否已经有通过记录："+ifhaspass);
            if (ans[0].equals(answer)){
                submissionDOMapper.updateifpass(1,FileID);
                pass="Pass.";
                System.out.println("pass");
            }else {
                submissionDOMapper.updateifpass(0,FileID);
                pass="Not pass.";
                System.out.println("not pass");
            }
        }catch (IOException e){
        }
        String output="Q"+pid+" has been submitted!Submission id is "+FileID+"."+compile+pass+time+memory;
        return output;
    }

    @Override
    public String checklist(String sid) throws Exception {
        SubmissionDO submissionDO=null;
        try {
            submissionDO=submissionDOMapper.selectbySID(sid);
        }catch (Exception e){
            throw new Exception("submission doesn't exist");
        }
        String str="id:"+submissionDO.getIdsub()+" problemid:"+submissionDO.getProblemid()+" useremail:"+submissionDO.getEmail()+
                " time_used:"+submissionDO.getTimeUsed()+"ms"+" memory_used:"+submissionDO.getMemoryUsed()+"kb";
        return str;
    }
}
