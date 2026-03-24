# 🌦️ Weather App

A weather application that retrieves and displays weather information using the [OpenWeather API](https://openweathermap.org/api).

Cities are fetched using predefined latitude and longitude coordinates and displayed in alphabetical order with detailed weather data.

[![Weather App Demo](https://img.youtube.com/vi/MNKRpudc2Cc/0.jpg)](https://youtube.com/shorts/MNKRpudc2Cc)

---

## ✅ Features

- 🌡️ Current, max and min temperature
- 💧 Humidity
- 🌬️ Pressure
- 🎨 Weather icons loaded from OpenWeather
- 🔄 Pull-to-refresh to update weather data
- 🏙️ Cities listed in alphabetical order (duplicates removed)
- 🔍 Search cities by name
- 📋 City detail screen with expanded info:
  - Feels like temperature
  - Wind speed and direction
  - Weather description
  - 5-day weather forecast

---

## 🚀 Getting Started

1. Clone the repository
2. Add your API key to the `local.properties` file:
```
   WEATHER_API_KEY=<your-api-key-here>
```
3. Build and run the project in Android Studio

---

## 🛠️ Built With

- Java
- Android Studio
- [OpenWeather API](https://openweathermap.org/api)
- Retrofit + Gson
- Glide
- Material Design 3
