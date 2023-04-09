package br.com.psoa.smbox.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "post")
public class Post {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;
    
    @Column(name = "post_date")
    private String date;
    
    @Column(name = "post_subject")
    private String subject;
    
    @Column(name = "post_content")
    private String content;
    
    // Constructors, getters and setters, and other methods
    
    public Post() {}
    
    public Post(String date, String subject, String content) {
        this.date = date;
        this.subject = subject;
        this.content = content;
    }
    
    // Getters and setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getDate() {
        return date;
    }
    
    public void setDate(String date) {
        this.date = date;
    }
    
    public String getSubject() {
        return subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    // Other methods
    
    @Override
    public String toString() {
        return "Post [id=" + id + ", date=" + date + ", subject=" + subject + ", content=" + content + "]";
    }
}
