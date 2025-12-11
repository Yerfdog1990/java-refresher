package com.cwz.controller;

import com.cwz.model.Notice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NoticeController {
    @GetMapping("/notices")
    public Notice getNotices(){
        return new Notice("Important Notice",
                "Bank Servers will be down between 12 am ans 2 am");
    }
}
