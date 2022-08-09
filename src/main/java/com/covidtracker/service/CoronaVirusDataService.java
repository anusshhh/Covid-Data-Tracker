package com.covidtracker.service;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.covidtracker.models.LocationStats;

@Service
public class CoronaVirusDataService {
	private static String DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
	private List<LocationStats> newStats = new ArrayList<>();

	@PostConstruct
	@Scheduled(cron = "* * 1 * * *")
	public List<LocationStats> fetchVirusData() throws IOException, InterruptedException {
		List<LocationStats> newStats = new ArrayList<>();
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(DATA_URL)).build();
		HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
		StringReader csvReader = new StringReader(httpResponse.body());

		Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvReader);
		for (CSVRecord record : records) {
			LocationStats locationStat = new LocationStats();
			locationStat.setState((record.get("Province/State").equals(""))?"All":record.get("Province/State"));
			locationStat.setCountry(record.get("Country/Region"));
			int latestCases=Integer.parseInt(record.get(record.size() - 1));
			int previousCase=Integer.parseInt(record.get(record.size() - 2));
			locationStat.setIncreasedCases(latestCases-previousCase);
			locationStat.setLatestTotalCases(latestCases);
			newStats.add(locationStat);
		}
		this.newStats = newStats;
		return this.newStats;
	}
		public int getTotalCases() {
			return newStats.stream().mapToInt(stat->stat.getLatestTotalCases()).sum();
	}
		public int getTotalIncrease() {
			return newStats.stream().mapToInt(stat->stat.getIncreasedCases()).sum();
			
		}
	
	

}
