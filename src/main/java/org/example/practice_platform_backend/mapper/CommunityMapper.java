package org.example.practice_platform_backend.mapper;

import org.apache.ibatis.annotations.*;
import org.example.practice_platform_backend.entity.Community;
import org.example.practice_platform_backend.entity.Fruit;
import org.example.practice_platform_backend.entity.Project;
import org.springframework.transaction.annotation.Transactional;

@Mapper
public interface CommunityMapper {

    //注册社区
    @Insert("INSERT into community(community_name,introduction,address,is_pass,avatar_path) " +
            "values(#{community_name},#{introduction},#{address},0,'community_avatar/1775D1493E2547398D4FC613F8250219.jpeg')")
    @Options(useGeneratedKeys = true, keyProperty = "community_id")
    @Transactional
    void registerCommunity(Community community);

    //判断社区是否已经存在，不能重名
    @Select("SELECT count(*) from community where community_name = #{community_name}")
    int findCommunityIdByName(String community_name);

    //社区id 查社区名
    @Select("select community_name from community where community_id = #{community_id}")
    String getCommunityName(int community_id);

    //社区id 查社区头像
    @Select("select avatar_path from community where community_id = #{community_id}")
    String getCommunityAvatarPath(int community_id);

    //社区id 查相关成果
    @Select("Select * from fruit_info where project_id = " +
            "(select project_id from succ_project where need_id = " +
            "(select need_id from community_need where community_id = #{community_id}))" +
            "LIMIT 2 OFFSET #{offset}")
    Fruit[] getCommunityFruits(int community_id, int offset);

    //负责人 id 查找社区
    @Select("select community_id from community where user_id = #{user_id}")
    int findCommunityIdByUserId(@Param("user_id") int user_id);

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

}
