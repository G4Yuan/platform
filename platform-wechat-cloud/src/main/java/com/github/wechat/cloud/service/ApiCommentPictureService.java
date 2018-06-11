package com.github.wechat.cloud.service;

import com.github.model.dao.wechat.ApiCommentPictureMapper;
import com.github.model.entity.wechat.CommentPictureVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public class ApiCommentPictureService {
    @Autowired
    private ApiCommentPictureMapper commentPictureDao;


    public CommentPictureVo queryObject(Integer id) {
        return commentPictureDao.queryObject(id);
    }


    public List<CommentPictureVo> queryList(Map<String, Object> map) {
        return commentPictureDao.queryList(map);
    }


    public int queryTotal(Map<String, Object> map) {
        return commentPictureDao.queryTotal(map);
    }

    public int save(CommentPictureVo comment) {
        return commentPictureDao.save(comment);
    }


    public void update(CommentPictureVo comment) {
        commentPictureDao.update(comment);
    }


    public void delete(Integer id) {
        commentPictureDao.delete(id);
    }


    public void deleteBatch(Integer[] ids) {
        commentPictureDao.deleteBatch(ids);
    }

}
