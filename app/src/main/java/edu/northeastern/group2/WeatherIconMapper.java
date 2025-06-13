package edu.northeastern.group2;

import android.util.Log;

public class WeatherIconMapper {
    private static final String TAG = "WeatherIconMapper";

    public static String getEmojiByCode(int conditionCode) {
        // WeatherAPI condition codes to emoji mapping
        switch (conditionCode) {
            case 1000: return "☀️"; // Sunny/Clear
            case 1003: return "🌤️"; // Partly cloudy
            case 1006: return "☁️"; // Cloudy
            case 1009: return "☁️"; // Overcast
            case 1030: return "🌫️"; // Mist
            case 1063: return "🌦️"; // Patchy rain possible
            case 1066: return "🌨️"; // Patchy snow possible
            case 1069: return "🌨️"; // Patchy sleet possible
            case 1072: return "🌨️"; // Patchy freezing drizzle possible
            case 1087: return "⛈️"; // Thundery outbreaks possible
            case 1114: return "🌨️"; // Blowing snow
            case 1117: return "🌨️"; // Blizzard
            case 1135: return "🌫️"; // Fog
            case 1147: return "🌫️"; // Freezing fog
            case 1150: return "🌧️"; // Patchy light drizzle
            case 1153: return "🌧️"; // Light drizzle
            case 1168: return "🌧️"; // Freezing drizzle
            case 1171: return "🌧️"; // Heavy freezing drizzle
            case 1180: return "🌧️"; // Patchy light rain
            case 1183: return "🌧️"; // Light rain
            case 1186: return "🌧️"; // Moderate rain at times
            case 1189: return "🌧️"; // Moderate rain
            case 1192: return "🌧️"; // Heavy rain at times
            case 1195: return "🌧️"; // Heavy rain
            case 1198: return "🌧️"; // Light freezing rain
            case 1201: return "🌧️"; // Moderate or heavy freezing rain
            case 1204: return "🌨️"; // Light sleet
            case 1207: return "🌨️"; // Moderate or heavy sleet
            case 1210: return "🌨️"; // Patchy light snow
            case 1213: return "🌨️"; // Light snow
            case 1216: return "🌨️"; // Patchy moderate snow
            case 1219: return "🌨️"; // Moderate snow
            case 1222: return "🌨️"; // Patchy heavy snow
            case 1225: return "🌨️"; // Heavy snow
            case 1237: return "🌨️"; // Ice pellets
            case 1240: return "🌧️"; // Light rain shower
            case 1243: return "🌧️"; // Moderate or heavy rain shower
            case 1246: return "🌧️"; // Torrential rain shower
            case 1249: return "🌨️"; // Light sleet showers
            case 1252: return "🌨️"; // Moderate or heavy sleet showers
            case 1255: return "🌨️"; // Light snow showers
            case 1258: return "🌨️"; // Moderate or heavy snow showers
            case 1261: return "🌨️"; // Light showers of ice pellets
            case 1264: return "🌨️"; // Moderate or heavy showers of ice pellets
            case 1273: return "⛈️"; // Patchy light rain with thunder
            case 1276: return "⛈️"; // Moderate or heavy rain with thunder
            case 1279: return "⛈️"; // Patchy light snow with thunder
            case 1282: return "⛈️"; // Moderate or heavy snow with thunder
            default: 
                Log.w(TAG, "Unknown weather condition code: " + conditionCode);
                return "❓";
        }
    }
} 