package br.com.psoa.smbox.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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

    @Column(columnDefinition = "TEXT", name = "post_content")
    private String content;

    @ManyToMany
    @JoinTable(
        name = "post_category",
        joinColumns = @JoinColumn(name = "post_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();

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

    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    // Other methods
    
    @Override
    public String toString() {
        return "Post [id=" + id + ", date=" + date + ", subject=" + subject + ", content=" + content + "]";
    }
}
