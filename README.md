# Shopping Demo

## Contents
- Introduction
- Project Structure
- Precautions
- Kit Usage Description
- Required Environment
- Licensing and Terms

## 1. Introduction
This project contains the shopping demo app based on HMS Core Kits.

It shows how to use the HMS Core solution in Shopping app.

It provides the demo of the whole shopping process except payment.

It provide following capabilities with HMS Core solution:
- Show the Ads in the App.
- You can go to the offering page by searching name, scan the barcode/QR code or take a picture of the product.
- Show the video and 3D model of the product.
- Login with Huawei Account
- Order payment remind.
- Get user location or saved address for delivery information.


You can use the following barcode picture to test barcode buy function:

![Bar Code](barcode.gif)


## 2. Project Structure

- app/src/main/assets The product information, 3D model and other resources. In real case, you can download it from internet.
- app/src/main/java/com/huawei/industrydemo/shopping 
- app/src/main/java/com/huawei/industrydemo/shopping/constants      The constants used in the App Demo.
- app/src/main/java/com/huawei/industrydemo/shopping/base           The basic function for each activity and fragment.
- app/src/main/java/com/huawei/industrydemo/shopping/entity         The definition of each entity.
- app/src/main/java/com/huawei/industrydemo/shopping/fragment       The fragment for each activity.
- app/src/main/java/com/huawei/industrydemo/shopping/inteface       The call back interface.
- app/src/main/java/com/huawei/industrydemo/shopping/page           The activity for each page.
- app/src/main/java/com/huawei/industrydemo/shopping/push           The push message for order notification
- app/src/main/java/com/huawei/industrydemo/shopping/utils          The common tool function.
- app/src/main/java/com/huawei/industrydemo/shopping/view           The cutomized widget used in the demo



## 3. Precautions
The Shopping Demo is a Demo app which simulate a shopping process. There are limited products in the App. Some functions are unavailable or virtual service.

- Customer Service is unavailable.
- It is virtual payment in the App. There will be no deduction from anywhere.
- The product in the App is virtual. 
- The order you made in the App is virtual, there will not be product delivery.
- The Ads in the App is test Ads.
- The account you login is the Huawei Account on the phone. The App will get your avatar and account name. It is used to show in the App.


## 4. Kit Usage Description
- When opening the App, Ads kit will be called to show the splash Ads.
- When you log in your Huawei Account, the account kit will used.
- Scan kit is used to scan the barcode/QR Code to view the product.
- You can also take a photo to the physical goods and system will search for the similar item in the system. ML kit capability is used here.
- After you create a purchase order, a notification will be sent to you via push kit.
- When you view the product information, video kit is used to show the product video.
- Location kit is used to get your current location to get the district information.
- Identity kit is used to get configured address to make it fast for delivery address.
- Analytics Kit will collect the basic usage information to App developer to analyze the App usage statistics.
- Scene Kit can show the 3D model of the product.





## 5. Required Environment
- Android Studio: 3.6
- Android SDK: 19
- Gradle: 6.3



## 6. Licensing and Terms
HUAWEI Shopping Demo App uses the Apache 2.0 license.
