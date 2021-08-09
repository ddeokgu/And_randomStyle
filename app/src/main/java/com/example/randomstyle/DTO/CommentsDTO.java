package com.example.randomstyle.DTO;

import java.util.Date;

public class CommentsDTO {
    private int b_no;
    private int c_no;
    private String userid;
    private String comments;
    private String write_date;

    public int getB_no() {
        return b_no;
    }

    public void setB_no(int b_no) {
        this.b_no = b_no;
    }

    public int getC_no() {
        return c_no;
    }

    public void setC_no(int c_no) {
        this.c_no = c_no;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getWrite_date() {
        return write_date;
    }

    public void setWrite_date(String write_date) {
        this.write_date = write_date;
    }

    @Override
    public String toString() {
        return "CommentsDTO{" +
                "b_no=" + b_no +
                ", c_no=" + c_no +
                ", userid='" + userid + '\'' +
                ", comments='" + comments + '\'' +
                ", write_date=" + write_date +
                '}';
    }
}
