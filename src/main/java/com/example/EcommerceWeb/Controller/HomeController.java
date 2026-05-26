package com.example.EcommerceWeb.Controller;

import com.example.EcommerceWeb.DTO.HomeStatsDto;
import com.example.EcommerceWeb.Service.DashBoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.DatagramSocket;

@RestController
@RequestMapping("/api/home")
public class HomeController {
    @Autowired
    DashBoardService dashboardService;

    @GetMapping({"/home","/"})
    public String home(){
        return "Welcome to Shopping, Buy Whatever suits your Need";
    }

    @GetMapping("/stats")
    public ResponseEntity<HomeStatsDto> getStats() {
        return new ResponseEntity<>(dashboardService.getHomeStats(), HttpStatus.OK);
    }



}
