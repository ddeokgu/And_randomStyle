package com.example.randomstyle.DTO;

public class BoardDTO {
    private int no;
    private String userid;
    private String title;
    private String contents;
    private String likes;
    private String comments;
    private int views;
    private String photo1_url;
    private String photo2_url;

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public String getPhoto1_url() {
        return photo1_url;
    }

    public void setPhoto1_url(String photo1_url) {
        this.photo1_url = photo1_url;
    }

    public String getPhoto2_url() {
        return photo2_url;
    }

    public void setPhoto2_url(String photo2_url) {
        this.photo2_url = photo2_url;
    }

    @Override
    public String toString() {
        return "BoardDTO{" +
                "no=" + no +
                ", userid='" + userid + '\'' +
                ", title='" + title + '\'' +
                ", contents='" + contents + '\'' +
                ", likes='" + likes + '\'' +
                ", comments='" + comments + '\'' +
                ", views=" + views +
                ", photo1_url='" + photo1_url + '\'' +
                ", photo2_url='" + photo2_url + '\'' +
                '}';
    }
}
