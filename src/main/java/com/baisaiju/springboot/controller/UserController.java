package com.baisaiju.springboot.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.baisaiju.springboot.dao.CompetitionTemplate;
import com.baisaiju.springboot.dao.SearchTemplate;
import com.baisaiju.springboot.dao.UserTemplate;
import com.baisaiju.springboot.entities.Competition;
import com.baisaiju.springboot.entities.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author dav1d
 */
@Controller
public class UserController {
    @Autowired
    private UserTemplate userTemplate;
    @Autowired
    private CompetitionTemplate competitionTemplate;
    @Autowired
    SearchTemplate searchTemplate;

    @ResponseBody
    @PostMapping("/getOpenId")
    public String getOpenId(@RequestBody Map<String, Object> data) throws IOException {
        String code = data.get("code").toString();
        System.out.println(code);
        File dir = new File("");
        List<String> result = new ArrayList<>();
        try {
            System.out.println(dir.getAbsolutePath().replace("\\", "\\\\"));
            Process process = Runtime.getRuntime()
                    .exec("python " + dir.getAbsolutePath() + "/python/getBsjOpenid.py" + " " + code);
            process.waitFor();
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
            BufferedReader in1 = new BufferedReader(new InputStreamReader(process.getErrorStream(), "UTF-8"));
            String line = null;
            while ((line = in.readLine()) != null) {
                result.add(line);
                System.out.println(line);
            }
            while ((line = in1.readLine()) != null) {
                System.out.println(line);
            }
            in.close();
            // java代码中的process.waitFor()返回值为0表示我们调用python脚本成功，
            // 返回值为1表示调用python脚本失败，这和我们通常意义上见到的0与1定义正好相反
            int re = process.waitFor();
            //System.out.println(re);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println(result.toString());
        return result.get(0);
    }

    @ResponseBody
    @PostMapping("/getFavorite")
    public List<Competition> getFavorite(@RequestBody Map<String, Object> data) {
        return userTemplate.getFavorite(data);
    }

    @ResponseBody
    @PostMapping("/judgeUser")
    public User judgeUser(@RequestBody Map<String, Object> data) {
        return userTemplate.findByOpenId(data.get("openId").toString());
    }

    @ResponseBody
    @PostMapping("/newUser")
    public String newUser(@RequestBody Map<String, Object> data) {
        userTemplate.newUser(data);
        searchTemplate.newUser(data);
        return "Success";
    }

    @ResponseBody
    @PostMapping("/addFavorite")
    public String addFavorite(@RequestBody Map<String, Object> data) {
        userTemplate.addFavorite(data);
        return "Success";
    }

    @ResponseBody
    @PostMapping("/delFavorite")
    public String delFavorite(@RequestBody Map<String, Object> data) {
        userTemplate.delFavorite(data);
        return "Success";
    }

    @ResponseBody
    @PostMapping("/upload")
    public String upload(HttpServletRequest request,
                         @RequestParam("competitionName") String  competitionName,
                         @RequestParam("introduction") String  introduction,
                         @RequestParam("file") MultipartFile file) throws Exception{
        //接收参数description
        System.out.println("competitionName: " + competitionName);
        System.out.println(file.toString());
        //如果文件不为空，写入上传路径
        // if (!file.isEmpty()){
        //     //上传文件路径
        //     String path = request.getServletContext().getRealPath("/upload/");
        //     System.out.println("path = " + path);
        //     //上传文件名
        //     String filename = file.getOriginalFilename();
        //     File filePath = new File(path, filename);
        //     //判断路径是否存在，如果不存在就创建一个
        //     if (!filePath.getParentFile().exists()){
        //         filePath.getParentFile().mkdirs();
        //     }
        //     //将上传文件保存到一个目标文件当中
        //     file.transferTo(new File(path+File.separator + filename));
        //     System.out.println(filename);
        //     return "success";
        // }else {
        //     return "error";
        // }
        return "test";
    }


}
