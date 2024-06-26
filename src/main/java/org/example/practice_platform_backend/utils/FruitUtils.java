package org.example.practice_platform_backend.utils;

import com.alibaba.fastjson.JSON;
import com.google.gson.JsonArray;
import lombok.Setter;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.example.practice_platform_backend.entity.Comment;
import org.example.practice_platform_backend.entity.Fruit;
import org.example.practice_platform_backend.entity.FruitMedia;
import org.example.practice_platform_backend.mapper.CommentMapper;
import org.example.practice_platform_backend.mapper.FruitMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Setter
@Component
public class FruitUtils {
    @Autowired
    private ImageUtils imageUtils;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private FruitMapper fruitMapper;

    @Value("${uploadPath}")
//    private String uploadPath = "D:/Desktop/Processing/终极实训/Social_Practice_Platform_backend/uploadfiles";
     private String uploadPath;
    /**
     * 返回成果查询结果
     * @param fruit
     * @param Comments
     * @param fruitMedias
     * @param unlikeflag
     * @return
     * @throws IOException
     */
    public JSONObject getFruitInfo(Fruit fruit, Comment[] Comments,
                                   FruitMedia[] fruitMedias, boolean unlikeflag) throws IOException {
        JSONObject result =  new JSONObject();
        JSONArray mediaList = new JSONArray();
        JSONArray commentList = new JSONArray();
        result.put("address", fruit.getPosition());
        result.put("date", fruit.getDate());
        System.out.println(fruit.getDate());
        result.put("res_intro", fruit.getIntroduction());
        result.put("like", String.valueOf(fruit.getKudos_num()));
        result.put("comment", String.valueOf(fruit.getComment_num()));
        for(FruitMedia fruitMedia : fruitMedias){
            JSONObject media = new JSONObject();
            if(fruitMedia.getType().equals("img") ||  fruitMedia.getType().equals("cover")){
                media.put("img_flag", 0);
                media.put("src", imageUtils.getFileBytes(uploadPath + "/" + fruitMedia.getPath()));
            }
            else{
                media.put("img_flag", 1);
                media.put("src",  fruitMedia.getPath());
            }
            mediaList.add(media);
        }
        result.put("imgList", mediaList);
        for(int i=0;i<2;i++){
            JSONObject comment = new JSONObject();
            Map<String, String> user_info = commentMapper.getAvatarPathByUserId(Comments[i].getUser_id());
            comment.put("avatar", imageUtils.getFileBytes(uploadPath + "/" + user_info.get("avatar_path")));
            comment.put("date", Comments[i].getComment_time());
            comment.put("user_name", user_info.get("username"));
            comment.put("content", Comments[i].getContent());
            commentList.add(comment);
        }
        result.put("comment_list", commentList);
        result.put("unlike_flag", unlikeflag);
        return result;
    }
}
