package com.baisaiju.springboot.controller;

<<<<<<< HEAD
import com.baisaiju.springboot.dao.CompetitionTemplate;
import com.baisaiju.springboot.dao.SearchTemplate;
import com.baisaiju.springboot.dao.UserTemplate;
import com.baisaiju.springboot.entities.Competition;
import com.baisaiju.springboot.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
            System.out.println(re);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(result.toString());
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
=======
import com.baisaiju.springboot.dao.UserTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class UserController {

    @Autowired
    private UserTemplate userTemplate;
>>>>>>> 6ff4a82f72e7f324976de3ecd6ec66662d253660


}
