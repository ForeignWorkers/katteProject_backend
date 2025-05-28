package me.soldesk.katteproject_backend.service;

import common.bean.content.ContentShortformBean;
import common.bean.content.ContentStyleBean;
import common.bean.content.ContentStyleComment;
import me.soldesk.katteproject_backend.mapper.ContentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContentService {
    @Autowired
    private ContentMapper contentMapper;

    public void addShortform(ContentShortformBean contentShortformBean) {
        contentMapper.addShortform(contentShortformBean);
    }

    public ContentShortformBean getShortformById(int id) {
        return contentMapper.getShortformById(id);
    }

    public List<ContentShortformBean> getShortformsByProjectId(int projectId, int count, int offset) {
        return contentMapper.getShortformByProductId(projectId, count, offset);
    }

    public void addStyle(ContentStyleBean contentStyleBean) {
       contentMapper.addStyle(contentStyleBean);
    }

    public void addHashtag(String hashtag) {
        contentMapper.addHashtag(hashtag);
    }

    public Integer getHashtagIdByName(String hashtag) {
        return contentMapper.getHashtagIdByName(hashtag);
    }

    public void addStyleJoinHashtag(int contentId, int hashtagId) {
        contentMapper.addStyleJoinHashTag(contentId,hashtagId);
    }

    //해쉬 코드가 현재 존재하는지 체크함
    public int getHashtagIdOrInsert(String hashtag) {
        Integer id = getHashtagIdByName(hashtag);
        if (id == null) {
            addHashtag(hashtag);
            id = contentMapper.getHashtagIdByName(hashtag); // 다시 가져옴
        }
        return id;
    }

    //Style과 해쉬태그를 연결해주는 Join 태이블 만들어줌
    public void linkStyleWithHashtag(int styleContentId, List<String> hashtags) {
        for (String tag : hashtags) {
            int hashtagId = getHashtagIdOrInsert(tag);
            addStyleJoinHashtag(styleContentId, hashtagId);
        }
    }

    //특정 해쉬태그가 있는 스타일 가져오기
    public List<ContentStyleBean> getStyleByHashtag(String hashtag, int count, int offset) {
        return contentMapper.getStyleByHashtag(hashtag, count, offset);
    }

    public ContentStyleBean getStyleById(int styleId) {
        return contentMapper.getStyleById(styleId);
    }

    public void addStyleComment(ContentStyleComment styleComment) {
        contentMapper.addStyleComment(styleComment);
    }

    public List<ContentStyleComment> getStyleCommentById(int styleId) {
        return contentMapper.getStyleCommentByStyleId(styleId);
    }

    public List<ContentStyleBean> getStyleByUserId(int userId, int count, int offset) {
        return contentMapper.getStyleByUserId(userId, count, offset);
    }

    public List<ContentStyleComment> getStyleCommentByUserId(int userId, int count, int offset) {
        return contentMapper.getStyleCommentByUserId(userId, count, offset);
    }

    public boolean toggleStyleLike(int styleId, int userId) {
        if (contentMapper.hasLiked(styleId, userId)) {
            contentMapper.removeLike(styleId, userId);
            contentMapper.decreaseLikeCount(styleId);
            return false;  // 좋아요 취소
        } else {
            contentMapper.addLike(styleId, userId);
            contentMapper.increaseLikeCount(styleId);
            return true;   // 좋아요 추가
        }
    }

    public List<ContentStyleBean> getLikedStyles(int userId, int count, int offset) {
        return contentMapper.getLikedStylesByUser(userId, count, offset);
    }
}
