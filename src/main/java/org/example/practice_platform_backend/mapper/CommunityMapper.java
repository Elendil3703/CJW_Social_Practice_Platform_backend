package org.example.practice_platform_backend.mapper;

import org.apache.ibatis.annotations.*;
import org.example.practice_platform_backend.entity.Community;
import org.example.practice_platform_backend.entity.Fruit;
import org.example.practice_platform_backend.entity.Project;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface CommunityMapper {

    //注册社区
    @Insert("INSERT into community(community_name,introduction,address,is_pass,user_id,avatar_path) " +
            "values(#{community_name},#{introduction},#{address},0,#{user_id},'community_avatar/default_avatar.jpg')")
    @Options(useGeneratedKeys = true, keyProperty = "community_id")
    @Transactional
    void registerCommunity(Community community);

    //判断社区是否已经存在，不能重名
    @Select("SELECT count(*) from community where community_name = #{community_name}")
    int findCommunityIdByName(String community_name);

    //社区 id 获取社区基本信息
    @Select("select community_id,community_name,introduction,address,user_id from community where community_id=#{community_id}")
    Community getCommunityById(int community_id);

    //获取社区的所有媒体列表
    @Select("select path,type from community_media where community_id = #{community_id}")
    List<HashMap<String,String>> getCommunityMedia(int community_id);

    //社区id 查社区名
    @Select("select community_name from community where community_id = #{community_id}")
    String getCommunityName(int community_id);

    //社区id 查社区头像
    @Select("select avatar_path from community where community_id = #{community_id}")
    String getCommunityAvatarPath(int community_id);

    //社区id 查相关成果
    @Select("Select * from fruit_info where project_id = " +
            "(select project_id from succ_project where need_id in " +
            "(select need_id from community_need where community_id = #{community_id}))" +
            "LIMIT 2 OFFSET #{offset}")
    Fruit[] getCommunityFruits(int community_id, int offset);

    //负责人 id 查找社区
    @Select("select community_id from community where user_id = #{user_id}")
    Integer findCommunityIdByUserId(@Param("user_id") int user_id);

    // 获取封面 id
    @Select("SELECT media_id FROM community_media WHERE community_id = #{communityId} AND type = 'cover'")
    int existsCommunityCover(@Param("communityId") int communityId);

    // 检查社区图片是否超过限制
    @Select("SELECT COUNT(*) FROM community_media WHERE community_id = #{communityId} AND type != 'video'")
    int existsCommunityImage(@Param("communityId") int communityId);

    // 添加社区图片（非封面）
    @Insert("insert into community_media(path,community_id,type) " +
            "values(#{path},#{community_id},'image')")
    boolean addCommunityImage(@Param("path") String path, @Param("community_id") int community_id);

    // 添加社区图片（封面）
    @Insert("insert into community_media(path,community_id,type) " +
            "values(#{path},#{community_id},'image')")
    boolean addCommunityCover(@Param("path") String path, @Param("community_id") int community_id);


    // 检查是否存在社区视频
    @Select("SELECT path FROM community_media WHERE community_id = #{communityId} AND type = 'video'")
    String existsCommunityVideo(@Param("communityId") int communityId);

    // 添加社区视频
    @Insert("insert into community_media(path,community_id,type) " +
            "values(#{path},#{community_id},'video')")
    boolean addCommunityVideo(@Param("path") String path, @Param("community_id") int community_id);

    // 删除社区视频
    @Delete("DELETE from community_media where community_id = #{id} and type = 'video'")
    boolean deleteCommunityVideo(@Param("id") int id);

    //查询是否有相应 user_id 的社区
    @Select("select COUNT(*) from community where user_id = #{user_id}")
    int existsCommunityUser(@Param("user_id") int user_id);

    //统计各个市的社区
    @Select("""
            SELECT
                CONCAT(SUBSTRING_INDEX(address, '市', 1), '市') as city,
                COUNT(*) AS sum
            FROM
                community_need
            JOIN
                succ_project ON community_need.need_id = succ_project.need_id
            GROUP BY
                SUBSTRING_INDEX(address, '市', 1);
            """)
    List<HashMap<String,Integer>> getCommunityCountsByAddress();

}
