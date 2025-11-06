package br.com.psoa.smbox;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.SimpleDateFormat;

import br.com.psoa.smbox.model.Post;

@Controller
public class PostController {
    
    @Autowired
    private PostRepository postRepository;
    
    @GetMapping("/")
    public String index() {
        return "redirect:/posts";
    }    

    @GetMapping("/posts")
    public String getAllPosts(Model model, @RequestParam(required = false) String search) {
        List<Post> posts;
        if (search != null && !search.trim().isEmpty()) {
            posts = postRepository.findAll(Sort.by("date").descending()).stream()
                .filter(post -> 
                    post.getSubject().toLowerCase().contains(search.toLowerCase()) ||
                    post.getContent().toLowerCase().contains(search.toLowerCase()) ||
                    post.getDate().contains(search)
                )
                .toList();
        } else {
            posts = postRepository.findAll(Sort.by("date").descending());
        }
        model.addAttribute("posts", posts);
        model.addAttribute("search", search != null ? search : "");
        return "list";
    }
  
    @GetMapping("/{id}")
    public String showPost(@PathVariable("id") Long id, Model model) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID: " + id));
        model.addAttribute("post", post);
        return "show";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        var post = new Post();
        var date = new Date();

        var dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        var formattedDate = dateFormat.format(date);
        post.setDate(formattedDate);

        var subjectDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        var formattedSubjectDate = subjectDateFormat.format(date);
        post.setSubject("Brainstorm " + formattedSubjectDate);

        model.addAttribute("post", post);
        return "add";
    }
    
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
      Post post = postRepository.findById(id)
              .orElseThrow(() -> new IllegalArgumentException("Invalid post ID: " + id));
      model.addAttribute("post", post);
      return "edit";
    }

    @GetMapping("/delete/{id}")
    @Transactional
    public String delete(@PathVariable("id") Long id) {
      postRepository.deleteById(id);
      return "redirect:/posts";
    }


    @PostMapping("/add")
    public String addPost(@ModelAttribute("post") Post post, BindingResult result) {
      if (result.hasErrors()) {
        return "add";
      }
      postRepository.save(post);
      return "redirect:/posts";
    }

    @PostMapping("/edit")
    public String editPost(@ModelAttribute("post") Post post, BindingResult result) {
      if (result.hasErrors()) {
        return "edit";
      }
      postRepository.save(post);
      return "redirect:/posts";
    }
}
