English | [中文](README_ZH.md)
<div align="center">
<img src="https://github.com/HMS-Core/hms-ecommerce-demo/blob/main/images/logo.png" width=250>
</div>

# HMS E-commerce Demo

## Overview
HMS Core provides a solution that assists e-commerce apps with quick customer acquisition and improved conversion rates, for sustainable business growth.

The sample project provides a shopping demo app with related HMS Core kits integrated, demonstrating how to use the HMS Core solution for shopping apps. The demo covers the complete shopping process other than payment.

## Download Link
[Demo website](https://developer.huawei.com/consumer/en/solution/hms/ecommerce?ha_source=hms7)

Scan the QR code for a specific region with your browser to try out the demo app.

<table><tr>
<td>
<img src="https://github.com/HMS-Core/hms-ecommerce-demo/blob/main/images/barcode/China.png" width="200">
<p>China</p>
</td>
<td>
<img src="https://github.com/HMS-Core/hms-ecommerce-demo/blob/main/images/barcode/Asia-Africa-and-Latin-America.png" width="200">
<p>Asia, Africa, and Latin America</p>
</td>
<td>
<img src="https://github.com/HMS-Core/hms-ecommerce-demo/blob/main/images/barcode/Europe.png" width=200>
<p>Europe</p>
</td>
<td>
<img src="https://github.com/HMS-Core/hms-ecommerce-demo/blob/main/images/barcode/Russia.png" width=200>
<p>Russia</p>
</td>
</tr></table>

## Features
Demo app screens and used HMS Core features are as follows:
- Product list screen
- Product details screen
- Favorite products and shopping cart
- Product display via picture, video, or 3D modeling
- Search by scanning barcode
- Product search via picture or voice
- QR code payment and generation
- One-tap sign-in and sign-out
- Product order reminder pushing
- User location pinpointing
- User address acquisition
- Membership service
- System integrity check
- User comment and automatic translation of comments
- AR fitting
- Bank card recognition
- Geofence
- Smart customer service bot
- Bonus point service
- Offline store guidance
- Preferential price marketing
- Product sharing


## App Generation
**Compilation environment**

- Android Studio: 4.1
- Android SDK: 19
- Gradle: 6.3

**Procedure**
1. Clone the code repository.
  
        git clone https://github.com/HMS-Core/hms-ecommerce-demo.git

You can also download the ZIP package that contains the project.

2. Copy your JKS file and the **agconnect-services.json** file to the **app** directory. For details about how to generate the JKS file and **agconnect-services.json** file, please refer to [Configuring App Information in AppGallery Connect](https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides/config-agc-0000001050196065?ha_source=hms7).

3. Update the JKS file information and app ID in the app-level **build.gradle** file.

4. Run the following command to compile the demo app:

        cd hms-ecommerce-demo
        gradle clean
        gradle build

## Used Kits
- Account Kit helps you sign in to the app with your HUAWEI ID.

![AccountKit](images/kit-usage-gif-english/AccountKit.gif)

- Scan Kit scans barcodes and QR codes for product viewing and payment, and generates QR codes.

![ScanKit](images/kit-usage-gif-english/ScanKit.gif)

You can use the following barcode to test the barcode-based purchase function.

![Bar Code](images/barcode.gif)

![ScanKit](images/kit-usage-gif-english/scan-for-payment.gif)

![ScanKit](images/kit-usage-gif-english/QR-Code-generation.gif)

- ML Kit searches for similar products based on the taken picture of a product. 

![MLKit](images/kit-usage-gif-english/PhotoShopping.gif)

- Push Kit receives relevant notifications after order placement. 

![PushKit](images/kit-usage-gif-english/PushKit.gif)

- Video Kit plays videos related to a product to show the product information.

![VideoKit](images/kit-usage-gif-english/VideoKit.gif)

- Location Kit obtains the current device location, and obtains the device region from the device location. In addition, Location Kit provides the geofence service, which can send messages to users when they enter or exit the specified region.

  ![PushKit](images/kit-usage-gif-english/GroFence.gif)


- Identity Kit obtains addresses configured by users to quickly fill in the package delivery address.
- Analytics Kit obtains basic usage information about the current app to analyze the app usage data for app optimization.

- Scene Kit displays products through 3D modeling.

![SceneKit](images/kit-usage-gif-english/SceneKit.gif)

- In-App Purchases bolsters the membership subscription for enjoying discounts for members.

- Safety Detect evaluates the system integrity during app startup to ensure running environment security.

![SafetyDetect](images/kit-usage-gif-english/SafetyDetect.gif)

- ML Kit provides services such comment translation, bank card recognition, and voice search.

![MLKit](images/kit-usage-gif-english/Translation.gif)![MLKit](images/kit-usage-gif-english/BankCardRecognization.gif)![MLKit](images/kit-usage-gif-english/VoiceSearch.gif)

- AR Engine provides the fitting services such as glasses fitting.

![AREngine](images/kit-usage-gif-english/AR.gif)

- Map Kit and Location Kit display the offline store information and generate the navigation route to the store.


![Map/location](images/kit-usage-gif-english/stores-around.gif)

- Search Kit provides the smart customer service bot that automatically replies to questions of users.


![Search](images/kit-usage-gif-english/SmartAssistantServices.gif)

- CaaS Engine allows users to share their screens during shopping through the sharing button on the product details screen.
- Crash Kit records the app crash information.
- Adding products to favorites: Users can add products to favorites and display them on the favorite product screen.
- Discount price: Both the original price and discount price are displayed for all products, and the discount price is highlighted. 
- New product: The new product is displayed on an independent screen, and a discount countdown is displayed for the discount activity. Users can subscribe to the product to receive push messages.
- Member bonus points: When a user purchases a product, bonus points are added to the user's account based on the product amount they bought.
- Product link sharing: When a user views a product, they can share the product as a link to their friends in social apps, including Alipay, WeChat, and Facebook.


## Precautions
The shopping demo app simulates the real shopping process. Therefore, the quantity of products in the app is limited. Some functions are unavailable and some others are virtual.

- The product purchase function is virtual, and no fee is deducted.
- In the app, membership subscription will deduct fees from your account, which will not be refunded. 
- Once membership subscription has been done, you must manually cancel the subscription through the management screen to avoid recurring charges. When a user cancels a subscription they still have membership access until the end of the subscription period.
- All products in the demo app are virtual.
- All orders in the demo app are virtual, and no products will be delivered for such orders. 
- All ads in the demo app are for tests.
- This app allows users to sign in with their HUAWEI IDs. It obtains the user profile and user name from their HUAWEI ID for display in the app.


## Technical Support
You can visit the [Reddit community](https://www.reddit.com/r/HuaweiDevelopers/) to obtain the latest information about HMS Core and communicate with other developers.

If you have any questions about the sample code, try the following:
- Visit [Stack Overflow](https://stackoverflow.com/questions/tagged/huawei-mobile-services?tab=Votes), submit your questions, and tag them with `huawei-mobile-services`. Huawei experts will answer your questions.
- Visit the HMS Core section in the [HUAWEI Developer Forum](https://forums.developer.huawei.com/forumPortal/en/home?fid=0101187876626530001?ha_source=hms1) and communicate with other developers.

If you encounter any issues when using the sample code, submit your [issues](https://github.com/HMS-Core/hms-ecommerce-demo/issues) or submit a [pull request](https://github.com/HMS-Core/hms-ecommerce-demo/pulls).

## License
The sample code is licensed under [Apache License 2.0](https://github.com/HMS-Core/hms-ecommerce-demo/blob/main/LICENSE).
