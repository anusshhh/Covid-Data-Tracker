package com.covidtracker.controller;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.covidtracker.models.LocationStats;
import com.covidtracker.service.CoronaVirusDataService;

@Controller
public class HomeController {
	@Autowired
	CoronaVirusDataService coronaVirusDataService;
	@RequestMapping("/")
	public String home(Model m) {
		DecimalFormat formatter=new DecimalFormat("##,##,###");
		
		try {
			List<LocationStats> locationStats=coronaVirusDataService.fetchVirusData();
			int totalCases=coronaVirusDataService.getTotalCases();
			int totalIncrease=coronaVirusDataService.getTotalIncrease();
			m.addAttribute("locationStats",locationStats);
			m.addAttribute("totalCases",formatter.format(totalCases));
			m.addAttribute("totalIncreasedCases",formatter.format(totalIncrease));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return "home";
	}

}
