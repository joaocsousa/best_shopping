# BestShopping

<img src="http://www.tinycoolthings.com/best_shopping_big_logo.png" style="width: 100px;"/>

## Description

BestShopping is a project that intends to allow its users to compare prices between two major Portuguese hipermarkets, [Jumbo](http://www.jumbo.pt/) and [Continente](http://www.continente.pt/). This project involves a backend API and a frontend Android application.

### Backend API
To serve the Android application, two things were necessary:

* Product information (name, brand, price, etc.);
* The API to expose this information.

Neither Jumbo nor Continente expose an API that allows remote public access to their products and prices, therefore it was necessary to develop a mechanism to collect the necessary data. This was achieved by creating a web crawler in Python to browse through every product’s web page and parse it to gather every bit of information. With this information, a database was created and exposed through an API created using the Django framework.

### Android Application
The Android application was built in accordance to Google’s latest design patterns and recommendations. It requests information from the remote database using the created API through JSON requests and it also stores information in a local database that is populated as the user explores the application. When a new version of the remote data is available, the local database is disposed and a new database is created.

### Screenshots


