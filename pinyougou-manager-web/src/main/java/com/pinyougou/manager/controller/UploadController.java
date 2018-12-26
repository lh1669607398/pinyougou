package com.pinyougou.manager.controller;

import com.pinyougou.entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import util.FastDFSClient;
//文件上传

@RestController
public class UploadController {
    @Value("${FILE_SERVER_URL}")
    private String  file_server_url;

    @RequestMapping("/upload")
    public Result upload(MultipartFile file){
        String filename = file.getOriginalFilename();//获取文件名
        String name = filename.substring(filename.lastIndexOf(".") + 1);//获取扩展名
        try {
            FastDFSClient dfsClient= new FastDFSClient("D:\\JAVA\\pejet\\pinyougou-parent\\pinyougou-shop-web\\src\\main\\resources\\config\\fdfs_client.conf");
            String fileName = dfsClient.uploadFile(file.getBytes(), name);
            String url=file_server_url+fileName;  //图片完整路径
            return new Result(true,url);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传失败");
        }
    }
}
