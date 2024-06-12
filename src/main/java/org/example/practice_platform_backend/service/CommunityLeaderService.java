package org.example.practice_platform_backend.service;

import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.example.practice_platform_backend.entity.CommunityLeader;
import org.example.practice_platform_backend.entity.User;
import org.example.practice_platform_backend.mapper.CommitteeMapper;
import org.example.practice_platform_backend.mapper.UserMapper;
import org.example.practice_platform_backend.utils.ImageUtils;
import org.example.practice_platform_backend.utils.JwtUtils;
import org.example.practice_platform_backend.utils.RandomGenerateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Objects;

@Service
public class CommunityLeaderService {

    // 错误日志
    private static final Logger LOGGER = LoggerFactory.getLogger(CommunityLeaderService.class);

    @Autowired
    private CommitteeMapper committeeMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ImageUtils imageUtils;

    @Autowired
    private RandomGenerateUtils randomGenerateUtils;

    @Value("${uploadPath}")
    private String uploadPath;

    public boolean checkIdentity(int userId) {
        return Objects.equals(committeeMapper.getUserCategory(userId), "committee");
    }

    // 获取所有社区负责人
    public List<CommunityLeader> getCommunityLeaders() throws IOException {
        List<CommunityLeader> leaders = committeeMapper.getCommunityLeader();
        return leaders.stream().peek(leader -> {
            try {
                String path = uploadPath + leader.getImg();
                leader.setImg(imageUtils.getFileBytes(path));
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }
        }).collect(Collectors.toList());
    }

    //  修改社区负责人
    @Transactional
    public Map<String, String> modifyCommunityLeader(Map<String, String> requestData) throws BadHanyuPinyinOutputFormatCombination {
        String user_name = randomGenerateUtils.generateRandomUserName(requestData.get("name"), 4);
        String passwd = "123456";
        String md5Pass = DigestUtils.md5DigestAsHex(passwd.getBytes());

        User user = new User();
        user.setName(requestData.get("name"));
        user.setUser_name(user_name);
        user.setPassword(md5Pass);
        user.setGender(requestData.get("gender"));
        user.setPhone_number(requestData.get("phone"));
        user.setUser_category("committee");
        userMapper.register(user);

        boolean updateSuccessful = committeeMapper.updateCommunityLeader(Integer.parseInt(requestData.get("id")),user.getUser_id());
        boolean deleteSuccessful = updateSuccessful && committeeMapper.deleteCommunityLeader(Integer.parseInt(requestData.get("id")));

        if (updateSuccessful && deleteSuccessful) {
            Map<String, String> result = new HashMap<>();
            result.put("user_name", user_name);
            result.put("password", passwd);
            return result;
        } else {
            throw new IllegalStateException("服务器删除出错");
        }
    }
}