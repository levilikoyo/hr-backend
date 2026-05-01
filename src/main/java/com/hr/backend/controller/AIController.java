/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.controller;

/**
 *
 * @author apple
 */


import org.springframework.web.bind.annotation.*;
import com.hr.backend.service.OpenAIService;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    private OpenAIService ai = new OpenAIService();

    @PostMapping("/ask")
    public String ask(@RequestBody String question) {

        return ai.askAI(question);
    }
}
