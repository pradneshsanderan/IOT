# Human Activity Recognition and Monitoring System

## Overview

This repository hosts the implementation of an end-to-end **Human Activity Recognition (HAR)** system that classifies and monitors physical activities in real-time. The system integrates wearable sensors, machine learning models, and a mobile application to deliver accurate activity classification and step tracking.

The project is designed for applications in healthcare and fitness, providing users with insights into their physical activities and allowing healthcare professionals to monitor patient activity levels effectively.

---

## Features

- **Real-Time Activity Recognition**: Classifies up to 14 physical activities using data from accelerometer and gyroscope sensors.
- **Step Counting**: Tracks user steps in real-time using a Fast Fourier Transform-based algorithm.
- **Mobile Application**: Lightweight Android app for real-time activity visualization, historical data tracking, and user authentication.
- **Cloud-Based Deployment**: Models and data are stored and processed in the cloud, ensuring minimal resource usage on the device.

---

## Methodology

### Data Collection
- **Sensors**: RESpeck and Thingy devices.
- **Data Captured**: Accelerometer and gyroscope readings sampled at 25 Hz.
- **Preprocessing**: Data segmented into 2-second frames for classification.

### Feature Extraction
- Manually extracted features: Mean, standard deviation, skewness, kurtosis, and more.
- Dimensionality reduction using Sparse Principal Component Analysis (SPCA).

### Machine Learning Models
- **Classifiers**: Random Forest (RF) and LightGBM for optimal accuracy.
- **Performance Metrics**: Accuracy, precision, recall, and F1 score.

### Step Counting
- FFT-based peak detection and low-pass filtering for accurate step counting.

### Mobile Application
- Developed using Firebase for real-time data storage and Google Cloud Platform for model deployment.
- Features include real-time visualization, historical tracking, and user authentication.

---

## Results

### Classification Performance
- **Offline Classification**:
  - RESpeck Sensor: 95%+ accuracy using SPCA → RF.
  - Thingy Sensor: 95%+ accuracy using SPCA → LightGBM.
- **Live Classification**: Real-time tests validated high accuracy for all activity classes.

### Application Performance
- Lightweight mobile app (~10.2 MB) with minimal resource usage.
- Efficient cloud integration for seamless data processing.

---

## Installation and Usage

### Prerequisites
- Android device with Bluetooth Low Energy (BLE) support.
- RESpeck and Thingy sensors.

### Setup
1. Clone this repository.
2. Install the mobile application APK on your Android device.
3. Connect the sensors to the mobile app via Bluetooth.
4. Use the app to monitor activities in real-time.

---

## Future Work
- Customizable calibration for personalized activity analysis.
- Offline data storage for areas with limited internet connectivity.

---

## Contributors
- **Passara Chanchotisatien**
- **Joseph Moncrieff**
- **Pradnesh Sanderan**

---

## License

This project is licensed under the MIT License.
