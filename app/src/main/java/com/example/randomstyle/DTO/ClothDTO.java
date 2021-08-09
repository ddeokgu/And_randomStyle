package com.example.randomstyle.DTO;

public class ClothDTO {

    private int no;
    private String userid;
    private String category;
    private String photo_url;

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    @Override
    public String toString() {
        return "ClothDTO{" +
                "no=" + no +
                ", userid='" + userid + '\'' +
                ", category='" + category + '\'' +
                ", photo_url='" + photo_url + '\'' +
                '}';
    }
}
