package hello.validation.web.validation;

import hello.validation.domain.item.Item;
import hello.validation.domain.item.ItemRepository;
import hello.validation.domain.item.SaveCheck;
import hello.validation.domain.item.UpdateCheck;
import hello.validation.form.ItemSaveForm;
import hello.validation.form.ItemUpdateForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/validation/v4/items")
@RequiredArgsConstructor
public class ValidationItemControllerV4 {

    private final ItemRepository itemRepository;

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v4/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v4/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v4/addForm";
    }

    @PostMapping("/add")
    public String addItem(@Validated @ModelAttribute("iem") ItemSaveForm itemSaveForm,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes) {
        // 특정 필드 예외가 아닌 전체 예외
        if (itemSaveForm.getPrice() != null && itemSaveForm.getQuantity() != null) {
            int resultPrice = itemSaveForm.getPrice() * itemSaveForm.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.reject("totalPriceMin", new Object[]{10000,
                        resultPrice}, null);
            } }

        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "validation/v4/addForm";
        }

        //성공 로직
        Item item = new Item(); item.setItemName(itemSaveForm.getItemName());
        item.setPrice(itemSaveForm.getPrice());
        item.setQuantity(itemSaveForm.getQuantity());

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v4/items/{itemId}";

    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v4/editForm";
    }

    public String edit(@PathVariable Long itemId,
                       @Validated @ModelAttribute("item") ItemUpdateForm itemUpdateForm,
                       BindingResult bindingResult) {
        //특정 필드 예외가 아닌 전체 예외
        if (itemUpdateForm.getPrice() != null && itemUpdateForm.getQuantity() != null) {
            int resultPrice = itemUpdateForm.getPrice() * itemUpdateForm.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }

        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "validation/v4/editForm";
        }

        Item itemParam = new Item();
        itemParam.setItemName(itemUpdateForm.getItemName());
        itemParam.setPrice(itemUpdateForm.getPrice());
        itemParam.setQuantity(itemUpdateForm.getQuantity());

        itemRepository.update(itemId, itemParam);
        return "redirect:/validation/v4/items/{itemId}";
    }
}
