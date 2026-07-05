package br.com.psoa.smbox;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import br.com.psoa.smbox.model.Category;

@Controller
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping("/categories")
    public String getAllCategories(Model model) {
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
        return "categories/list";
    }

    @GetMapping("/categories/{id}")
    public String showCategory(@PathVariable("id") Long id, Model model) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID: " + id));
        model.addAttribute("category", category);
        return "categories/show";
    }

    @GetMapping("/categories/add")
    public String showAddForm(Model model) {
        model.addAttribute("category", new Category());
        return "categories/add";
    }

    @GetMapping("/categories/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID: " + id));
        model.addAttribute("category", category);
        return "categories/edit";
    }

    @GetMapping("/categories/delete/{id}")
    @Transactional
    public String delete(@PathVariable("id") Long id) {
        categoryRepository.deleteById(id);
        return "redirect:/categories";
    }

    @PostMapping("/categories/add")
    public String addCategory(@ModelAttribute("category") Category category, BindingResult result) {
        if (result.hasErrors()) {
            return "categories/add";
        }
        categoryRepository.save(category);
        return "redirect:/categories";
    }

    @PostMapping("/categories/edit")
    public String editCategory(@ModelAttribute("category") Category category, BindingResult result) {
        if (result.hasErrors()) {
            return "categories/edit";
        }
        categoryRepository.save(category);
        return "redirect:/categories";
    }
}
