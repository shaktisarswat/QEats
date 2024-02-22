package com.javaproject.qeats.exchanges;

import com.javaproject.qeats.dto.Menu;
import jakarta.validation.constraints.NotNull;
public class GetMenuResponse {
    @NotNull
    Menu menu;

    public GetMenuResponse(Menu menu) {
        this.menu = menu;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }
}