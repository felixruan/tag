package org.etocrm.authentication.util;

import com.chinanetcenter.api.entity.HttpClientResult;
import com.chinanetcenter.api.exception.WsClientException;
import com.chinanetcenter.api.util.Config;
import com.chinanetcenter.api.wsbox.FileUploadManage;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;


@Slf4j
public class UpLoadUtil {


    //上传文件到网宿工具
    @SneakyThrows
    public static String getNewPath(MultipartFile file) {
        if (file != null && file.getOriginalFilename() != null && file.getSize() > 0) {

            Config.AK = "979d020f4cc2383352e87d2c86f4a4c7a019a67c";
            Config.SK = "bcfae3ac6cede8880b294012d7c462f9eed25682";
            Config.PUT_URL = "http://qishu.up21.v1.wcsapi.com";
            String newfileName = getNewfileName(file);
            String bucketName = "basewoaap";
            String fileKey = "tag/" + newfileName;
            String fileKeyWithFolder = "tag" + File.separatorChar + newfileName;
            String url = "https://wscdn.woaap.com/" + fileKey;
            UpLoadUtil ulu = new UpLoadUtil();
            try (FileInputStream in = (FileInputStream) file.getInputStream()) {
                FileUploadManage fileUploadManage = new FileUploadManage();
                HttpClientResult result = fileUploadManage.uploadForAutoMimeType(bucketName, fileKey, in);
                int status = result.getStatus();
                if (status == 200) {
                    return url;
                } else {
                    log.info("上传图片失败：" + result.getStatus() + ":" + result.getResponse());
                    return "erro";
                }
            } catch (Exception e) {
                log.info("上传图片失败:" + e.getMessage());
                return "erro";
            }

        } else {
            log.info("上传图片图片文件为空！");
            return "erro";
        }
    }

    //通过路径上传到服务器
    @SneakyThrows
    public static String getNewPathByPath(String path) {

        Config.AK = "979d020f4cc2383352e87d2c86f4a4c7a019a67c";
        Config.SK = "bcfae3ac6cede8880b294012d7c462f9eed25682";
        Config.PUT_URL = "http://qishu.up21.v1.wcsapi.com";
        String newfileName = UUID.randomUUID() + path.substring(path.lastIndexOf("."));
        String bucketName = "basewoaap";
        String fileKey = "alg/" + newfileName;
        String fileKeyWithFolder = "alg" + File.separatorChar + newfileName;
        String url = "https://wscdn.woaap.com/" + fileKey;
        UpLoadUtil ulu = new UpLoadUtil();
        File file = new File(path);
        try (FileInputStream in = new FileInputStream(file)) {
            FileUploadManage fileUploadManage = new FileUploadManage();
            HttpClientResult result = fileUploadManage.uploadForAutoMimeType(bucketName, fileKey, in);
            int status = result.getStatus();
            if (status == 200) {
                return url;
            } else {
                log.info("上传图片失败：" + result.getStatus() + ":" + result.getResponse());
                return "erro";
            }
            //ulu.uploadFileForAutoMimeType(bucketName, fileKey, in);

        } catch (IOException e) {
            log.info("上传图片失败:" + e.getMessage());
            return "erro";
        }
    }

    /**
     * 通过文件流上传文件，方法里会关闭InputStream，会自动识别文件类型
     * 默认覆盖上传
     */
    public void uploadFileForAutoMimeType(String bucketName, String fileKey, InputStream in) {
        try {
            FileUploadManage fileUploadManage = new FileUploadManage();
            HttpClientResult result = fileUploadManage.uploadForAutoMimeType(bucketName, fileKey, in);
            log.info("上传图片成功：" + result.getStatus() + ":" + result.getResponse());
        } catch (WsClientException e) {
            log.info("上传图片失败:" + e.getMessage());
            e.printStackTrace();

        }
    }


    //生成新的文件名
    public static String getNewfileName(MultipartFile file) {
        if (file != null && file.getOriginalFilename() != null && file.getSize() > 0) {
            //获取文件名
            String originalFilename = file.getOriginalFilename();
            //生成新的文件名
            String newFileName = UUID.randomUUID() + originalFilename.substring(originalFilename.lastIndexOf("."));
            return newFileName;
        }
        return null;

    }

    //关闭文件流
    public static void closStream(InputStream is) throws IOException {
        is.close();
    }
}
