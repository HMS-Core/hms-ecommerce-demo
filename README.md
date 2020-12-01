English | [中文](README_ZH.md)
<div align="center">
<img src="https://github.com/HMS-Core/hms-ecommerce-demo/blob/main/images/logo.png" width=200>
</div>

# HMS E-commerce Demo

## Description
HMS Core Offers an innovative solution that assists e-commerce apps with quick customer acquisition and improved conversion rates, for sustainable business growth.

This project contains the shopping demo app based on HMS Core Kits. It shows how to use the HMS Core solution in shopping app. It provides the demo of the whole shopping process except payment.

## Demo Link
Demo website: https://developer.huawei.com/consumer/en/solution/hms/ecommerce

Firstly choose the area you are in, then you can download the Demo App from scanning the corresponding QR code.

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
Features of this application with HMS Core solution
- Product list page
- Product detail page
- Picture/video/3D product showcase
- Ads placement
- Barcode scanning-based search
- Photo scanning-based search
- One-tap login in/out
- Notifications for order/payment update
- User location/location filling
- Membership purchase
- System integrity check
- Comments, which can be translated in app
- AR Wearing
- Bank card recognition
- Image Super-Resolution
- Geo-fencing
- Voice Search


## Application Generation
**Environment Requirement**:

- Android Studio: 4.1
- Android SDK: 19
- Gradle: 6.3

**Run locally**
1. Clone the repository:
     
        git clone https://github.com/HMS-Core/hms-ecommerce-demo.git

You can also download the zip file containing the code in this page

2. Copy jks file and agconnect-services.json to app folder. About how to get these files, you can refer to [Configuring App Information in AppGallery Connect](https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides/config-agc-0000001050196065)

3. Update jks file information and application ID in app/build.gradle file.

4. Build the application from the command line:

        cd hms-ecommerce-demo
        gradle clean
        gradle build

## Kit Usage Description
- When opening the App, Ads kit will be called to show the splash Ads.

![AdsKit](images/kit-usage-gif-english/AdsKit.gif)

- When you log in your Huawei Account, the account kit will used.

![AccountKit](images/kit-usage-gif-english/AccountKit.gif)

- Scan kit is used to scan the barcode/QR Code to view the product.

![ScanKit](images/kit-usage-gif-english/ScanKit.gif)

You can use the following barcode picture to test barcode buy function:

![Bar Code](images/barcode.gif)

- You can also take a photo to the physical goods and system will search for the similar item in the system. ML kit capability is used here.

![MLKit](images/kit-usage-gif-english/PhotoShopping.gif)

- After you create a purchase order, a notification will be sent to you via push kit.

![PushKit](images/kit-usage-gif-english/PushKit.gif)![PushKit](images/kit-usage-gif-english/GroFence.gif)

- When you view the product information, video kit is used to show the product video.

![VideoKit](images/kit-usage-gif-english/VideoKit.gif)

- Location kit is used to get your current location to get the district information.
- Identity kit is used to get configured address to make it fast for delivery address.
- Analytics Kit will collect the basic usage information to App developer to analyze the App usage statistics.

- Scene Kit can show the 3D model of the product.

![SceneKit](images/kit-usage-gif-english/SceneKit.gif)


- IAP Kit can help user to subscribe the membership in App to enjoy membership discount.

![IapKit](images/kit-usage-gif-english/Iap.gif)

- Safety Detect can make the system integrity check to make sure the system environment is secure.

![SafetyDetect](images/kit-usage-gif-english/SafetyDetect.gif)

- You can use ML kit to make translation and image super-resolution. It can also recognize the bank card and recognize your voice to make voice search.

![MLKit](images/kit-usage-gif-english/Translation.gif)![MLKit](images/kit-usage-gif-english/BankCardRecognization.gif)![MLKit](images/kit-usage-gif-english/VoiceSearch.gif)

- AR kit can help you to try the goods virtually, like wearing the glasses on AR.

![AREngine](images/kit-usage-gif-english/AR.gif)


## Precautions
The Shopping Demo is a Demo app which simulate a shopping process. There are limited products in the App. Some functions are unavailable or virtual service.

- Customer Service is unavailable.
- It is virtual payment in the App. There will be no deduction from anywhere.
- The membership purchase in the App will make real deduction from your account. The deduction will not be refunded.
- After membership purchase, please cancel the subscription in the "Sub Mgmt". Otherwise it will make deduction in next cycle. After cancellation, the existing membership will continue until the end of the current cycle.
- The product in the App is virtual. 
- The order you made in the App is virtual, there will not be product delivery.
- The Ads in the App is test Ads.
- The account you login is the Huawei Account on the phone. The App will get your avatar and account name. It is used to show in the App.


## Participation
If you want to evaluate more about HMS Core, [HMSCore on Reddit](https://www.reddit.com/r/HuaweiDevelopers/) is for you to keep up with latest news about HMS Core, and to exchange insights with other developers.

If you have questions about how to use HMS samples, try the following options:
- [Stack Overflow](https://stackoverflow.com/questions/tagged/huawei-mobile-services) is the best place for any programming questions. Be sure to tag your question with 
`huawei-mobile-services`.
- [Huawei Developer Forum](https://forums.developer.huawei.com/forumPortal/en/home?fid=0101187876626530001) HMS Core Module is great for general questions, or seeking recommendations and opinions.

If you run into a bug in our samples, please submit an [issue](https://github.com/HMS-Core/hms-ecommerce-demo/issues) to the Repository. Even better you can submit a [Pull Request](https://github.com/HMS-Core/hms-ecommerce-demo/pulls) with a fix.

## Licensing and Terms
HMS Shopping Demo is licensed under the [Apache 2.0 license](https://github.com/HMS-Core/hms-ecommerce-demo/blob/main/LICENSE).

