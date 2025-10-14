package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.security.JwtAuthFilter;
import com.epam.rd.autocode.spring.project.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletResponse;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/clients")
@PreAuthorize("hasRole('CLIENT')")
public class ClientController {
    private static final Logger log = LoggerFactory.getLogger(ClientController.class);

    private final ClientService clientService;

    @GetMapping("/me")
    public String me(Model model, Principal principal) {
        var dto = clientService.getClientByEmail(principal.getName());
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", dto);
        }
        model.addAttribute("client", dto);
        log.info("client.profile.view user={}", principal.getName());
        return "client-profile";
    }

    @PostMapping("/me")
    public String updateMe(@ModelAttribute("form") @Valid ClientDTO form,
                           BindingResult br,
                           Principal principal,
                           RedirectAttributes ra) {
        log.info("client.profile.update.in user={} name='{}' balance='{}'",
            principal.getName(), form.getName(), form.getBalance());
        if (br.hasErrors()) {
            var fieldErrs = br.getFieldErrors().stream()
                .map(fe -> fe.getField() + "=" + fe.getRejectedValue() + " (" + String.join(",", fe.getCodes()) + ") " + fe.getDefaultMessage())
                .toList();
            var objErrs = br.getGlobalErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();
            log.warn("client.profile.update.validation user={} fieldErrors={} globalErrors={}",
                principal.getName(), fieldErrs, objErrs);
            ra.addFlashAttribute("org.springframework.validation.BindingResult.form", br);
            ra.addFlashAttribute("form", form);
            ra.addFlashAttribute("error", "Перевірте правильність заповнення форми");
            log.warn("client.profile.update.validation user={} errors={}", principal.getName(), br.getErrorCount());
            return "redirect:/clients/me";
        }
        form.setEmail(principal.getName());
        var updated = clientService.updateClientByEmail(principal.getName(), form);
        ra.addFlashAttribute("form", updated);
        ra.addFlashAttribute("success", "Дані оновлено");
        log.info("client.profile.update.ok user={} newName='{}' newBalance={}",
            principal.getName(), updated.getName(), updated.getBalance());
        return "redirect:/clients/me";
    }

    @PostMapping("/me/delete")
    public String deleteMe(Principal principal,
                           HttpServletResponse response,
                           RedirectAttributes ra) {
        clientService.deleteClientByEmail(principal.getName());
        response.addHeader("Set-Cookie",
            org.springframework.http.ResponseCookie.from(JwtAuthFilter.ACCESS_COOKIE, "")
                .httpOnly(true).secure(false).sameSite("Lax").path("/").maxAge(0).build().toString());
        ra.addFlashAttribute("info", "Акаунт видалено");
        log.warn("client.profile.deleted user={}", principal.getName());
        return "redirect:/login?deleted";
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/manage")
    public String manage(Model model) {
        model.addAttribute("clients", clientService.getAllClients());
        log.info("clients.manage.view");
        return "clients/manage";
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/{email}/block")
    public String block(@PathVariable String email, RedirectAttributes ra) {
        clientService.blockByEmail(email);
        log.warn("client.block email={}", email);
        ra.addFlashAttribute("success", "Клієнта заблоковано");
        return "redirect:/clients/manage";
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/{email}/unblock")
    public String unblock(@PathVariable String email, RedirectAttributes ra) {
        clientService.unblockByEmail(email);
        log.info("client.unblock email={}", email);
        ra.addFlashAttribute("success", "Клієнта розблоковано");
        return "redirect:/clients/manage";
    }
}
