package com.weather_json;

public class Weather {
	private String cityName, weather, temperature, pathImage; 
	
	public Weather() {
		
	}
	
	public Weather(String _cityName, String _weather, String _temperature, String _pathImage) {
		this.cityName = _cityName;
		this.weather = _weather;
		this.temperature = _temperature;
		this.pathImage = _pathImage;
	}
	
	public String getCityName() {
		return cityName;
	}
	
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	
	public String getWeather() {
		return weather;
	}
	
	public void setWeather(String weather) {
		this.weather = weather;
	}
	
	public String getTemperature() {
		return temperature;
	}
	
	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}
	
	public String getPathImage() {
		return pathImage;
	}
	
	public void setPathImage(String pathImage) {
		this.pathImage = pathImage;
	}
}
