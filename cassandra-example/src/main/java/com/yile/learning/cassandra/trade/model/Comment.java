package com.yile.learning.cassandra.trade.model;

import java.util.UUID;

/**
 * 用于保存产品的评论信息,使用TimeUUID作为ColumnFamily的key，实际存储数据如:
 * <p/>
 * <code>
 * Comment = { //Super ColumnFamily
 * 8177ec99-b9df-11df-94a6-1b9323c915f2:{
 * name:"9127ec99-b9df-11df-94a6-1b9323c915f2",
 * value {
 * {name:"content",value:"容易掉颜色",timestamp:123456789}
 * {name:"commentUserName",value:"lily",timestamp:123456789}
 * }
 * }
 * }
 * </code>
 *
 * @author justin.liang
 */
public class Comment {
    public static final String COLUMN_FAMILY = "Comment";

    private UUID uuid;

    private UUID name;

    private String content;

    private String commentUserName;

    public void setName(UUID name) {
        this.name = name;
    }

    public UUID getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCommentUserName() {
        return commentUserName;
    }

    public void setCommentUserName(String commentUserName) {
        this.commentUserName = commentUserName;
    }
}
