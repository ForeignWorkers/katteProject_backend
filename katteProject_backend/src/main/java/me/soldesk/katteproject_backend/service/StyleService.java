package me.soldesk.katteproject_backend.service;

import java.util.List;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;
import common.bean.content.ContentStyleBean;
import me.soldesk.katteproject_backend.mapper.StyleMapper;
@Service
public class StyleService {
    private final StyleMapper mapper;

    public StyleService(StyleMapper mapper) {
        this.mapper = mapper;
    }

    /** 전체 게시물 수 */
    public int getTotalCount() {
        return mapper.countAllStyles();
    }

    /** page(0부터) 와 size 만큼 게시물 조회 + 각 스타일별 이미지 URL 채우기 */
    public List<ContentStyleBean> getPage(int page, int size) {
        int offset = page * size;

        // 1) 스타일 정보만 먼저 조회 (RowBounds 로 페이징)
        List<ContentStyleBean> style = mapper.selectRecentStyles(new RowBounds(offset, size));

        // 2) 각 스타일별로 이미지 URL 리스트 조회해서 빈에 세팅
        for (ContentStyleBean styles : style) {
            List<String> urls = mapper.selectImageUrlsByStyleId(styles.getId());
            styles.setImageUrls(urls);
        }

        return style;
    }

    //등록한 스타일 갯수 반환
    public int countStyleByUserId(int user_id){ return mapper.countStyleByUserId(user_id);}

    //스타일 반환
    public List<ContentStyleBean> getStyleByUserId(int user_id, int page, int size){
        int offset = page * size;

        List<ContentStyleBean> style = mapper.selectStylesByUserId(user_id, new RowBounds(offset, size));

        for(ContentStyleBean styles : style){
            List<String> urls = mapper.selectImageUrlsByStyleId(styles.getId());
            styles.setImageUrls(urls);
        }

        return style;

    }

}
