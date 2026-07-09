package br.com.psoa.smbox;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import br.com.psoa.smbox.model.Category;

@ControllerAdvice
public class GlobalModelAttributes {

    @Autowired
    private CategoryRepository categoryRepository;

    @ModelAttribute("sidebarCategories")
    public List<Category> sidebarCategories() {
        return categoryRepository.findAll();
    }
}
