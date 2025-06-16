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

    public Integer getLatestShortformId() {
        return contentMapper.getLatestShortformId();
    }

    /**
     * 스타일을 저장하고, 바로 이어서 해시태그를 join 테이블에 연결해 줍니다.
     * ContentStyleBean#getId() 에는 addStyle() 호출 후 DB에서 자동 생성된 PK가 들어옵니다.
     */
    public void addStyle(ContentStyleBean contentStyleBean) {
        // 1) 스타일 본문 저장 (id 자동 채번)
        contentMapper.addStyle(contentStyleBean);

        // 2) 해시태그 목록이 있으면 join 테이블에 연결
        List<String> tags = contentStyleBean.getHashtags();
        if (tags != null) {
            int styleId = contentStyleBean.getId();
            for (String tag : tags) {
                // 해시태그가 없으면 먼저 삽입, 있으면 그 ID를 리턴
                Integer hashtagId = contentMapper.getHashtagIdByName(tag);
                if (hashtagId == null) {
                    contentMapper.addHashtag(tag);
                    hashtagId = contentMapper.getHashtagIdByName(tag);
                }
                // join 테이블에 스타일-해시태그 연결
                contentMapper.addStyleJoinHashTag(styleId, hashtagId);
            }
        }
    }

    public void addHashtag(String hashtag) {
        contentMapper.addHashtag(hashtag);
    }

    public Integer getHashtagIdByName(String hashtag) {
        return contentMapper.getHashtagIdByName(hashtag);
    }

    public void addStyleJoinHashtag(int contentId, int hashtagId) {
        contentMapper.addStyleJoinHashTag(contentId, hashtagId);
    }

    // 해시태그가 없으면 삽입 후 ID 반환, 있으면 기존 ID 반환
    public int getHashtagIdOrInsert(String hashtag) {
        Integer id = getHashtagIdByName(hashtag);
        if (id == null) {
            addHashtag(hashtag);
            id = contentMapper.getHashtagIdByName(hashtag);
        }
        return id;
    }

    //Style과 해쉬태그를 연결해주는 Join 태이블 만들어줌
    public void linkStyleWithHashtag(int styleContentId, List<String> hashtags) {
        for (String tag : hashtags) {
            int hashtagId = getHashtagIdOrInsert(tag);
            contentMapper.addStyleJoinHashTag(styleContentId, hashtagId);
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
        if (contentMapper.hasStyleLike(styleId, userId)) {
            contentMapper.removeStyleLike(styleId, userId);
            contentMapper.decreaseStyleLikeCount(styleId);
            return false;  // 좋아요 취소
        } else {
            contentMapper.addStyleLike(styleId, userId);
            contentMapper.increaseStyleLikeCount(styleId);
            return true;   // 좋아요 추가
        }
    }

    public List<ContentStyleBean> getLikedStyles(int userId, int count, int offset) {
        return contentMapper.getLikedStylesByUser(userId, count, offset);
    }

    public boolean toggleShortLike(int short_id, int user_id) {
        if (contentMapper.hasShortLike(short_id, user_id)) {
            contentMapper.removeShortLike(short_id, user_id);
            contentMapper.decreaseShortLikeCount(short_id);
            return false;  // 좋아요 취소
        } else {
            contentMapper.addShortLike(short_id, user_id);
            contentMapper.increaseShortLikeCount(short_id);
            return true;   // 좋아요 추가
        }

    }
    public int addStyleAndReturnId(ContentStyleBean styleBean) {
        // 1) 스타일 저장: @Options(useGeneratedKeys=true)로 styleBean.id에 PK가 채워집니다.
        contentMapper.addStyle(styleBean);
        // 2) 채워진 id를 꺼내서 리턴
        return styleBean.getId();
    }
    public int getStyleCountByUserId(int userId) {
        return contentMapper.countStyleByUserId(userId);
    }

    public int getStyleCommentCountByUserId(int userId) {
        return contentMapper.countStyleCommentByUserId(userId);
    }

    public ContentShortformBean getShortOneRandom(){
        return contentMapper.getRandomShort();
    }

    public List<ContentStyleBean> getRecentStyles(int page, int size) {
        int offset = (page - 1) * size;
        return contentMapper.getRecentStyles(size, offset);
    }

    public List<ContentStyleBean> getRecentStylesByOffset(int size, int offset) {
        return contentMapper.getRecentStyles(size, offset);
    }

    public void insertStyleProductTag(int style_id, int product_id) {
        contentMapper.insertStyleProductTag(style_id, product_id);
    }
}
