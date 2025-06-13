# National Parks Explorer

A comprehensive Android application that allows users to explore national parks across the United States, featuring real-time weather information and detailed park information.

## Features

### 1. State-based Park Search
- Search for national parks by state code (e.g., CA, NY, TX)
- Real-time validation of state codes
- Dynamic loading animation during search
- Comprehensive list of parks with detailed information

### 2. Weather Integration
- Real-time weather data for each park
- Temperature display in Fahrenheit
- Dynamic weather icons based on current conditions

### 3. User Interface
- Clean and intuitive design
- Responsive layout for both portrait and landscape orientations
- Dynamic loading animations
- Error handling with user-friendly messages
- Keyboard management for better user experience

## Technical Implementation

### API Integration
- National Parks Service API for park data
- WeatherAPI for real-time weather information
- Custom HTTP client implementation without external libraries
- Proper error handling and retry mechanisms

### Code Architecture
- Modular design with separate components
- Utility classes for common functionality
- Efficient data management
- Proper thread handling for network operations

### UI Components
- RecyclerView for park listings
- Custom adapters for data display
- Dynamic layouts using ConstraintLayout
- Loading animations and progress indicators

## Group Members and Contributions

### Team Members
1. Chris Chen (GitHub: [chrischenlixing])
    - Implemented weather API integration
    - Created weather icon mapping system
    - Developed loading animations
    - Validation of the state code
    - Brought keyboard when parks found

2. Shuyue Zhang (GitHub: [Shuyue6481])
    - Design UI for park search screen (new Activity)
    - Include an EditText for inputting the state code (e.g., CA, UT)
    - Add a search button to trigger API calls
    - Use ConstraintLayout for responsive design in both orientations

3. Qi Zheng (GitHub: [zqiq536])
    -Create project and app icon
    -Setup MainActivity with group name
    -Add [At Your Service] button to bring up ServiceActivity

## Technical Requirements
- Android Studio Arctic Fox or later
- Minimum SDK: API 24 (Android 7.0)
- Target SDK: API 34 (Android 14)
- Java 8 or later

## Setup Instructions
1. Clone the repository
2. Open the project in Android Studio
3. Add your API keys in `local.properties`:
   ```
   npsApiKey=your_nps_api_key
   weatherApiKey=your_weather_api_key
   ```
4. Build and run the application

---
