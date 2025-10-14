package com.epam.rd.autocode.spring.project.controller;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PageControllerTest {

    @Test
    void loginPage_returnsLoginView() {
        PageController controller = new PageController();
        assertThat(controller.loginPage()).isEqualTo("login");
    }
}
